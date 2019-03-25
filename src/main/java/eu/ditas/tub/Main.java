package eu.ditas.tub;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length > 0){
            //Local Test Mode
            KeycloakAdmin.ADMIN_CONFIG_FILE = args[0];
            KeycloakAdmin.DELETE_ON_READ = false;
        }
        IKeycloakAdmin admin = new KeycloakAdmin();
        APIController controller = new APIController(8000,admin);
        controller.init();
    }
}
