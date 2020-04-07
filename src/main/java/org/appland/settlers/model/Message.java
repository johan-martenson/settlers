package org.appland.settlers.model;

public interface Message {

    // Missing: this building has caused you to lose land, we're being bombarded by a catapult, emergency production program activated/deactivated

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
        TREE_CONSERVATION_PROGRAM_DEACTIVATED
    }

    MessageType getMessageType();
}
