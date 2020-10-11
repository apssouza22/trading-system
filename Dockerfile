FROM adoptopenjdk/openjdk14:armv7l-ubuntu-jre-14.0.2_12 as builder

ARG JAR_FILE=runner/target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk/openjdk14:armv7l-ubuntu-jre-14.0.2_12
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

EXPOSE 8080
ENTRYPOINT ["java","--enable-preview","org.springframework.boot.loader.JarLauncher"]
