CREATE SCHEMA IF NOT EXISTS products;

CREATE TABLE products.category (
    id bigserial PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE products.product (
    id bigserial PRIMARY KEY,
    product_identifier VARCHAR NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(100) NOT NULL,
    preco FLOAT NOT NULL,
    category_id bigint REFERENCES products.category(id)
);

INSERT INTO products.category(id, nome) VALUES(1, 'eletronico');
INSERT INTO products.category(id, nome) VALUES(2, 'moveis');
INSERT INTO products.category(id, nome) VALUES(3, 'brinquedos');