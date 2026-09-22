package org.appland.settlers.computer;

import org.appland.settlers.chat.ChatManager;
import org.appland.settlers.model.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class ChatPlayer implements GamePlayEventListener {
    private static final int GLOBAL_COOLDOWN = 50;
    private static final int MAX_RECENT_EVENTS = 3;
    private static final int MAX_RECENT_LINES = 5;

    private final Player player;
    private final Random random = new Random();
    private final EventTrigger eventTrigger;

    /** Last time any chat line was spoken. */
    private long lastChatTick = Long.MIN_VALUE;

    /** Last time each event was spoken. */
    private final Map<GamePlayEvent, Long> lastGamePlayEventTicks = new EnumMap<>(GamePlayEvent.class);

    /** Recently spoken events. */
    private final Deque<GamePlayEvent> recentGamePlayEvents = new ArrayDeque<>();

    /** Recently spoken chat lines. */
    private final Deque<String> recentLines = new ArrayDeque<>();

    /** Last event that resulted in a spoken line. */
    private GamePlayEvent lastGamePlayEvent;

    /** Current mood used for selecting chat lines. */
    private Gabs.Mood mood = Gabs.Mood.CALM;

    /** Strength of the current mood. */
    private int moodPriority;

    /** When the current mood expires. */
    private long moodUntil;

    public ChatPlayer(Player player, EventTrigger eventTrigger) {
        this.player = player;
        this.eventTrigger = eventTrigger;

        eventTrigger.addListener(this);
    }

    public void onEvent(GamePlayEvent gamePlayEvent) {
        expireMoodIfNecessary();

        if (!shouldSpeak(gamePlayEvent)) {
            return;
        }

        var lines = Gabs.forNation(player.getNation()).getOrDefault(gamePlayEvent, List.of());

        if (lines.isEmpty()) {
            return;
        }

        var candidates = lines.stream()
                .filter(line -> line.mood() == mood)
                .toList();

        if (candidates.isEmpty()) {
            candidates = lines.stream()
                    .filter(line -> line.mood() == gamePlayEvent.mood())
                    .toList();
        }

        if (candidates.isEmpty()) {
            candidates = lines;
        }

        var line = weightedRandom(candidates);

        if (line == null) {
            return;
        }

        ChatManager.sendChatToGame(
                "%s(%s - %s)".formatted(
                        line.text(),
                        gamePlayEvent.name().toLowerCase(),
                        mood.name().toLowerCase()
                ),
                player,
                player.getMap()
        );

        var now = player.getMap().getTime();

        lastChatTick = now;
        lastGamePlayEvent = gamePlayEvent;
        lastGamePlayEventTicks.put(gamePlayEvent, now);

        updateMood(gamePlayEvent);

        rememberGamePlayEvent(gamePlayEvent);
        rememberLine(line.text());
    }

    private boolean shouldSpeak(GamePlayEvent gamePlayEvent) {
        var now = player.getMap().getTime();

        // Don't repeat the same event over and over.
        if (recentGamePlayEvents.contains(gamePlayEvent)) {
            return false;
        }

        // Respect this event's own cooldown.
        var lastTime = lastGamePlayEventTicks.get(gamePlayEvent);

        if (lastTime != null && now - lastTime < gamePlayEvent.cooldown()) {
            return false;
        }

        // During the global cooldown, only higher-priority events may interrupt.
        if (lastGamePlayEvent != null &&
                now - lastChatTick < GLOBAL_COOLDOWN &&
                gamePlayEvent.priority() <= lastGamePlayEvent.priority()) {
            return false;
        }

        return true;
    }

    private void expireMoodIfNecessary() {
        if (player.getMap().getTime() < moodUntil) {
            return;
        }

        mood = Gabs.Mood.CALM;
        moodPriority = 0;
    }

    private void updateMood(GamePlayEvent event) {
        var now = player.getMap().getTime();

        // Existing mood has expired.
        if (now >= moodUntil) {
            moodPriority = 0;
        }

        // Stronger (or equally strong) events override the current mood.
        if (event.priority() >= moodPriority) {

            mood = event.mood();
            moodPriority = event.priority();
            moodUntil = now + event.moodDuration();
        }
    }

    private Gabs.ChatLine weightedRandom(List<Gabs.ChatLine> lines) {
        var candidates = lines.stream()
                .filter(line -> !recentLines.contains(line.text()))
                .toList();

        if (candidates.isEmpty()) {
            candidates = lines;
        }

        int totalWeight = candidates.stream()
                .mapToInt(Gabs.ChatLine::weight)
                .sum();

        int roll = random.nextInt(totalWeight);

        for (var line : candidates) {
            roll -= line.weight();

            if (roll < 0) {
                return line;
            }
        }

        return null;
    }

    private void rememberGamePlayEvent(GamePlayEvent gamePlayEvent) {
        recentGamePlayEvents.addLast(gamePlayEvent);

        while (recentGamePlayEvents.size() > MAX_RECENT_EVENTS) {
            recentGamePlayEvents.removeFirst();
        }
    }

    private void rememberLine(String line) {
        recentLines.addLast(line);

        while (recentLines.size() > MAX_RECENT_LINES) {
            recentLines.removeFirst();
        }
    }

    public Gabs.Mood getMood() {
        return mood;
    }
}