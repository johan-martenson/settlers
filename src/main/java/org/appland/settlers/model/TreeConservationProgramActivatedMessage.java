package org.appland.settlers.model;

public class TreeConservationProgramActivatedMessage implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_ACTIVATED;
    }

    @Override
    public String toString() {
        return "Message: Tree conservation program is activated";
    }
}
