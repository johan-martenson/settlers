package org.appland.settlers.model;

public class HarborIsFinishedMessage implements Message {

    private final Harbor harbor;

    public HarborIsFinishedMessage(Harbor harbor) {
        this.harbor = harbor;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.HARBOR_IS_FINISHED_MESSAGE;
    }

    public Harbor getHarbor() {
        return harbor;
    }
}
