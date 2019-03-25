package eu.ditas.tub;

import eu.ditas.tub.model.BlueprintConfig;
import eu.ditas.tub.model.KeyCloakModel;

public interface IKeycloakAdmin {
    Object initizeRelam(BlueprintConfig config);

    void applyConfig(KeyCloakModel config);
}
