package com.adventure.world;

import com.adventure.factory.GameObjectFactory;
import com.adventure.model.Room;
import com.adventure.singleton.GameState;

public final class WorldBuilder {
    private static final String[] TEMPLATES = {
            "entrance", "camp", "hall", "library", "vault", "treasure"
    };

    private WorldBuilder() {
    }

    public static GameState buildWorld() {
        GameState state = GameState.getInstance();
        for (String template : TEMPLATES) {
            Room room = GameObjectFactory.createRoom(template);
            state.getRooms().put(room.getId(), room);
        }
        return state;
    }
}
