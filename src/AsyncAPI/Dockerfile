FROM openjdk:8-jre-alpine

ENV APP_HOME=/opt/app
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

COPY target/playtrial-1.0-SNAPSHOT-dist.zip playtrial-1.0-SNAPSHOT-dist.zip
RUN unzip playtrial-1.0-SNAPSHOT-dist.zip -d $APP_HOME
RUN rm playtrial-1.0-SNAPSHOT-dist.zip

EXPOSE 9000

CMD java  -cp '/opt/app/playtrial-1.0-SNAPSHOT/lib/*' play.core.server.ProdServerStart $APP_HOME/playtrial-1.0-SNAPSHOT
