FROM adoptopenjdk/openjdk14:armv7l-ubuntu-jre-14.0.2_12

ENV JAVA_OPTS=""

EXPOSE 8080

ADD runner/target/*.jar app.jar

ENTRYPOINT exec java --enable-preview $JAVA_OPTS -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -jar app.jar
