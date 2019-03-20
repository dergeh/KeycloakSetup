import com.fasterxml.jackson.databind.ObjectMapper;
import model.KeyCloakModel;
import model.UserModel;
import org.jboss.logging.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

public class KeycloakAdmin {

	private static String realmName = "vdc_access";

	private static String clientId = "vdc_client";

	private static String redirectUri = "http://localhost:9300";

	private final static Logger logger = Logger.getLogger("KeycloakRESTAdmin");

	public static void main(String... args) {

		KeyCloakModel model = null;
		File config = new File("/opt/jboss/ditas/Keycloak.json");
		ObjectMapper om = new ObjectMapper();
		try {

			model = om.readValue(config, KeyCloakModel.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (model == null) {
			System.exit(-1);
		}
		config.delete();
		waitForKeycloak(model.getUrl());
		Keycloak kc = Keycloak.getInstance(model.getUrl(), "master", "admin",
				model.getPassword(), "admin-cli");

		//build a test client to get tokens
		ClientRepresentation client = new ClientRepresentation();
		client.setClientId(clientId);
		client.setRedirectUris(Arrays.asList(redirectUri));
		client.setEnabled(true);
		client.setDirectAccessGrantsEnabled(true);
		client.setImplicitFlowEnabled(true);
		client.setWebOrigins(null);
		client.setPublicClient(true);
		List<ClientRepresentation> clients = new LinkedList<>();
		clients.add(client);

		//parse the roles for the realm
		RolesRepresentation roles = new RolesRepresentation();
		roles.setRealm(parseRoles(model.getRoles()));

		//construct the realm
		RealmRepresentation realm = new RealmRepresentation();
		realm.setRoles(roles);
		realm.setRealm(realmName);
		realm.setRegistrationAllowed(model.isRegistrationAllowed());
		realm.setClients(clients);
		realm.setEnabled(true);
		kc.realms().create(realm);

		//add users to realm
		for (UserModel user : model.getUsers()) {
			UserRepresentation realmUser = parseUser(user);
			Response response = kc.realm(realmName).users().create(realmUser);
			//kc.realm(realmName).rolesById();
			String userId = response.getLocation().getPath()
					.replaceAll(".*/([^/]+)$", "$1");
			logger.info("id: " + userId);
			UserResource userResource = kc.realm(realmName).users().get(userId);
			List<RoleRepresentation> realmRoles = new LinkedList<>();
			for (String role : user.getRealmRoles()) {
				realmRoles.add(kc.realm(realmName).roles().get(role)
						.toRepresentation());

			}
			logger.info(realmRoles);
			userResource.roles().realmLevel().add(realmRoles);

		}

	}

	private static List<RoleRepresentation> parseRoles(List<String> roles) {
		List<RoleRepresentation> retVal = new LinkedList<>();
		for (String s : roles) {
			retVal.add(new RoleRepresentation(s, s, false));
		}
		return retVal;
	}

	private static void waitForKeycloak(String url) {
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}/**
		 try {
		 sleep(10000);
		 } catch (InterruptedException e) {
		 e.printStackTrace();
		 }*/
	}

	private static UserRepresentation parseUser(UserModel model) {
		//build Credentials representation
		CredentialRepresentation cred = new CredentialRepresentation();
		cred.setType(CredentialRepresentation.PASSWORD);
		cred.setValue(model.getPassword());

		//build the User Representation
		UserRepresentation user = new UserRepresentation();
		user.setUsername(model.getUsername());
		user.setEnabled(true);
		user.setCredentials(Arrays.asList(cred));
		user.setRealmRoles(model.getRealmRoles());

		return user;
	}

}
