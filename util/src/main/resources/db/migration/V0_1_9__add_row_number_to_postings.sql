-- Add row_number column to postings table
ALTER TABLE postings
    ADD COLUMN row_number SMALLINT;