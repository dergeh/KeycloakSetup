package eu.ditas.tub.model;

public class BlueprintConfig {
    private String blueprintID;
    private String ClientId = "vdc_client";
    private String defaultRedirectUri = "http://localhost:9300";

    private boolean registrationAllowed;

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getDefaultRedirectUri() {
        return defaultRedirectUri;
    }

    public void setDefaultRedirectUri(String defaultRedirectUri) {
        this.defaultRedirectUri = defaultRedirectUri;
    }

    public String getBlueprintID() {
        return blueprintID;
    }

    public void setBlueprintID(String blueprintID) {
        this.blueprintID = blueprintID;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
    }

}
