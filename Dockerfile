FROM jboss/keycloak:4.8.3.Final

ARG KEYCLOAK_ADMIN_USER=admin
ARG KEYCLOAK_ADMIN_PASWORD=password


USER root
RUN yum install -y gettext
USER jboss
ADD setup.sh /setup.sh
ADD KeycloakAdminClient-1.0-SNAPSHOT.jar .
COPY --chown=jboss:jboss Keycloak.json /opt/jboss/ditas/Keycloak.json
##RUN /opt/jboss/keycloak/bin/add-user-keycloak.sh -u $KEYCLOAK_ADMIN_USER -p $KEYCLOAK_ADMIN_PASWORD
#ADD $KEYCLOAK_IMPORT_REALM /opt/jboss/keycloak/
#ADD master-realm.json /opt/jboss/keycloak
ENTRYPOINT [ "/setup.sh" ]

CMD [ "-b", "0.0.0.0"]
