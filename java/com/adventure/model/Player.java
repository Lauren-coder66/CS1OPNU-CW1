package com.adventure.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private String currentRoomId;
    private final List<Item> inventory = new ArrayList<>();
    private boolean hasWon;

    public Player(String name, String currentRoomId) {
        this.name = name;
        this.currentRoomId = currentRoomId;
        this.hasWon = false;
    }

    public String getName() {
        return name;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    public boolean hasItem(String itemName) {
        return inventory.stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase(itemName));
    }
}
