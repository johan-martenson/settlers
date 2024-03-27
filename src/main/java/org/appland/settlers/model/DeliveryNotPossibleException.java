package org.appland.settlers.model;

import org.appland.settlers.model.buildings.Building;

public class DeliveryNotPossibleException extends InvalidGameLogicException {

    /**
     *
     */
    private static final long serialVersionUID = 6581138146376890558L;

    public DeliveryNotPossibleException(Building building, Cargo cargo) {
        super("Building " + building.getClass().getSimpleName() + " at " +
                building.getPosition() +
              " does not accept delivery of " + cargo.getMaterial());
    }
}
