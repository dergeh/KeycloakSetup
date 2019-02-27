# KeycloakSetup

Using the Keycloak Java Client this project sets up a docker Keycloak instance based on the Configuration defined in the  Keycloak.json file.


## Usage
Build the Dockercontainer using `docker build .` and run the Dockercontainer using `docker run -p 8080:8080 $containername`. When the container starts the first Line displayed is the generated Password for the admin you need to save it to log into Keycloak.

## Config Fields
- `url`  --> the Url where Keycloak can be found
- `password` --> default gets generated when you run the container for the first time 
- `roles` --> define the roles that will be available in the created realm
- `users` --> Array of users you want in your realm you need to define `username` `password` and `realmRoles`
- `registrationAllowed` --> bool to indicate wether or not users will be able to register themselfes
