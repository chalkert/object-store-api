# object-store-api
AAFC DINA object-store implementation.

See DINA object-store [specification](https://github.com/DINA-Web/object-store-specs).

# Database
This project requires a PostgreSQL database to run and to run integration tests.

# Minio
A [Minio](https://min.io/) service is also required to run the project (not required for testing).

# Testing
For testing purpose or local development a [Docker Compose](https://docs.docker.com/compose/) file can be used:

```
version: '3'
services:
  db:
    image: "postgres:9.6"
    container_name: "objectstore_test_postgres"
    environment:
      POSTGRES_DB: object_store_test
      POSTGRES_PASSWORD: mypassword
    volumes:
      - ./src/test/resources/create-test-users.sql:/docker-entrypoint-initdb.d/1-init-schema.sql
    ports:
      - "5432:5432"
      
  minio:
    image: minio/minio
    container_name: minio
    volumes:
       - ./minio-data:/data
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    ports:
      - "9000:9000"
    command: server /data
```

To run the integration tests:

```
 mvn verify -Dspring.datasource.url=jdbc:postgresql://localhost/object_store_test -Dspring.datasource.username=test -Dspring.datasource.password=test
```

# docker experimental mode
create /etversion: '3'
services:
  db:
    image: "postgres:9.6"
    container_name: "objectstore_test_postgres"
    environment:
      POSTGRES_DB: object_store_test
      POSTGRES_PASSWORD: mypassword
    volumes:
      - ./src/test/resources/create-test-users.sql:/docker-entrypoint-initdb.d/1-init-schema.sql
    ports:
      - "5432:5432"
      
  minio:
    image: minio/minio
    container_name: minio
    volumes:
       - ./minio-data:/data
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    ports:
      - "9000:9000"
    command: server /datac/docker/daemon.json
add "debug:true"

{
  "debug": true,
  "tls": true,
  "tlscert": "/var/docker/server.pem",
  "tlskey": "/var/docker/serverkey.pem",
  "hosts": ["tcp://192.168.59.3:2376"],
  "log-level": "debug"
}
