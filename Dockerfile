FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/scala-3.3.0/gbank-assembly-0.1.0-SNAPSHOT.jar /opt/app
expose 8080
CMD ["java", "-jar", "/opt/app/gbank-assembly-0.1.0-SNAPSHOT.jar"]
