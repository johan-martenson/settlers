package org.appland.settlers.model.messages;

import org.appland.settlers.model.Player;

import java.util.Objects;

import static org.appland.settlers.model.messages.Message.MessageType.GAME_ENDED;

public final class GameEndedMessage extends Message {
    private final Player winner;

    public GameEndedMessage(Player winner) {
        this.winner = winner;
    }

    @Override
    public MessageType getMessageType() {
        return GAME_ENDED;
    }

    @Override
    public String toString() {
        return "Message: Game ended with " + winner.getName() + " as winner";
    }

    public Player winner() {
        return winner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GameEndedMessage) obj;
        return Objects.equals(this.winner, that.winner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner);
    }

}
