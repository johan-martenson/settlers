package org.appland.settlers.model.messages;

public class TreeConservationProgramDeactivatedMessage extends Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.TREE_CONSERVATION_PROGRAM_DEACTIVATED;
    }

    @Override
    public String toString() {
        return "Message: Tree conservation program is deactivated";
    }
}
