package eu.ditas.tub;

public class Main {

    public static void main(String[] args) throws Exception {
        IKeycloakAdmin admin = new KeycloakAdmin();
        APIController controller = new APIController(8000,admin);
        controller.init();
    }
}
