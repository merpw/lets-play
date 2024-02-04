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

### Environment variables

| Variable       | Description     | Default value   |
|----------------|-----------------|-----------------|
| JWT_SECRET     | JWT secret      | secret          |
| ADMIN_EMAIL    | Admin email     | admin@localhost |
| ADMIN_PASSWORD | Admin password  | AdminAdmin      |
| SERVER_NAME    | Server name     | localhost       |
| SSL_CERT       | SSL certificate |                 |
| SSL_KEY        | SSL key         |                 |

> **Note**: run `./gen_env.sh` to generate`.env` file with strong random values.

- Configure the project by editing `.env` file.

- Optional: add your SSL certificate and key to `SSL_CERT` and `SSL_KEY` environment variables in `.env` file.

- To run the latest stable version of the project, run:

`docker compose -f docker-compose.yml -f docker-compose.prod.yml up`

- The project is optimized to be deployed
  on [remote Docker hosts](https://www.docker.com/blog/how-to-deploy-on-remote-docker-hosts-with-docker-compose/), so
  you can specify `DOCKER_HOST` environment variable in `.env` file.