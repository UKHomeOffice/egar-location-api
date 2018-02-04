UPDATE location.location SET icao_code = LEFT(icao_code, 13);
ALTER TABLE location.location ALTER COLUMN icao_code type VARCHAR(13);

UPDATE location.location SET name = LEFT(name, 200);
ALTER TABLE location.location ALTER COLUMN name type VARCHAR(200);

UPDATE location.location SET post_code = LEFT(post_code, 13);
ALTER TABLE location.location ALTER COLUMN post_code type VARCHAR(13);
