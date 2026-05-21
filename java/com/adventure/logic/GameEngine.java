package com.adventure.logic;

import com.adventure.model.Item;
import com.adventure.model.NPC;
import com.adventure.model.Player;
import com.adventure.model.Puzzle;
import com.adventure.model.Room;
import com.adventure.observer.GameEventPublisher;
import com.adventure.observer.PlayerObserver;
import com.adventure.singleton.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameEngine {
    public static final String WIN_ITEM = "golden artifact";

    private final GameState state;
    private final GameEventPublisher events = new GameEventPublisher();
    private final Map<String, PlayerObserver> observers = new HashMap<>();

    public GameEngine() {
        this(GameState.getInstance());
    }

    public GameEngine(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public GameEventPublisher getEvents() {
        return events;
    }

    public Map<String, PlayerObserver> getObservers() {
        return observers;
    }

    public String addPlayer(String name) {
        return addPlayer(name, "entrance");
    }

    public String addPlayer(String name, String startRoom) {
        name = name.trim();
        if (name.isEmpty()) {
            return "Player name cannot be empty.";
        }
        if (state.getPlayers().containsKey(name)) {
            return "Player '" + name + "' already exists.";
        }
        Player player = new Player(name, startRoom);
        state.registerPlayer(player);
        PlayerObserver observer = new PlayerObserver(name);
        observers.put(name, observer);
        events.subscribe(observer);
        events.notify(name + " has joined the adventure.", observer);
        return "Welcome, " + name + "! Type 'help' for commands.";
    }

    public String switchPlayer(String name) {
        if (!state.getPlayers().containsKey(name)) {
            return "Unknown player: " + name;
        }
        state.setActivePlayer(name);
        return "Now controlling: " + name;
    }

    public String processCommand(String raw) {
        if (state.isGameOver()) {
            return "Game over. Restart the program to play again.";
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        String active = state.getActivePlayer();
        if (active == null) {
            return "No active player. Use 'join <name>' first.";
        }

        Player player = state.getPlayer(active);
        String[] parts = trimmed.split("\\s+");
        String cmd = parts[0].toLowerCase();
        List<String> args = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            args.add(parts[i]);
        }

        String result;
        if ("help".equals(cmd)) {
            result = cmdHelp();
        } else if ("look".equals(cmd)) {
            result = cmdLook(player);
        } else if ("go".equals(cmd)) {
            result = cmdGo(player, args);
        } else if ("take".equals(cmd)) {
            result = cmdTake(player, args);
        } else if ("drop".equals(cmd)) {
            result = cmdDrop(player, args);
        } else if ("inventory".equals(cmd) || "inv".equals(cmd)) {
            result = cmdInventory(player);
        } else if ("talk".equals(cmd)) {
            result = cmdTalk(player, args);
        } else if ("use".equals(cmd)) {
            result = cmdUse(player, args);
        } else if ("give".equals(cmd)) {
            result = cmdGive(player, args);
        } else if ("players".equals(cmd)) {
            result = cmdPlayers();
        } else if ("switch".equals(cmd)) {
            result = args.isEmpty() ? "Usage: switch <player>" : switchPlayer(args.get(0));
        } else {
            result = "Unknown command: " + cmd + ". Type 'help'.";
        }

        List<String> notifications = observers.get(active).drainMessages();
        if (!notifications.isEmpty()) {
            result += "\n--- Updates ---\n" + String.join("\n", notifications);
        }
        return result;
    }

    public String tryCollectWin(Player player) {
        Room room = requireRoom(player);
        if (room == null || !"treasure".equals(room.getId())) {
            return null;
        }
        Item artifact = findItemInList(room.getItems(), WIN_ITEM);
        if (artifact == null) {
            return null;
        }
        room.getItems().remove(artifact);
        player.getInventory().add(artifact);
        return triggerWin(player);
    }

    private String cmdHelp() {
        return "Commands:\n"
                + "  look              - describe current room\n"
                + "  go <direction>    - move (north/south/east/west)\n"
                + "  take <item>       - pick up an item\n"
                + "  drop <item>       - drop an item in the room\n"
                + "  inventory / inv   - list your items\n"
                + "  talk <npc>        - speak with an NPC\n"
                + "  use <item>        - use item (e.g. unlock vault)\n"
                + "  give <item> to <player> - transfer item to another player\n"
                + "  players           - list all players and locations\n"
                + "  switch <name>     - control another player\n"
                + "  join <name>       - add a new player\n"
                + "  quit              - exit game";
    }

    private String cmdLook(Player player) {
        Room room = requireRoom(player);
        if (room == null) {
            return "You are nowhere. Something is wrong.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(room.getName()).append("\n");
        sb.append(room.getDescription());
        if (room.isLocked()) {
            sb.append("\n[This area is locked.]");
        }
        if (!room.getItems().isEmpty()) {
            sb.append("\nItems: ").append(room.getItems().stream()
                    .map(Item::getName).collect(Collectors.joining(", ")));
        }
        if (!room.getNpcs().isEmpty()) {
            sb.append("\nNPCs: ").append(room.getNpcs().stream()
                    .map(NPC::getName).collect(Collectors.joining(", ")));
        }
        if (!room.getExits().isEmpty()) {
            sb.append("\nExits: ").append(String.join(", ", room.getExits().keySet()));
        }

        List<String> others = state.playersInRoom(room.getId()).stream()
                .map(Player::getName)
                .filter(n -> !n.equals(player.getName()))
                .collect(Collectors.toList());
        if (!others.isEmpty()) {
            sb.append("\nOther players here: ").append(String.join(", ", others));
        }
        return sb.toString();
    }

    private String cmdGo(Player player, List<String> args) {
        if (args.isEmpty()) {
            return "Usage: go <direction>";
        }
        String direction = args.get(0).toLowerCase();
        Room room = requireRoom(player);
        if (room == null) {
            return "Invalid location.";
        }

        String targetId = room.getExits().get(direction);
        if (targetId == null) {
            return "No exit to the " + direction + ".";
        }

        Room target = state.getRoom(targetId);
        if (target == null) {
            return "That path leads nowhere.";
        }

        if (target.isLocked()) {
            return "The way " + direction + " is locked. You may need an item or puzzle solution.";
        }

        if ("vault".equals(room.getId()) && "north".equals(direction) && "treasure".equals(targetId)) {
            Puzzle puzzle = room.getPuzzle();
            if (puzzle != null && !puzzle.isSolved()) {
                return "The inner door is sealed. Use the rusty key here first.";
            }
        }

        player.setCurrentRoomId(targetId);
        events.notify(player.getName() + " moved " + direction + " to " + target.getName() + ".");
        return cmdLook(player);
    }

    private String cmdTake(Player player, List<String> args) {
        if (args.isEmpty()) {
            return "Usage: take <item>";
        }
        String itemName = String.join(" ", args).toLowerCase();
        Room room = requireRoom(player);
        if (room == null) {
            return "Invalid location.";
        }

        Item item = findItemInList(room.getItems(), itemName);
        if (item == null) {
            return "There is no '" + itemName + "' here.";
        }
        if (!item.isPortable()) {
            return "You cannot take " + item.getName() + ".";
        }

        room.getItems().remove(item);
        player.getInventory().add(item);
        events.notify(player.getName() + " picked up " + item.getName() + ".");
        if (WIN_ITEM.equalsIgnoreCase(item.getName())) {
            return triggerWin(player);
        }
        return "You took the " + item.getName() + ".";
    }

    private String cmdDrop(Player player, List<String> args) {
        if (args.isEmpty()) {
            return "Usage: drop <item>";
        }
        String itemName = String.join(" ", args).toLowerCase();
        Room room = requireRoom(player);
        if (room == null) {
            return "Invalid location.";
        }

        Item item = findItemInList(player.getInventory(), itemName);
        if (item == null) {
            return "You do not have '" + itemName + "'.";
        }

        player.getInventory().remove(item);
        room.getItems().add(item);
        events.notify(player.getName() + " dropped " + item.getName() + ".");
        return "You dropped the " + item.getName() + ".";
    }

    private String cmdInventory(Player player) {
        if (player.getInventory().isEmpty()) {
            return "Your inventory is empty.";
        }
        String items = player.getInventory().stream()
                .map(Item::getName)
                .collect(Collectors.joining(", "));
        return "Inventory: " + items;
    }

    private String cmdTalk(Player player, List<String> args) {
        if (args.isEmpty()) {
            return "Usage: talk <npc>";
        }
        String npcName = String.join(" ", args).toLowerCase();
        Room room = requireRoom(player);
        if (room == null) {
            return "Invalid location.";
        }

        for (NPC npc : room.getNpcs()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                return npc.getName() + ": " + npc.getDialogue();
            }
        }
        return "No NPC named '" + npcName + "' here.";
    }

    private String cmdUse(Player player, List<String> args) {
        if (args.isEmpty()) {
            return "Usage: use <item>";
        }
        String itemName = String.join(" ", args).toLowerCase();
        Room room = requireRoom(player);
        if (room == null) {
            return "Invalid location.";
        }

        if (!player.hasItem(itemName)) {
            return "You do not have '" + itemName + "'.";
        }

        if ("vault".equals(room.getId())) {
            Puzzle puzzle = room.getPuzzle();
            if (puzzle != null && !puzzle.isSolved()) {
                if (itemName.equals(puzzle.getRequiredItem().toLowerCase())) {
                    puzzle.setSolved(true);
                    room.setLocked(false);
                    Room treasure = state.getRoom("treasure");
                    if (treasure != null) {
                        treasure.setLocked(false);
                    }
                    removeItemFromPlayer(player, itemName);
                    events.notify(player.getName() + " unlocked the vault with the "
                            + puzzle.getRequiredItem() + "!");
                    return "The vault door grinds open. The path north is now accessible!";
                }
                return "That item does not work here.";
            }
        }

        if (player.hasItem(WIN_ITEM)) {
            return triggerWin(player);
        }
        return "Nothing happens.";
    }

    private String cmdGive(Player player, List<String> args) {
        if (args.size() < 3) {
            return "Usage: give <item> to <player>";
        }
        int toIndex = -1;
        for (int i = 0; i < args.size(); i++) {
            if ("to".equalsIgnoreCase(args.get(i))) {
                toIndex = i;
                break;
            }
        }
        if (toIndex < 1 || toIndex >= args.size() - 1) {
            return "Usage: give <item> to <player>";
        }

        String targetName = args.get(toIndex + 1);
        String itemName = String.join(" ", args.subList(0, toIndex)).toLowerCase();

        Player target = state.getPlayer(targetName);
        if (target == null) {
            return "Player '" + targetName + "' not found.";
        }
        if (!target.getCurrentRoomId().equals(player.getCurrentRoomId())) {
            return targetName + " is not in your room.";
        }

        Item item = findItemInList(player.getInventory(), itemName);
        if (item == null) {
            return "You do not have '" + itemName + "'.";
        }

        player.getInventory().remove(item);
        target.getInventory().add(item);
        events.notify(player.getName() + " gave " + item.getName() + " to " + target.getName() + ".");
        return "You gave " + item.getName() + " to " + target.getName() + ".";
    }

    private String cmdPlayers() {
        StringBuilder sb = new StringBuilder("Players:\n");
        for (Player p : state.getPlayers().values()) {
            Room room = state.getRoom(p.getCurrentRoomId());
            String roomName = room != null ? room.getName() : "unknown";
            String status = p.hasWon() ? " (WON)" : "";
            sb.append("  ").append(p.getName()).append(": ").append(roomName).append(status).append("\n");
        }
        return sb.toString().trim();
    }

    private String triggerWin(Player player) {
        player.setHasWon(true);
        state.setGameOver(true);
        String message = player.getName() + " obtained the golden artifact! The team succeeds!";
        state.setWinMessage(message);
        events.notify(message);
        return message + "\n*** YOU WIN! ***";
    }

    private Room requireRoom(Player player) {
        return state.getRoom(player.getCurrentRoomId());
    }

    private static Item findItemInList(List<Item> items, String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    private static void removeItemFromPlayer(Player player, String name) {
        player.getInventory().removeIf(i -> i.getName().equalsIgnoreCase(name));
    }
}
