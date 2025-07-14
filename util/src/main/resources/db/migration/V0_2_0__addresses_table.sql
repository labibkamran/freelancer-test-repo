CREATE TABLE IF NOT EXISTS addresses
(
    id                           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    country_iso_code             CHAR(2) NOT NULL,
    administrative_division_code TEXT,
    city                         TEXT    NOT NULL,
    postal_code                  TEXT,
    street_address_1             TEXT    NOT NULL,
    street_address_2             TEXT
);

CREATE INDEX idx_addresses_country_postal_city ON addresses (country_iso_code, postal_code, city);

-- add address to company
ALTER TABLE companies
    ADD IF NOT EXISTS address_id INTEGER,
    ADD CONSTRAINT companies_addresses_id_fk FOREIGN KEY (address_id) REFERENCES addresses (id);