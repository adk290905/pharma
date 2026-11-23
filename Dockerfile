FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package

FROM quay.io/wildfly/wildfly:34.0.1.Final-jdk21

COPY --from=build /app/target/projectPHARMA-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/app.war
COPY docker/postgresql-module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/module.xml
ADD --chown=jboss:0 https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/postgresql.jar
COPY docker/entrypoint.sh /opt/jboss/entrypoint.sh

USER root
RUN chown jboss:0 /opt/jboss/entrypoint.sh \
    && chmod 0555 /opt/jboss/entrypoint.sh
USER jboss

ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseContainerSupport"
EXPOSE 8080

ENTRYPOINT ["/opt/jboss/entrypoint.sh"]
