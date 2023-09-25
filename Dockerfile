FROM gradle:latest AS builder
WORKDIR /usr/app/
COPY . .
RUN gradle build --no-daemon -x test

FROM amazoncorretto:17-alpine3.18-jdk

WORKDIR /usr/app/

COPY --from=builder /usr/app/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD java -jar app.jar