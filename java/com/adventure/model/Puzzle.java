package com.adventure.model;

public class Puzzle {
    private final String description;
    private final String requiredItem;
    private boolean solved;

    public Puzzle(String description, String requiredItem) {
        this.description = description;
        this.requiredItem = requiredItem;
        this.solved = false;
    }

    public String getDescription() {
        return description;
    }

    public String getRequiredItem() {
        return requiredItem;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
