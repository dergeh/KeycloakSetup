#!/bin/sh
docker build -t ditas/keycloak:production .
docker stop --time 20 keycloak || true
docker rm --force keycloak || true
docker pull ditas/keycloak:production

# Get the host IP
HOST_IP="$(ip route get 8.8.8.8 | awk '{print $NF; exit}')"

# Run the docker mapping the ports and passing the host IP via the environmental variable "DOCKER_HOST_IP"
docker run -p 58080:8443 -p 58484:8080  -e KEYCLOAK_PRODUCTION=1 -e DOCKER_HOST_IP=$HOST_IP -e KEYCLOAK_PASSWORD="DuERFAyVfP6Wl8iYO1PgKUsIdL0lq7cI" --restart unless-stopped -d --name keycloak ditas/keycloak:production
