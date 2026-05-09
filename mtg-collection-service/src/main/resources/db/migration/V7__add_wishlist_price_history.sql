CREATE TABLE wishlist_card_price_history (
    id BIGSERIAL PRIMARY KEY,
    wishlist_card_id BIGINT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    record_date DATE NOT NULL,
    CONSTRAINT fk_wishlist_card_price FOREIGN KEY (wishlist_card_id) REFERENCES wishlist_card(id) ON DELETE CASCADE
);
