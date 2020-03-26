-- How to use it: awk 'FNR==NR{A[$1]=$2;next}{for(i in A)sub("\\"i,A[i], $0)}1' env.env setup_schema_user_permission.sql
-- env.env file format should be like object_store_test seqdb_test
-- Create Schema and Revoke privileges for PUBLIC user
REVOKE CONNECT ON DATABASE object_store_test FROM PUBLIC;
CREATE SCHEMA IF NOT EXISTS object_store;
REVOKE CREATE ON SCHEMA object_store FROM PUBLIC;

-- migration user
CREATE ROLE migration_user NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN PASSWORD 'test';
GRANT CONNECT ON DATABASE object_store_test TO migration_user;
GRANT USAGE, CREATE ON SCHEMA object_store TO migration_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA object_store GRANT ALL PRIVILEGES ON TABLES TO migration_user;

-- web user
CREATE ROLE web_user NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN PASSWORD 'test';
GRANT CONNECT ON DATABASE object_store_test TO web_user;
GRANT USAGE ON SCHEMA object_store TO web_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA object_store TO web_user;
ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA object_store GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO web_user;
ALTER DEFAULT PRIVILEGES FOR USER migration_user IN SCHEMA object_store GRANT SELECT, USAGE ON SEQUENCES TO web_user;
