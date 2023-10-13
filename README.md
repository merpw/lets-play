# lets-play

The 01-edu lets-play task from the Java program. Solved Sep 2023.

## [Task description and audit questions](https://github.com/01-edu/public/tree/master/subjects/java/projects/lets-play)

### Demo [lets-play.mer.pw](https://lets-play.mer.pw)

## How to run?

### Docker compose

```bash
docker compose up
```

Server will be available on [localhost:8080](http://localhost:8080).

### Production

- Open `./prod` directory.
- Run `./gen_env.sh` to generate`.env` config file with ADMIN_PASSWORD and JWT_SECRET.
- Edit `.env` and `nginx.cong` files if needed.
- Run `docker compose up`.

### Native

JRE 17 and MongoDB is required.

> You can use dockerized MongoDB by calling `docker compose up db`

```bash
./gradlew bootRun
```

Server will be available on [localhost:8080](http://localhost:8080).

## API

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/16966820-8237010b-10e5-47b4-95ba-abb4587e9593?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D16966820-8237010b-10e5-47b4-95ba-abb4587e9593%26entityType%3Dcollection%26workspaceId%3D8e6f6f99-c3c2-4738-b609-a958ed3a626a#?env%5BLETS-PLAY%5D=W3sia2V5IjoiVE9LRU4iLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWUsInR5cGUiOiJzZWNyZXQiLCJzZXNzaW9uVmFsdWUiOiJleUpoYkdjaU9pSklVekkxTmlKOS5leUpwYzNNaU9pSnpaV3htSWl3aWMzVmlJam9pTmpVeU9USmxORGxqT1RabE1HTXhZbVExTXpreE16TTJJaXdpWlhod0lqb3hOamszTWpNNU5qUTBMQ0pwWVhRaU9qRTJPVGN5TURNMk5EUXNJbk5qYjNCbC4uLiIsInNlc3Npb25JbmRleCI6MH0seyJrZXkiOiJCQVNFX1VSTCIsInZhbHVlIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZW5hYmxlZCI6dHJ1ZSwidHlwZSI6ImRlZmF1bHQiLCJzZXNzaW9uVmFsdWUiOiJodHRwczovL2xldHMtcGxheS5tZXIucHciLCJzZXNzaW9uSW5kZXgiOjF9XQ==)

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