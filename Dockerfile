FROM maven:3.6-jdk-8 as build
COPY . .
RUN mvn package


FROM jboss/keycloak:4.8.3.Final

ARG KEYCLOAK_ADMIN_USER=admin
ARG KEYCLOAK_ADMIN_PASWORD=password


USER root
RUN yum install -y gettext
USER jboss
COPY --from=build setup.sh /setup.sh
COPY --from=build target/KeycloakAdminClient-1.0-SNAPSHOT-jar-with-dependencies.jar KeycloakAdminClient.jar
COPY --from=build --chown=jboss:jboss Keycloak.json /opt/jboss/ditas/Keycloak.json.tmp
##RUN /opt/jboss/keycloak/bin/add-user-keycloak.sh -u $KEYCLOAK_ADMIN_USER -p $KEYCLOAK_ADMIN_PASWORD
#ADD $KEYCLOAK_IMPORT_REALM /opt/jboss/keycloak/
#ADD master-realm.json /opt/jboss/keycloak
ENTRYPOINT [ "/setup.sh" ]

CMD [ "-b", "0.0.0.0"]
