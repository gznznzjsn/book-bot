-- This migration file creates tables for each model defined in our application the rows of which match the model's parameters

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS book
(
    id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id BIGINT,
    title       text NOT NULL,
    author      text NOT NULL
);

CREATE TABLE IF NOT EXISTS "user"
(
    id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    telegram_id BIGINT UNIQUE
);