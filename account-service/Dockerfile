FROM adoptopenjdk/openjdk11-openj9:alpine
RUN apk add --update \
    curl openssl tzdata \
    && rm -rf /var/cache/apk/*

RUN apk add fontconfig ttf-dejavu

COPY target/*.jar /app/account-service-application.jar
COPY src/main/resources/ /usr/app/config/

CMD java -jar /app/account-service-application.jar