--liquibase formatted sql

--changeset cheburek:007_create_addons_table
CREATE TABLE addons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true
);
