FROM openjdk:8-jre-alpine

ENV APP_HOME=/opt/app
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

COPY target/test-api-*.jar api.jar

EXPOSE 8081

ENTRYPOINT /usr/bin/java ${JAVA_OPTS} -jar api.jar