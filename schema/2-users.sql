GRANT USAGE ON SCHEMA objectstore TO brf;
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON ALL TABLES IN SCHEMA objectstore TO brf;
ALTER DEFAULT PRIVILEGES IN SCHEMA objectstore GRANT ALL PRIVILEGES ON TABLES TO brf;