--liquibase formatted sql

--changeset cheburek:008_create_menu_item_addons_table
CREATE TABLE menu_item_addons (
    menu_item_id BIGINT NOT NULL,
    addon_id BIGINT NOT NULL,
    PRIMARY KEY (menu_item_id, addon_id),
    CONSTRAINT fk_menu_item_addons_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
    CONSTRAINT fk_menu_item_addons_addon FOREIGN KEY (addon_id) REFERENCES addons(id)
);
