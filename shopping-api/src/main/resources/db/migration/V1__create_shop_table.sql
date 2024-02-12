CREATE SCHEMA IF NOT EXISTS shopping;

CREATE TABLE shopping.shop (
    id bigserial PRIMARY KEY,
    user_identifier VARCHAR(100) NOT NULL,
    date TIMESTAMP NOT NULL,
    total FLOAT NOT NULL
);

CREATE TABLE shopping.item (
    shop_id bigserial REFERENCES shopping.shop(id),
    product_identifier VARCHAR(100) NOT NULL,
    price FLOAT NOT NULL
);