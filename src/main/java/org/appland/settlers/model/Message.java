package org.appland.settlers.model;

public interface Message {

    enum MessageType {
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
        BOMBARDED_BY_CATAPULT
    }

    MessageType getMessageType();
}
