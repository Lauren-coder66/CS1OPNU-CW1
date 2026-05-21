package com.adventure.cli;

import com.adventure.logic.GameEngine;
import com.adventure.model.Player;
import com.adventure.singleton.GameState;
import com.adventure.world.WorldBuilder;

import java.util.Scanner;

public class GameCLI {
    private final GameEngine engine;
    private final Scanner scanner;

    public GameCLI() {
        this(new Scanner(System.in));
    }

    public GameCLI(Scanner scanner) {
        GameState.reset();
        WorldBuilder.buildWorld();
        this.engine = new GameEngine();
        this.scanner = scanner;
    }

    public GameEngine getEngine() {
        return engine;
    }

    public void run() {
        printBanner();
        setupPlayers();
        if (engine.getState().getPlayers().isEmpty()) {
            System.out.println("No players joined. Exiting.");
            return;
        }

        System.out.println("\nAdventure begins! Use 'switch <name>' to change active player.");
        System.out.println(engine.processCommand("look"));

        while (!engine.getState().isGameOver()) {
            String active = engine.getState().getActivePlayer();
            System.out.print("\n[" + active + "]> ");
            String raw = scanner.nextLine().trim();

            if (raw.isEmpty()) {
                continue;
            }
            if ("quit".equalsIgnoreCase(raw) || "exit".equalsIgnoreCase(raw)) {
                System.out.println("Thanks for playing!");
                break;
            }

            if (raw.toLowerCase().startsWith("join ")) {
                System.out.println(engine.addPlayer(raw.substring(5).trim()));
                continue;
            }

            if (raw.toLowerCase().startsWith("switch ")) {
                System.out.println(engine.switchPlayer(raw.substring(7).trim()));
                continue;
            }

            String result = engine.processCommand(raw);
            if (!result.isEmpty()) {
                System.out.println(result);
            }

            Player player = engine.getState().getPlayer(active);
            if (player != null && !engine.getState().isGameOver()) {
                String winMsg = engine.tryCollectWin(player);
                if (winMsg != null) {
                    System.out.println(winMsg);
                }
            }
        }

        if (engine.getState().isGameOver()) {
            System.out.println("\n" + engine.getState().getWinMessage());
        }
    }

    private void printBanner() {
        System.out.println(repeatChar('=', 50));
        System.out.println("  Multi-Player Text Adventure (Java)");
        System.out.println("  Goal: unlock the vault and claim the golden artifact");
        System.out.println(repeatChar('=', 50));
    }

    private static String repeatChar(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    private void setupPlayers() {
        System.out.println("\nAdd players (command: join <name>, empty line to start):");
        while (true) {
            System.out.print("join> ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                if (!engine.getState().getPlayers().isEmpty()) {
                    break;
                }
                System.out.println("At least one player is required.");
                continue;
            }
            String name = raw.toLowerCase().startsWith("join ") ? raw.substring(5).trim() : raw;
            System.out.println(engine.addPlayer(name));
        }
    }
}
