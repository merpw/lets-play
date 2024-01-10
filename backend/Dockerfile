FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME

WORKDIR /usr/app/

# Install dependencies
COPY build.gradle.kts settings.gradle.kts ./

COPY ./services/${SERVICE_NAME}/build.gradle.kts ./services/${SERVICE_NAME}/

COPY ./shared/build.gradle.kts ./shared/

RUN gradle services:${SERVICE_NAME}:dependencies --no-daemon

# Build the application
COPY . .
RUN gradle services:${SERVICE_NAME}:build --no-daemon -x test

FROM amazoncorretto:17-alpine3.18-jdk

ARG SERVICE_NAME

WORKDIR /usr/app/

COPY --from=builder /usr/app/services/${SERVICE_NAME}/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD java -jar app.jar