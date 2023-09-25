# lets-play

The 01-edu lets-play task from the Java program. Solved Sep 2023.

## [Task description and audit questions](https://github.com/01-edu/public/tree/master/subjects/java/projects/lets-play)

## How to run?

### Docker compose

```bash
docker compose up
```

Server will be available on [localhost:8080](http://localhost:8080).

### Native

JRE 17 and MongoDB is required.

> You can use dockerized MongoDB by calling `docker compose up db`

```bash
./gradlew bootRun
```

Server will be available on [localhost:8080](http://localhost:8080).

## Testing

The database for tests is powered by [testcontainers](https://testcontainers.com/), so Docker is required to run tests.

```bash
./gradlew test
```