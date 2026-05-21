package com.adventure.model;

public class NPC {
    private final String name;
    private final String description;
    private final String dialogue;

    public NPC(String name, String description, String dialogue) {
        this.name = name;
        this.description = description;
        this.dialogue = dialogue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDialogue() {
        return dialogue;
    }
}
