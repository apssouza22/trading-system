FROM adoptopenjdk/openjdk14:armv7l-ubuntu-jre-14.0.2_12

ENV JAVA_OPTS=""

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk/openjdk14:armv7l-ubuntu-jre-14.0.2_12
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

EXPOSE 8080

ENTRYPOINT exec java --enable-preview $JAVA_OPTS -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -jar app.jar
