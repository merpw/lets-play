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

## API

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/16966820-8237010b-10e5-47b4-95ba-abb4587e9593?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D16966820-8237010b-10e5-47b4-95ba-abb4587e9593%26entityType%3Dcollection%26workspaceId%3D8e6f6f99-c3c2-4738-b609-a958ed3a626a#?env%5BLETS-PLAY%5D=W3sia2V5IjoiVE9LRU4iLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWUsInR5cGUiOiJzZWNyZXQiLCJzZXNzaW9uVmFsdWUiOiJleUpoYkdjaU9pSklVekkxTmlKOS5leUpwYzNNaU9pSnpaV3htSWl3aWMzVmlJam9pTmpVeU9USmxORGxqT1RabE1HTXhZbVExTXpreE16TTJJaXdpWlhod0lqb3hOamszTWpNNU5qUTBMQ0pwWVhRaU9qRTJPVGN5TURNMk5EUXNJbk5qYjNCbC4uLiIsInNlc3Npb25JbmRleCI6MH0seyJrZXkiOiJCQVNFX1VSTCIsInZhbHVlIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZW5hYmxlZCI6dHJ1ZSwidHlwZSI6ImRlZmF1bHQiLCJzZXNzaW9uVmFsdWUiOiJodHRwczovL2xldHMtcGxheS5tZXIucHciLCJzZXNzaW9uSW5kZXgiOjF9XQ==)

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