--liquibase formatted sql

--changeset cheburek:003_create_orders_table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL
);
