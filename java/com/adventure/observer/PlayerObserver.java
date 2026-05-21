package com.adventure.observer;

import java.util.ArrayList;
import java.util.List;

public class PlayerObserver implements Observer {
    private final String playerName;
    private final List<String> messages = new ArrayList<>();

    public PlayerObserver(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void update(String message) {
        messages.add(message);
    }

    public List<String> drainMessages() {
        List<String> pending = new ArrayList<>(messages);
        messages.clear();
        return pending;
    }
}
