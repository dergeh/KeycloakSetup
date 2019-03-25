package eu.ditas.tub.model;

import java.util.LinkedList;
import java.util.List;

public class KeyCloakConfig {

	private String blueprintID;

	private List<String> roles;
	private List<UserModel>users;

	public KeyCloakConfig() {
		roles = new LinkedList<>();
		users = new LinkedList<>();
	}


	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<UserModel> getUsers() {
		return users;
	}

	public void setUsers(List<UserModel> users) {
		this.users = users;
	}

	public String getBlueprintID() {
		return blueprintID;
	}

	public void setBlueprintID(String blueprintID) {
		this.blueprintID = blueprintID;
	}

}
