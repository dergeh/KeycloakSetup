package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.admin.client.Keycloak;

import java.util.List;

public class KeyCloakModel {

	private String url;
	private String password;
	private List<String> roles;
	private List<UserModel>users;
	private boolean registrationAllowed;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public boolean isRegistrationAllowed() {
		return registrationAllowed;
	}

	public void setRegistrationAllowed(boolean registrationAllowed) {
		this.registrationAllowed = registrationAllowed;
	}
}
