# backend

## How to run?

### Using docker compose (check main [README.md](../README.md) for more info).

### Native

JRE 17, Gradle and MongoDB is required.

> You can use dockerized MongoDB by calling `docker compose up db`

Run

```bash
gradle services:<name>:bootRun
```

E.g. to run products service:

```bash
gradle services:products:bootRun
```

Server will be available on [localhost:8080](http://localhost:8080/products).

## How to use?

There's a Swagger UI available for each microservice.

When running using Docker it's available at [/api/docs](http://localhost/api/docs). It contains docs for all services.

When running natively use [localhost:8080/swagger-ui](http://localhost:8080/swagger-ui). It contains docs only for running
service.

## Configuration

### Environment variables

#### Common (for all services)

| Name         | Description    | Default value  |
|--------------|----------------|----------------|
| `MONGO_HOST` | MongoDB host   | `localhost`    |
| `JWT_SECRET` | JWT secret key | `UnsafeSecret` |

#### Users service

| Name             | Description                        | Default value     |
|------------------|------------------------------------|-------------------|
| `ADMIN_ENABLED`  | Enable initial admin user creation | `true`            |
| `ADMIN_USERNAME` | Initial admin user name            | `admin`           |
| `ADMIN_PASSWORD` | Initial admin user password        | `AdminAdmin`      |
| `ADMIN_EMAIL`    | Initial admin user email           | `admin@localhost` |

### SSL

SSL is handled by nginx, so you can configure it by editing [./nginx.conf](./nginx.conf).

## Testing

The database for tests is powered by [testcontainers](https://testcontainers.com/), so Docker is required to run tests.

To run tests for all services:

```bash
gradle test
```

### Coverage

Coverage reports are generated automatically by [jacoco](https://www.eclemma.org/jacoco/) after each test run.

They're available in [./build/reports/jacoco/test/html](./build/reports/jacoco/test/html).