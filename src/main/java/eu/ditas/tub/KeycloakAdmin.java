package eu.ditas.tub;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ditas.tub.model.AdminConfig;
import eu.ditas.tub.model.BlueprintConfig;
import eu.ditas.tub.model.KeyCloakConfig;
import eu.ditas.tub.model.UserModel;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

public class KeycloakAdmin implements IKeycloakAdmin {

    private final static Logger logger = Logger.getLogger(KeycloakAdmin.class.getName());
    static String ADMIN_CONFIG_FILE = "/opt/jboss/ditas/Keycloak.json";
    static boolean DELETE_ON_READ = true;

    private final Keycloak client;

    public KeycloakAdmin() throws Exception {
        AdminConfig config = getKeyCloakConfig();
        client = getKeyCloakInstance(config.getUrl(), config.getPassword());
    }


    @Override
    public Object initizeRelam(BlueprintConfig config) {
        //TODO: create a serviceaccount for the relam
        //TODO: create a serviceaccount for ditas

        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(config.getClientId());
        clientRepresentation.setRedirectUris(Collections.singletonList(config.getDefaultRedirectUri()));
        clientRepresentation.setEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setImplicitFlowEnabled(true);
        clientRepresentation.setWebOrigins(null);
        clientRepresentation.setPublicClient(true);
        List<ClientRepresentation> clients = new LinkedList<>();
        clients.add(clientRepresentation);


        boolean realmExsists = false;
        for (RealmRepresentation representation : client.realms().findAll()) {
            if(representation.getRealm().matches(config.getBlueprintID())){
                realmExsists = true;
                break;
            }
        }



        if (!realmExsists){
            //construct the realm
            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm(config.getBlueprintID());
            realm.setRegistrationAllowed(config.isRegistrationAllowed());
            realm.setClients(clients);
            realm.setEnabled(true);

            client.realms().create(realm);
        }

        return new Object();
    }

    @Override
    public void applyConfig(KeyCloakConfig config) {
        RealmResource resource = client.realms().realm(config.getBlueprintID());

        //parse the roles for the realm and add them
        RolesRepresentation roles = new RolesRepresentation();
        roles.setRealm(createRoleRepresentation(config.getRoles()));
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRoles(roles);
        resource.update(realm);


        //add users to realm
        for (UserModel user : config.getUsers()) {
            UserRepresentation realmUser = parseUser(user);
            Response response = resource.users().create(realmUser);
            if(response.getStatus() == 409){
                //use already exsists - TODO: update instead ,skipping for now
                continue;
            }
            String userId = response.getLocation().getPath()
                    .replaceAll(".*/([^/]+)$", "$1");
            logger.info("id: " + userId);
            UserResource userResource = resource.users().get(userId);
            List<RoleRepresentation> realmRoles = new LinkedList<>();
            for (String role : user.getRealmRoles()) {
                realmRoles.add(resource.roles().get(role)
                        .toRepresentation());

            }
            logger.info(realmRoles);
            userResource.roles().realmLevel().add(realmRoles);

        }
    }

    @NotNull
    private Keycloak getKeyCloakInstance(String url, String password) {
        waitForKeycloak(url);
        return Keycloak.getInstance(url, "master", "admin",
                password, "admin-cli");
    }

    @NotNull
    private AdminConfig getKeyCloakConfig() throws Exception {
        AdminConfig model = null;
        File config = new File(ADMIN_CONFIG_FILE);
        ObjectMapper om = new ObjectMapper();
        try {
            model = om.readValue(config, AdminConfig.class);
        } catch (IOException e) {
            throw new Exception("Failed to read config from file ", e);
        }
        if (model == null) {
            throw new Exception("Config is empty");
        }
        if (DELETE_ON_READ) {
            config.delete();
        }
        logger.infof("read keycloak config %s",model);
        return model;
    }


    private List<RoleRepresentation> createRoleRepresentation(List<String> roles) {
        List<RoleRepresentation> retVal = new LinkedList<>();
        for (String s : roles) {
            retVal.add(new RoleRepresentation(s, s, false));
        }
        return retVal;
    }

    private void waitForKeycloak(String url) {
        int status = 0;
        try {
            while (status != 200) {
                try {
                    URL keycloak = new URL(url);
                    HttpURLConnection con =
                            (HttpURLConnection) keycloak.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();
                    status = con.getResponseCode();
                } catch (ConnectException e) {
                    logger.debug(
                            "Connection refused trying again to reach Keycloak");
                    sleep(1000);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private UserRepresentation parseUser(UserModel model) {
        //build Credentials representation
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(model.getPassword());

        //build the User Representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(model.getUsername());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(cred));
        user.setRealmRoles(model.getRealmRoles());

        return user;
    }

}
