package org.appland.settlers.model.messages;

import org.appland.settlers.model.Player;

import static org.appland.settlers.model.messages.Message.MessageType.GAME_ENDED;

public class GameEndedMessage implements Message {

    private final Player winner;

    public GameEndedMessage(Player winner) {
        this.winner = winner;
    }

    @Override
    public MessageType getMessageType() {
        return GAME_ENDED;
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "Message: Game ended with " + winner.getName() + " as winner";
    }
}
