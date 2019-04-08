FROM golang:1.12 as ssl
WORKDIR /opt
RUN git clone https://github.com/tawalaya/go-acme-proxy.git
WORKDIR /opt/go-acme-proxy
RUN CGO_ENABLED=0 go build -a --installsuffix cgo --ldflags="-w -s -X main.Build=$(git rev-parse --short HEAD)" -o go-acme-proxy

FROM gradle:5.2-jdk8-alpine as build
USER root
COPY . .
RUN gradle distTar

FROM jboss/keycloak:4.8.3.Final
ARG KEYCLOAK_ADMIN_USER=admin
ARG KEYCLOAK_ADMIN_PASWORD=password

USER root
RUN yum install -y gettext
COPY --from=ssl --chown=jboss:jboss /opt/go-acme-proxy/go-acme-proxy /go-acme-proxy
RUN setcap CAP_NET_BIND_SERVICE=+eip /go-acme-proxy

USER jboss
COPY --chown=jboss:jboss --from=build /home/gradle/docker/certs/tsl.crt //etc/x509/https/tls.crt
COPY --chown=jboss:jboss --from=build /home/gradle/docker/certs/tsl.key //etc/x509/https/tls.key
COPY --from=build /home/gradle/docker/setup.sh /setup.sh
COPY --from=build /home/gradle/build/distributions/KeycloakAdminClient.tar /tmp/KeycloakAdminClient.tar
RUN tar -xvf /tmp/KeycloakAdminClient.tar 
COPY --from=build --chown=jboss:jboss /home/gradle/docker/Keycloak.json /opt/jboss/ditas/Keycloak.json.tmp
ENTRYPOINT [ "/setup.sh" ]
EXPOSE 443
EXPOSE 8000
EXPOSE 8443
CMD [ "-b", "0.0.0.0"]
