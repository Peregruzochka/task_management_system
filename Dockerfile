FROM openjdk:17-alpine AS build_project
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle.kts $APP_HOME
COPY settings.gradle.kts $APP_HOME
COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN ./gradlew dependencies

COPY . .
RUN ./gradlew build -x test

FROM openjdk:17-jdk-slim
ENV ARTIFACT_NAME=task_management_system-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=build_project $APP_HOME/build/libs/$ARTIFACT_NAME app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

