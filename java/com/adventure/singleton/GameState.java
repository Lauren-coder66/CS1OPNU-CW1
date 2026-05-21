package com.adventure.singleton;

import com.adventure.model.Player;
import com.adventure.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton pattern: single global store for world and player data.
 */
public class GameState {
    private static GameState instance;

    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();
    private String activePlayer;
    private boolean gameOver;
    private String winMessage = "";

    private GameState() {
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    public void registerPlayer(Player player) {
        players.put(player.getName(), player);
        if (activePlayer == null) {
            activePlayer = player.getName();
        }
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(String activePlayer) {
        this.activePlayer = activePlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWinMessage() {
        return winMessage;
    }

    public void setWinMessage(String winMessage) {
        this.winMessage = winMessage;
    }

    public List<Player> playersInRoom(String roomId) {
        List<Player> result = new ArrayList<>();
        for (Player player : players.values()) {
            if (player.getCurrentRoomId().equals(roomId)) {
                result.add(player);
            }
        }
        return result;
    }
}
