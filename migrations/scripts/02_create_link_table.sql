--liquibase formatted sql

--changeset kuzin:02_create_link_sequence
CREATE SEQUENCE IF NOT EXISTS link_id_seq;


--changeset kuzin:02_create_link_table
CREATE TABLE IF NOT EXISTS link
(
    id  BIGINT PRIMARY KEY DEFAULT nextval('link_id_seq'),
    url TEXT NOT NULL
);