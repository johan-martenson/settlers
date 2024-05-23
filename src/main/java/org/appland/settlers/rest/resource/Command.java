package org.appland.settlers.rest.resource;

public enum Command {
    PLACE_FLAG,
    PLACE_ROAD,
    PLACE_BUILDING,
    REMOVE_ROAD,
    REMOVE_FLAG,
    REMOVE_BUILDING,
    CALL_SCOUT,
    CALL_GEOLOGIST,
    FULL_SYNC,
    INFORMATION_ON_POINTS,
    SET_RESERVED_IN_HEADQUARTERS,
    START_DETAILED_MONITORING,
    STOP_DETAILED_MONITORING,
    REMOVE_MESSAGE,
    SET_COAL_QUOTAS,
    GET_COAL_QUOTAS,
    GET_FOOD_QUOTAS,
    SET_FOOD_QUOTAS,
    GET_WHEAT_QUOTAS,
    SET_WHEAT_QUOTAS,
    GET_WATER_QUOTAS,
    SET_WATER_QUOTAS,
    GET_IRON_BAR_QUOTAS,
    SET_IRON_BAR_QUOTAS,
    PAUSE_GAME,
    RESUME_GAME,
    SET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING,
    GET_STRENGTH_WHEN_POPULATING_MILITARY_BUILDING,
    GET_DEFENSE_STRENGTH,
    SET_DEFENSE_STRENGTH,
    GET_DEFENSE_FROM_SURROUNDING_BUILDINGS,
    SET_DEFENSE_FROM_SURROUNDING_BUILDINGS,
    SET_GAME_SPEED,
    SET_MILITARY_POPULATION_FAR_FROM_BORDER,
    SET_MILITARY_POPULATION_CLOSE_TO_BORDER,
    SET_MILITARY_POPULATION_CLOSER_TO_BORDER,
    GET_POPULATE_MILITARY_CLOSE_TO_BORDER,
    GET_POPULATE_MILITARY_CLOSER_TO_BORDER,
    GET_POPULATE_MILITARY_FAR_FROM_BORDER,
    GET_SOLDIERS_AVAILABLE_FOR_ATTACK,
    SET_SOLDIERS_AVAILABLE_FOR_ATTACK,
    REMOVE_MESSAGES, GET_MILITARY_SETTINGS, UPGRADE, FLAG_DEBUG_INFORMATION, PLACE_FLAG_AND_ROAD
}
