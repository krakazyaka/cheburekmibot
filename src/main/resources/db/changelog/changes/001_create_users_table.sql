--liquibase formatted sql

--changeset cheburek:001_create_users_table
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    telegram_id VARCHAR(255) NOT NULL UNIQUE,
    user_code VARCHAR(4) NOT NULL UNIQUE,
    loyalty_points BIGINT,
    is_admin BOOLEAN NOT NULL DEFAULT false
);
