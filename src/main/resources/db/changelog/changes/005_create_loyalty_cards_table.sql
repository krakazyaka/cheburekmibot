--liquibase formatted sql

--changeset cheburek:005_create_loyalty_cards_table
CREATE TABLE loyalty_cards (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    current INT NOT NULL DEFAULT 0,
    target INT NOT NULL DEFAULT 10,
    free_available INT NOT NULL DEFAULT 0
);
