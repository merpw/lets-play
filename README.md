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

## Configuration

### Environment variables

| Name                   | Description                        | Default value     |
|------------------------|------------------------------------|-------------------|
| `MONGO_HOST`           | MongoDB host                       | `localhost`       |
| `ADMIN_ENABLED`        | Enable initial admin user creation | `true`            |
| `ADMIN_USERNAME`       | Initial admin user name            | `admin`           |
| `ADMIN_PASSWORD`       | Initial admin user password        | `AdminAdmin`      |
| `ADMIN_EMAIL`          | Initial admin user email           | `admin@localhost` |
| `JWT_SECRET`           | JWT secret key                     | `UnsafeSecret`    |
| `CORS_ALLOWED_ORIGINS` | _(optional)_ CORS allowed origins  |                   |
| `PASSWORD_SALT`        | _(optional)_ Password salt         |                   |

### SSL

SSL is handled by nginx, so you can configure it by editing [./nginx.conf](./nginx.conf).

### JWT

JWT is used for authentication. For security reasons, it's highly recommended to generate a custom secret key on
production:

```bash
bash ./gen_jwt.sh
```

## Testing

The database for tests is powered by [testcontainers](https://testcontainers.com/), so Docker is required to run tests.

```bash
./gradlew test
```

### Coverage

Coverage reports are generated automatically by [jacoco](https://www.eclemma.org/jacoco/) after each test run.

They're available in [./build/reports/jacoco/test/html](./build/reports/jacoco/test/html).