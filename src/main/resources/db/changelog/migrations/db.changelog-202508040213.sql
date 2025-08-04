--liquibase formatted sql
--changeset junior:20250804
--comment: set unblockreason nullable

ALTER TABLE BLOCKS MODIFY COLUMN unblock_reason varchar(255) NULL;

--rollback ALTER TABLE BLOCKS MODIFY COLUMN unblock_reason VARCHAR(255) NOT NULL;