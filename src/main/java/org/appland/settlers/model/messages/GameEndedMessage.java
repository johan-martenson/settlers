package org.appland.settlers.model.messages;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.messages.Message.MessageType.GAME_ENDED;

public record GameEndedMessage(Player winner) implements Message {

    @Override
    public MessageType getMessageType() {
        return GAME_ENDED;
    }

    @Override
    public String toString() {
        return "Message: Game ended with " + winner.getName() + " as winner";
    }
}
