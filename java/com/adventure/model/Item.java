package com.adventure.model;

public class Item {
    private final String name;
    private final String description;
    private final boolean portable;

    public Item(String name, String description, boolean portable) {
        this.name = name;
        this.description = description;
        this.portable = portable;
    }

    public Item(String name, String description) {
        this(name, description, true);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPortable() {
        return portable;
    }
}
