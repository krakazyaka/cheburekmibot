--liquibase formatted sql

--changeset cheburek:002_create_menu_items_table
CREATE TABLE menu_items (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image VARCHAR(255) NOT NULL,
    has_xl BOOLEAN NOT NULL DEFAULT false,
    available BOOLEAN NOT NULL DEFAULT true
);
