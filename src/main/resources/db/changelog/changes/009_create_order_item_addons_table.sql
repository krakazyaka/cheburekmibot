--liquibase formatted sql

--changeset cheburek:009_create_order_item_addons_table
CREATE TABLE order_item_addons (
    id BIGSERIAL PRIMARY KEY,
    order_item_id BIGINT NOT NULL,
    addon_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_item_addons_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id)
);
