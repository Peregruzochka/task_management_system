FROM openjdk:17-jdk-slim
LABEL authors="Peregruzochka"

WORKDIR /app


COPY gradle /gradle
COPY gradlew /gradlew
COPY build.gradle.kts /build.gradle.kts
COPY settings.gradle.kts /settings.gradle.kts


RUN ./gradlew build


COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
