ALTER TABLE wishlist_card ADD COLUMN condition VARCHAR(255) DEFAULT 'NM';
ALTER TABLE wishlist_card ADD COLUMN language VARCHAR(255) DEFAULT 'FR';
ALTER TABLE wishlist_card ADD COLUMN is_foil BOOLEAN DEFAULT FALSE;
ALTER TABLE wishlist_card ADD COLUMN max_price DECIMAL(19, 2);

-- Update existing rows if any
UPDATE wishlist_card SET condition = 'NM' WHERE condition IS NULL;
UPDATE wishlist_card SET language = 'FR' WHERE language IS NULL;
UPDATE wishlist_card SET is_foil = FALSE WHERE is_foil IS NULL;

-- Set NOT NULL after defaults
ALTER TABLE wishlist_card ALTER COLUMN condition SET NOT NULL;
ALTER TABLE wishlist_card ALTER COLUMN language SET NOT NULL;
