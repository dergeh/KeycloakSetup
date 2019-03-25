package eu.ditas.tub;

import eu.ditas.tub.model.BlueprintConfig;
import eu.ditas.tub.model.KeyCloakConfig;

public interface IKeycloakAdmin {
    Object initizeRelam(BlueprintConfig config);

    void applyConfig(KeyCloakConfig config);
}
