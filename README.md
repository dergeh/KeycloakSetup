# KeycloakSetup

Using the Keycloak Java Client this project sets up a docker Keycloak instance based on the Configuration defined in the  Keycloak.json file.

### __DISCLAIMER__ ###
__!!! This is WIP the project is currently under development and not running due to problems with the JAR packaging !!!__

## Usage
Build the project using `mvn package`
Copy the build JAR from target to the top domain Folder. Run `docker build .` 
and run the Dockercontainer using `docker run $containername`

## Config Fields
- `url`  --> the Url where Keycloak can be found
- `password` --> gets generated when you run the container for the first time
- `roles` --> define the roles that will be available in the created realm
- `users` --> Array of users you want in your realm you need to define `username` `password` and `realmRoles`
- `registrationAllowed` --> bool to indicate wether or not users will be able to register themselfes
