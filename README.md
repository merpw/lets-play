# lets-play

The 01-edu lets-play task from the Java program. Solved Sep 2023.

## [Task description and audit questions](https://github.com/01-edu/public/tree/master/subjects/java/projects/lets-play)

### Demo [lets-play.mer.pw](https://lets-play.mer.pw/products)

## How to run?

### Docker compose

```bash
docker compose up
```

Server will be available on [localhost:80](http://localhost:80/products).

### Production

- Open `./prod` directory.
- Run `./gen_env.sh` to generate`.env` config file with ADMIN_PASSWORD and JWT_SECRET.
- Edit `.env` and `nginx.cong` files if needed.
- Run `docker compose up`.

### Native

For backend, check [./backend/README.md](./backend/README.md).