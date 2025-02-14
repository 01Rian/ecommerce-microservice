ALTER TABLE products.category ADD description VARCHAR(255);
ALTER TABLE products.category ADD created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products.category ADD updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE products.product ADD quantity INTEGER;
ALTER TABLE products.product ADD created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products.product ADD updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products.product ADD CONSTRAINT uk_product_identifier UNIQUE (product_identifier);

-- Atualiza as colunas created_at e updated_at para os registros existentes
UPDATE products.product SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP;