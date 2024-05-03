package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Harbor;

public record HarborIsFinishedMessage(Harbor harbor) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.HARBOR_IS_FINISHED_MESSAGE;
    }
}
