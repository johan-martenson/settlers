package org.appland.settlers.model;

import static org.appland.settlers.model.Message.MessageType.GAME_ENDED;

public class GameEndedMessage implements Message {

    private final Player winnner;

    public GameEndedMessage(Player winnner) {
        this.winnner = winnner;
    }

    @Override
    public MessageType getMessageType() {
        return GAME_ENDED;
    }

    public Player getWinner() {
        return winnner;
    }

    @Override
    public String toString() {
        return "Message: Game ended with " + winnner.getName() + " as winner";
    }
}
