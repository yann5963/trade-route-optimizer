package com.example.mtg.market.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Component
public class CardmarketOAuthInterceptor implements ClientHttpRequestInterceptor {

    private final CardmarketProperties properties;

    public CardmarketOAuthInterceptor(CardmarketProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String url = request.getURI().toString();
        String method = request.getMethod().name();

        String authorizationHeader = generateAuthorizationHeader(method, url);
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return execution.execute(request, body);
    }

    private String generateAuthorizationHeader(String method, String url) {
        try {
            String nonce = UUID.randomUUID().toString().replace("-", "");
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            Map<String, String> oauthParams = new TreeMap<>();
            oauthParams.put("oauth_consumer_key", properties.getAppToken());
            oauthParams.put("oauth_token", properties.getAccessToken());
            oauthParams.put("oauth_signature_method", "HMAC-SHA1");
            oauthParams.put("oauth_timestamp", timestamp);
            oauthParams.put("oauth_nonce", nonce);
            oauthParams.put("oauth_version", "1.0");

            // Extract query parameters from URL if any, and add to params for signing
            String baseUrl = url;
            if (url.contains("?")) {
                String[] parts = url.split("\\?");
                baseUrl = parts[0];
                String[] queryParams = parts[1].split("&");
                for (String param : queryParams) {
                    String[] kv = param.split("=");
                    // decode so we don't double encode
                    String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
                    oauthParams.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name()), value);
                }
            }

            StringBuilder parameterString = new StringBuilder();
            for (Map.Entry<String, String> entry : oauthParams.entrySet()) {
                if (parameterString.length() > 0) {
                    parameterString.append("&");
                }
                parameterString.append(urlEncode(entry.getKey()))
                               .append("=")
                               .append(urlEncode(entry.getValue()));
            }

            String signatureBaseString = method.toUpperCase() + "&" +
                    urlEncode(baseUrl) + "&" +
                    urlEncode(parameterString.toString());

            String signingKey = urlEncode(properties.getAppSecret()) + "&" + urlEncode(properties.getAccessTokenSecret());

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] rawHmac = mac.doFinal(signatureBaseString.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(rawHmac);

            StringBuilder authHeader = new StringBuilder("OAuth realm=\"\",");
            authHeader.append("oauth_version=\"1.0\",");
            authHeader.append("oauth_timestamp=\"").append(timestamp).append("\",");
            authHeader.append("oauth_nonce=\"").append(nonce).append("\",");
            authHeader.append("oauth_consumer_key=\"").append(properties.getAppToken()).append("\",");
            authHeader.append("oauth_token=\"").append(properties.getAccessToken()).append("\",");
            authHeader.append("oauth_signature_method=\"HMAC-SHA1\",");
            authHeader.append("oauth_signature=\"").append(urlEncode(signature)).append("\"");

            return authHeader.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate OAuth signature", e);
        }
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            return value;
        }
    }
}
