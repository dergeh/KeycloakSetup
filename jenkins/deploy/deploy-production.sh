#!/usr/bin/env bash
# Production environment: 178.22.69.83
# Private key for ssh: /opt/keypairs/ditas-testbed-keypair.pem

ssh -i /opt/keypairs/ditas-testbed-keypair.pem cloudsigma@178.22.69.83 << 'ENDSSH'
# Ensure that a previously running instance is stopped (-f stops and removes in a single step)
# || true - "docker stop" fails with exit status 1 if image doen't exists, what makes the Pipeline fail. the "|| true" forces the command to exit with 0
# Try a graceful stop: 20 seconds for SIGTERM and SIGKILL after that
sudo docker stop --time 20 keycloak || true
sudo docker rm --force keycloak || true
sudo docker pull ditas/keycloak:production

# Get the host IP
HOST_IP="$(ip route get 8.8.8.8 | awk '{print $NF; exit}')"

# Run the docker mapping the ports and passing the host IP via the environmental variable "DOCKER_HOST_IP"
sudo docker run -p 58080:8443 -p 58000:443 -e DOCKER_HOST_IP=$HOST_IP  -v /opt/keycloak-db:/opt/jboss/keycloak/standalone/data --restart unless-stopped -d --name keycloak ditas/keycloak:production
ENDSSH