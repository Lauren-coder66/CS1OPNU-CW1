package com.adventure.factory;

import com.adventure.model.Item;
import com.adventure.model.NPC;
import com.adventure.model.Puzzle;
import com.adventure.model.Room;

/**
 * Factory pattern: create rooms, items, and NPCs from templates.
 */
public final class GameObjectFactory {
    private GameObjectFactory() {
    }

    public static Item createItem(String template) {
        if ("torch".equals(template)) {
            return new Item("torch", "A flickering torch that lights the darkness.");
        }
        if ("rusty_key".equals(template)) {
            return new Item("rusty key", "An old key covered in rust.");
        }
        if ("golden_artifact".equals(template)) {
            return new Item("golden artifact", "A radiant artifact — the ultimate prize.");
        }
        if ("note".equals(template)) {
            return new Item("note", "A note: 'The key unlocks the vault to the north.'");
        }
        throw new IllegalArgumentException("Unknown item template: " + template);
    }

    public static NPC createNpc(String template) {
        if ("guide".equals(template)) {
            return new NPC(
                    "Guide",
                    "A friendly explorer wearing a worn cloak.",
                    "Work together! Someone must bring the rusty key to the vault.");
        }
        if ("guardian".equals(template)) {
            return new NPC(
                    "Guardian",
                    "A stone guardian blocking suspicious travelers.",
                    "Only teamwork opens the final path. Share what you find.");
        }
        throw new IllegalArgumentException("Unknown NPC template: " + template);
    }

    public static Room createRoom(String template) {
        if ("entrance".equals(template)) {
            return buildEntrance();
        }
        if ("camp".equals(template)) {
            return buildCamp();
        }
        if ("hall".equals(template)) {
            return buildHall();
        }
        if ("library".equals(template)) {
            return buildLibrary();
        }
        if ("vault".equals(template)) {
            return buildVault();
        }
        if ("treasure".equals(template)) {
            return buildTreasure();
        }
        throw new IllegalArgumentException("Unknown room template: " + template);
    }

    private static Room buildEntrance() {
        Room room = new Room("entrance", "Forest Entrance",
                "You stand at a misty forest entrance. Paths lead east and south.");
        room.addExit("east", "hall");
        room.addExit("south", "camp");
        room.getItems().add(createItem("torch"));
        room.getNpcs().add(createNpc("guide"));
        return room;
    }

    private static Room buildCamp() {
        Room room = new Room("camp", "Abandoned Camp",
                "A deserted camp with scattered supplies. Exits: north, east.");
        room.addExit("north", "entrance");
        room.addExit("east", "library");
        room.getItems().add(createItem("note"));
        return room;
    }

    private static Room buildHall() {
        Room room = new Room("hall", "Stone Hall",
                "A drafty hall with ancient carvings. Exits: west, north.");
        room.addExit("west", "entrance");
        room.addExit("north", "library");
        return room;
    }

    private static Room buildLibrary() {
        Room room = new Room("library", "Hidden Library",
                "Dusty shelves line the walls. Exits: west, south, north.");
        room.addExit("west", "camp");
        room.addExit("south", "hall");
        room.addExit("north", "vault");
        room.getItems().add(createItem("rusty_key"));
        room.getNpcs().add(createNpc("guardian"));
        return room;
    }

    private static Room buildVault() {
        Room room = new Room("vault", "Sealed Vault",
                "A sealed vault door faces north. Something valuable awaits inside.");
        room.addExit("south", "library");
        room.addExit("north", "treasure");
        room.setPuzzle(new Puzzle("The vault door requires the rusty key.", "rusty key"));
        return room;
    }

    private static Room buildTreasure() {
        Room room = new Room("treasure", "Treasure Chamber",
                "Golden light fills the chamber. You found the heart of the dungeon!");
        room.addExit("south", "vault");
        room.getItems().add(createItem("golden_artifact"));
        return room;
    }
}
