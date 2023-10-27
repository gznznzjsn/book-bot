-- This migration file creates tables for each model defined in our application the rows of which match the model's parameters

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS book
(
    id     uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    title  text NOT NULL,
    author text NOT NULL
);