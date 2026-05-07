ALTER TABLE card ADD COLUMN last_deal_scan TIMESTAMP;

CREATE TABLE current_deals (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL REFERENCES card(id),
    seller_name VARCHAR(255) NOT NULL,
    seller_country VARCHAR(50) NOT NULL,
    condition VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL,
    is_foil BOOLEAN DEFAULT FALSE,
    price DECIMAL(10, 2) NOT NULL,
    trend_price DECIMAL(10, 2) NOT NULL,
    savings_percentage DECIMAL(5, 2) NOT NULL,
    detected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE'
);
