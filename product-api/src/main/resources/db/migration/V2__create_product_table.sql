CREATE TABLE products.product (
    id bigserial PRIMARY KEY,
    product_identifier VARCHAR NOT NULL,
    nome VARCHAR(100) NOT NULL,
    preco FLOAT NOT NULL,
    category_id bigint REFERENCES products.category(id)
);