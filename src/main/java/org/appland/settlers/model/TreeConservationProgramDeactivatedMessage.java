package org.appland.settlers.model;

public class TreeConservationProgramDeactivatedMessage implements Message {
    public TreeConservationProgramDeactivatedMessage() {
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_DEACTIVATED;
    }
}
