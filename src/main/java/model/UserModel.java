package model;

import java.util.List;

public class UserModel {

	private String password;

	private String username;

	private List<String> realmRoles;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRealmRoles() {
		return realmRoles;
	}

	public void setRealmRoles(List<String> realmRoles) {
		this.realmRoles = realmRoles;
	}
}
