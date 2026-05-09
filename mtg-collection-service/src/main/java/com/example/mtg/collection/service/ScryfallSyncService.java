package com.example.mtg.collection.service;

import com.example.mtg.collection.entity.MtgCardReference;
import com.example.mtg.collection.entity.MtgSet;
import com.example.mtg.collection.entity.SyncStatus;
import com.example.mtg.collection.repository.MtgCardReferenceRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.SyncStatusRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScryfallSyncService {

    private static final Logger log = LoggerFactory.getLogger(ScryfallSyncService.class);

    private final MtgSetRepository setRepository;
    private final MtgCardReferenceRepository cardReferenceRepository;
    private final SyncStatusRepository syncStatusRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ScryfallSyncService(MtgSetRepository setRepository,
                               MtgCardReferenceRepository cardReferenceRepository,
                               SyncStatusRepository syncStatusRepository) {
        this.setRepository = setRepository;
        this.cardReferenceRepository = cardReferenceRepository;
        this.syncStatusRepository = syncStatusRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public void syncAllReferences() {
        log.info("Starting full Scryfall synchronization...");
        try {
            // 1. Sync Sets
            log.info("Syncing MTG Sets...");
            ResponseEntity<String> setsResponse = restTemplate.getForEntity("https://api.scryfall.com/sets", String.class);
            JsonNode setsRoot = objectMapper.readTree(setsResponse.getBody());
            JsonNode dataNode = setsRoot.get("data");

            long setCount = 0;
            if (dataNode != null && dataNode.isArray()) {
                List<MtgSet> sets = new ArrayList<>();
                for (JsonNode setNode : dataNode) {
                    String id = setNode.get("id").asText();
                    String code = setNode.get("code").asText();
                    String name = setNode.get("name").asText();
                    String iconUri = setNode.has("icon_svg_uri") ? setNode.get("icon_svg_uri").asText() : null;
                    sets.add(new MtgSet(id, code, name, iconUri));
                }
                setRepository.deleteAllInBatch();
                setRepository.saveAll(sets);
                setCount = sets.size();
            }

            // 2. Get Bulk Data URI for default-cards
            log.info("Fetching bulk data URI for default-cards...");
            ResponseEntity<String> bulkDataResponse = restTemplate.getForEntity("https://api.scryfall.com/bulk-data/default-cards", String.class);
            JsonNode bulkRoot = objectMapper.readTree(bulkDataResponse.getBody());
            String downloadUri = bulkRoot.get("download_uri").asText();

            // 3. Stream download and parse JSON using Jackson Streaming API
            log.info("Downloading and parsing bulk data (this may take a few minutes)...");
            cardReferenceRepository.deleteAllInBatch();
            long cardCount = 0;

            JsonFactory factory = new JsonFactory();
            try (InputStream in = new URL(downloadUri).openStream();
                 JsonParser parser = factory.createParser(in)) {

                if (parser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected an array");
                }

                List<MtgCardReference> batch = new ArrayList<>();
                int batchSize = 5000;

                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    if (parser.currentToken() == JsonToken.START_OBJECT) {
                        String id = null, name = null, set = null;

                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            String fieldName = parser.getCurrentName();
                            parser.nextToken(); // move to value

                            if ("id".equals(fieldName)) {
                                id = parser.getValueAsString();
                            } else if ("name".equals(fieldName)) {
                                name = parser.getValueAsString();
                            } else if ("set".equals(fieldName)) {
                                set = parser.getValueAsString();
                            } else {
                                parser.skipChildren();
                            }
                        }

                        if (id != null && name != null && set != null) {
                            batch.add(new MtgCardReference(name, id, set));
                            cardCount++;

                            if (batch.size() >= batchSize) {
                                cardReferenceRepository.saveAll(batch);
                                cardReferenceRepository.flush();
                                entityManager.clear();
                                batch.clear();
                                log.info("Imported {} cards...", cardCount);
                            }
                        }
                    }
                }
                if (!batch.isEmpty()) {
                    cardReferenceRepository.saveAll(batch);
                }
            }

            // 4. Update Sync Status
            log.info("Sync complete! Imported {} sets and {} cards.", setCount, cardCount);
            SyncStatus status = syncStatusRepository.findTopByOrderByIdDesc().orElse(new SyncStatus());
            status.setLastSyncDate(LocalDateTime.now());
            status.setSetCount(setCount);
            status.setCardCount(cardCount);
            syncStatusRepository.save(status);

        } catch (Exception e) {
            log.error("Failed to sync from Scryfall", e);
            throw new RuntimeException("Failed to sync from Scryfall", e);
        }
    }
}
