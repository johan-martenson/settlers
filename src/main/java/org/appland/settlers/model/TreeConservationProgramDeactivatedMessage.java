package org.appland.settlers.model;

public class TreeConservationProgramDeactivatedMessage implements Message {
    private final Building building;

    public TreeConservationProgramDeactivatedMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_DEACTIVATED;
    }
}
