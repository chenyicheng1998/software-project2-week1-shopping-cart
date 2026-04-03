CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number INT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL
);

INSERT INTO localization_strings(`key`, value, language) VALUES
('app.title', 'Shopping Cart', 'en_US'),
('enter.num.items', 'Enter the number of items to purchase:', 'en_US'),
('enter.price', 'Enter the price for item', 'en_US'),
('enter.quantity', 'Enter the quantity for item', 'en_US'),
('add.item', 'Add item', 'en_US'),
('calculate.total', 'Calculate total', 'en_US'),
('total.cost', 'Total cost:', 'en_US'),
('result.log', 'Calculation result', 'en_US'),
('saved.db', 'Saved to database.', 'en_US');
