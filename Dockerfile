FROM amazoncorretto:24
COPY web-app/build/libs/web-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# https://openjdk.org/jeps/519 - compact object headers, experimental options can be removed with JDK 25
CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCompactObjectHeaders", "-jar", "/app.jar"]