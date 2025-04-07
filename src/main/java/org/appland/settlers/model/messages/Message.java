package org.appland.settlers.model.messages;

public abstract class Message {
    public boolean isRead = false;

    public enum MessageType {
        MILITARY_BUILDING_READY,
        MILITARY_BUILDING_OCCUPIED,
        UNDER_ATTACK,
        GEOLOGIST_FIND,
        NO_MORE_RESOURCES,
        BUILDING_LOST,
        BUILDING_CAPTURED,
        STORE_HOUSE_IS_READY,
        TREE_CONSERVATION_PROGRAM_ACTIVATED,
        MILITARY_BUILDING_CAUSED_LOST_LAND,
        TREE_CONSERVATION_PROGRAM_DEACTIVATED,
        GAME_ENDED,
        BOMBARDED_BY_CATAPULT,
        SHIP_HAS_REACHED_DESTINATION,
        HARBOR_IS_FINISHED,
        SHIP_READY_FOR_EXPEDITION
    }

    public abstract MessageType getMessageType();

    public boolean isRead() {
        return isRead;
    }
}
