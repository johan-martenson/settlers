package org.appland.settlers.chat;

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
    private static final Map<String, Collection<ChatListener>> roomChatListeners = new HashMap<>();
    private static final Map<String, List<ChatMessage>> roomChatHistory = new HashMap<>();

    public static void addMessageListenerForRoom(String roomId, ChatListener chatListener) {
        if (!roomChatListeners.containsKey(roomId)) {
            roomChatListeners.put(roomId, new HashSet<>());
        }

        roomChatListeners.get(roomId).add(chatListener);
    }

    public static Collection<ChatMessage> getChatHistoryForRoom(String room) {
        return roomChatHistory.getOrDefault(room, List.of());
    }

    public record ChatMessage(Player from, String text, SimpleTime time) {}

    public interface ChatListener {
        void newMessageForPlayer(ChatMessage chatMessage, Player player);
        void newMessageForRoom(ChatMessage chatMessage, String roomId);
    }

    public static void sendChatToRoom(String room, String text, Player from) {
        var chatMessage = new ChatMessage(from, text, getTime());

        if (roomChatListeners.containsKey(room)) {
            roomChatListeners.get(room).forEach(listener -> listener.newMessageForRoom(
                    chatMessage,
                    room
            ));
        }

        if (!roomChatHistory.containsKey(room)) {
            roomChatHistory.put(room, new ArrayList<>());
        }

        roomChatHistory.get(room).add(chatMessage);
    }

    public static void addMessageListenerForPlayer(Player player, ChatListener chatListener) {
        playerChatListeners.put(player, chatListener);
    }

    public record SimpleTime (int hours, int minutes, int seconds) {}

    public List<ChatMessage> getHistoryForRoom(String room) {
        if (!roomChatHistory.containsKey(room)) {
            return List.of();
        }

        return roomChatHistory.get(room);
    }

    static SimpleTime getTime() {
        var calendar = Calendar.getInstance();

        return new SimpleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
