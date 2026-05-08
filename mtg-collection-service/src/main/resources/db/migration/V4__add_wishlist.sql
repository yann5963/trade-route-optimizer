CREATE TABLE wishlist_card (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL REFERENCES card(id),
    desired_quantity INTEGER DEFAULT 1
);
