package org.appland.settlers.computer;

import static org.appland.settlers.computer.Gabs.Mood;

public enum GamePlayEvent {

    // Game lifecycle
    STARTING_GAME(0, 10, Mood.CONFIDENT, 200),
    DISCOVERED_OTHER_PLAYER(300, 50, Mood.CURIOUS, 400),
    RESPOND_TO_OTHER_PLAYERS_CHAT(0, 100, Mood.CALM, 100),
    IDLE_CHAT(1000, 5, Mood.CALM, 200),

    // Economy
    PLANKS_AND_STONES_READY(300, 40, Mood.HAPPY, 300),
    FOUND_GOLD(300, 50, Mood.HAPPY, 400),
    DISCOVERED_NEW_MOUNTAIN_WITH_MINABLE_ORE(300, 40, Mood.CURIOUS, 400),
    DISCOVERED_SEA(300, 40, Mood.CURIOUS, 400),
    NO_MORE_TREES(500, 60, Mood.WORRIED, 500),
    NO_MORE_STONE(500, 60, Mood.WORRIED, 500),
    ECONOMY_STALLED(500, 70, Mood.WORRIED, 600),
    OUT_OF_PLANKS_EMERGENCY(500, 70, Mood.WORRIED, 600),

    // Expansion
    EXPANDED_TERRITORY(200, 50, Mood.HAPPY, 350),
    LOST_TERRITORY(200, 70, Mood.WORRIED, 500),
    LAUNCHED_EXPEDITION(200, 50, Mood.CURIOUS, 350),
    FOUNDED_NEW_TERRITORY(200, 70, Mood.HAPPY, 600),

    // Military
    BUILT_FORTRESS(200, 40, Mood.CONFIDENT, 300),
    ATTACKING(0, 90, Mood.AGGRESSIVE, 300),
    BEING_ATTACKED(0, 100, Mood.ANGRY, 500),
    WON_BATTLE(50, 80, Mood.HAPPY, 500),
    LOST_BATTLE(50, 90, Mood.WORRIED, 500),
    CAPTURED_BUILDING(100, 80, Mood.HAPPY, 500),
    PROMOTED_FIRST_GENERAL(0, 70, Mood.CONFIDENT, 500),

    // Personality
    DOMINATING(1000, 20, Mood.CONFIDENT, 1000),
    FALLING_BEHIND(1000, 20, Mood.WORRIED, 1000);

    private final int cooldown;
    private final int priority;
    private final Mood mood;
    private final int moodDuration;

    GamePlayEvent(int cooldown, int chatPriority, Mood mood, int moodDuration) {
        this.cooldown = cooldown;
        this.priority = chatPriority;
        this.mood = mood;
        this.moodDuration = moodDuration;
    }

    public int cooldown() {
        return cooldown;
    }

    public int priority() {
        return priority;
    }

    public Mood mood() {
        return mood;
    }

    public int moodDuration() {
        return moodDuration;
    }
}