package org.appland.settlers.model;

public class InvalidStateForProduction extends InvalidGameLogicException {

    /**
     *
     */
    private static final long serialVersionUID = -4123948235819619294L;

    public InvalidStateForProduction(Building building) {
        super("Can not produce in building " + building);
    }
}
