--liquibase formatted sql

--changeset candidate:001-create-wallets
CREATE TABLE wallets (
    id uuid PRIMARY KEY,
    amount_in_rub NUMERIC(19, 2) NOT NULL DEFAULT 0
)