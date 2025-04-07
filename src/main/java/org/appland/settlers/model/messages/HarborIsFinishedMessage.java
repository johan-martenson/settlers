package org.appland.settlers.model.messages;

import org.appland.settlers.model.buildings.Harbor;

import java.util.Objects;

public final class HarborIsFinishedMessage extends Message {
    private final Harbor harbor;

    public HarborIsFinishedMessage(Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.HARBOR_IS_FINISHED;
    }

    public Harbor harbor() {
        return harbor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HarborIsFinishedMessage) obj;
        return Objects.equals(this.harbor, that.harbor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(harbor);
    }

    @Override
    public String toString() {
        return "HarborIsFinishedMessage[" +
                "harbor=" + harbor + ']';
    }

}
