package org.appland.settlers.model;

public class TreeConservationProgramActivatedMessage implements Message {
    private final Building building;

    public TreeConservationProgramActivatedMessage(Building building) {
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_ACTIVATED;
    }
}
