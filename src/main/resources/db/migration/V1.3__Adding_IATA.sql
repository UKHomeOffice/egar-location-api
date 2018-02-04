ALTER TABLE location.location DROP COLUMN name;
ALTER TABLE location.location DROP COLUMN post_code;

ALTER TABLE location.location ADD COLUMN iata_code VARCHAR(13);