package org.appland.settlers.chat;

import org.appland.settlers.model.GameMap;
import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChatManager {
    private static final Map<Player, ChatListener> playerChatListeners = new HashMap<>();
    private static final Map<Player, List<ChatMessage>> playerChatHistory = new HashMap<>();

    private static final Map<GameMap, Collection<ChatListener>> gameChatListeners = new HashMap<>();
    private static final Map<GameMap, List<ChatMessage>> gameChatHistory = new HashMap<>();

    private static final Map<String, Collection<ChatListener>> roomChatListeners = new HashMap<>();
    private static final Map<String, List<ChatMessage>> roomChatHistory = new HashMap<>();

    public static void addMessageListenerForRoom(String roomId, ChatListener chatListener) {
        roomChatListeners
                .computeIfAbsent(roomId, id -> new HashSet<>())
                .add(chatListener);
    }

    public static Collection<ChatMessage> getChatHistoryForRoom(String room) {
        return roomChatHistory.getOrDefault(room, List.of());
    }

    public static void removeMessageListenerForRoom(String roomId, ChatListener chatListener) {
        var listeners = roomChatListeners.get(roomId);

        if (listeners != null) {
            listeners.remove(chatListener);

            if (listeners.isEmpty()) {
                roomChatListeners.remove(roomId);
            }
        }
    }

    public static void sendChatToGame(String message, Player from, GameMap game) {
        var chatMessage = new ChatMessage(from, message, getTime());

        System.out.println("Chat manager: new chat message for game: " + message);

        gameChatHistory
                .computeIfAbsent(game, g -> new ArrayList<>())
                .add(chatMessage);

        var listeners = gameChatListeners.get(game);

        if (listeners != null) {
            listeners.forEach(listener -> listener.newMessageForGame(chatMessage, game));
        }
    }

    public static void addMessageListenerForGame(GameMap game, ChatListener chatListener) {
        gameChatListeners
                .computeIfAbsent(game, g -> new HashSet<>())
                .add(chatListener);
    }

    public static void removeMessageListenerForGame(GameMap game, ChatListener chatListener) {
        var listeners = gameChatListeners.get(game);

        if (listeners != null) {
            listeners.remove(chatListener);

            if (listeners.isEmpty()) {
                gameChatListeners.remove(game);
            }
        }
    }

    public static Collection<ChatMessage> getChatHistoryForGame(GameMap game) {
        return gameChatHistory.getOrDefault(game, List.of());
    }

    public record ChatMessage(Player from, String text, SimpleTime time) {}

    public interface ChatListener {
        void newMessageForPlayer(ChatMessage chatMessage, Player player);
        void newMessageForRoom(ChatMessage chatMessage, String roomId);
        void newMessageForGame(ChatMessage chatMessage, GameMap game);
    }

    public static void sendChatToRoom(String room, String text, Player from) {
        var chatMessage = new ChatMessage(from, text, getTime());

        roomChatHistory
                .computeIfAbsent(room, r -> new ArrayList<>())
                .add(chatMessage);

        var listeners = roomChatListeners.get(room);

        if (listeners != null) {
            listeners.forEach(listener -> listener.newMessageForRoom(chatMessage, room));
        }
    }

    public static void sendChatToPlayer(Player to, String text, Player from) {
        var chatMessage = new ChatMessage(from, text, getTime());

        playerChatHistory
                .computeIfAbsent(to, p -> new ArrayList<>())
                .add(chatMessage);

        var listener = playerChatListeners.get(to);

        if (listener != null) {
            listener.newMessageForPlayer(chatMessage, to);
        }
    }

    public static void addMessageListenerForPlayer(Player player, ChatListener chatListener) {
        playerChatListeners.put(player, chatListener);
    }

    public static void removeMessageListenerForPlayer(Player player) {
        playerChatListeners.remove(player);
    }

    public static Collection<ChatMessage> getChatHistoryForPlayer(Player player) {
        return playerChatHistory.getOrDefault(player, List.of());
    }

    public record SimpleTime(int hours, int minutes, int seconds) {}

    public List<ChatMessage> getHistoryForRoom(String room) {
        if (!roomChatHistory.containsKey(room)) {
            return List.of();
        }

        return roomChatHistory.get(room);
    }

    static SimpleTime getTime() {
        var calendar = Calendar.getInstance();

        return new SimpleTime(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
    }
}