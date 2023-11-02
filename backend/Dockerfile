FROM gradle:latest AS builder

ARG SERVICE_NAME

WORKDIR /usr/app/
COPY .. .
RUN gradle services:${SERVICE_NAME}:build --no-daemon -x test

FROM amazoncorretto:17-alpine3.18-jdk

ARG SERVICE_NAME

WORKDIR /usr/app/

COPY --from=builder /usr/app/services/${SERVICE_NAME}/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD java -jar app.jar