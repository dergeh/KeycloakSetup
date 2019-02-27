FROM gradle:5.2-jdk8-alpine as build
USER root
COPY . .
RUN gradle distTar


FROM jboss/keycloak:4.8.3.Final

ARG KEYCLOAK_ADMIN_USER=admin
ARG KEYCLOAK_ADMIN_PASWORD=password


USER root
RUN yum install -y gettext
USER jboss
COPY --from=build /home/gradle/setup.sh /setup.sh
COPY --from=build /home/gradle/build/distributions/KeycloakAdminClient.tar /tmp/KeycloakAdminClient.tar
RUN tar -xvf /tmp/KeycloakAdminClient.tar 
COPY --from=build --chown=jboss:jboss /home/gradle/Keycloak.json /opt/jboss/ditas/Keycloak.json.tmp
##RUN /opt/jboss/keycloak/bin/add-user-keycloak.sh -u $KEYCLOAK_ADMIN_USER -p $KEYCLOAK_ADMIN_PASWORD
#ADD $KEYCLOAK_IMPORT_REALM /opt/jboss/keycloak/
#ADD master-realm.json /opt/jboss/keycloak
ENTRYPOINT [ "/setup.sh" ]

CMD [ "-b", "0.0.0.0"]
