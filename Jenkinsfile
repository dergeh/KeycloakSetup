pipeline {
    // Mandatory to use per-stage agents
    agent none
    stages {
        //jvm tests a run during image creation, not nassesary to do it twise 
        stage('Staging image creation') {
            agent any
            steps {
                // The Dockerfile.artifact copies the code into the image and run the jar generation.
                echo 'Creating the image...'
                // This will search for a Dockerfile.artifact in the working directory and build the image to the local repository
                sh "docker build -t \"ditas/keycloak:staging\" -f Dockerfile ."
                echo "Done"
                echo 'Retrieving Docker Hub password from /opt/ditas-docker-hub.passwd...'
                // Get the password from a file. This reads the file from the host, not the container. Slaves already have the password in there.
                script {
                    password = readFile '/opt/ditas-docker-hub.passwd'
                }
                echo "Done"
                echo 'Login to Docker Hub as ditasgeneric...'
                sh "docker login -u ditasgeneric -p ${password}"
                echo "Done"
                echo "Pushing the image ditas/keycloak:staging"
                sh "docker push ditas/keycloak:staging"
                echo "Done "
            }
        }
        stage('Deployment in Staging') {
            agent any
            steps {
                sh './jenkins/deploy/deploy-staging.sh'
            }
        }
        //API test ommited as this is a already tested system (keycloak)
        stage('Production image creation') {
            agent any
            steps {
                sh "docker tag ditas/keycloak:staging ditas/keycloak:production"
                sh "docker push ditas/keycloak:production"
            }
        }
        stage('Deployment in Production') {
            agent any
            steps {
                sh './jenkins/deploy/deploy-production.sh'
            }
        }
    }
}
