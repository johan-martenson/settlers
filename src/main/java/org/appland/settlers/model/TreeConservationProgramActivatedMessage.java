package org.appland.settlers.model;

public class TreeConservationProgramActivatedMessage implements Message {
    public TreeConservationProgramActivatedMessage() {
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_ACTIVATED;
    }
}
