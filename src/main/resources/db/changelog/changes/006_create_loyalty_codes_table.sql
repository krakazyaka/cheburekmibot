--liquibase formatted sql

--changeset cheburek:006_create_loyalty_codes_table
CREATE TABLE loyalty_codes (
    id BIGINT PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    used BOOLEAN NOT NULL DEFAULT false,
    used_by VARCHAR(255),
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
