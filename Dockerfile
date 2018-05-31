FROM openjdk:8 AS BUILD_IMAGE

RUN mkdir /build
WORKDIR /build
COPY build.gradle gradlew /build/
COPY gradle /build/gradle
RUN ./gradlew build --continue

COPY src /build/src
COPY test /build/test
RUN ./gradlew build

FROM openjdk:8-jre-alpine

COPY --from=BUILD_IMAGE /build/build/libs/kt-webapp.jar /root/kt-webapp.jar
COPY static /root/static

WORKDIR /root
EXPOSE 8080

CMD ["java", "-server", "-Xms4g", "-Xmx4g", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "kt-webapp.jar"]