CREATE TABLE card (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    set_name VARCHAR(100) NOT NULL,
    rarity VARCHAR(50)
);

CREATE TABLE user_card (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL REFERENCES card(id),
    condition VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL,
    is_foil BOOLEAN DEFAULT FALSE,
    quantity INTEGER DEFAULT 1,
    purchase_price DECIMAL(10, 2)
);
