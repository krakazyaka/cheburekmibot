--liquibase formatted sql

--changeset cheburek:010_update_order_statuses
UPDATE orders SET status = 'CREATED' WHERE status = 'PENDING';
UPDATE orders SET status = 'IN_PROGRESS' WHERE status = 'PREPARING';
