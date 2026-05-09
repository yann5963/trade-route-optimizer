CREATE TABLE mtg_set (
    id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    icon_uri TEXT
);

CREATE TABLE mtg_card_reference (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    scryfall_id VARCHAR(50) NOT NULL UNIQUE,
    set_code VARCHAR(20) NOT NULL
);

CREATE TABLE sync_status (
    id BIGSERIAL PRIMARY KEY,
    last_sync_date TIMESTAMP,
    card_count BIGINT,
    set_count BIGINT
);
