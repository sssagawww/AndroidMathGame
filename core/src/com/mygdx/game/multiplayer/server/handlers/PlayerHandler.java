package com.mygdx.game.multiplayer.server.handlers;

import com.esotericsoftware.kryonet.Connection;
import com.mygdx.game.multiplayer.events.PlayerAddEvent;
import com.mygdx.game.multiplayer.events.PlayerRemoveEvent;
import com.mygdx.game.multiplayer.server.ServerFoundation;
import com.mygdx.game.multiplayer.server.supers.ServerPlayer;

import java.util.LinkedList;

public class PlayerHandler {
    public static final PlayerHandler INSTANCE = new PlayerHandler();
    private LinkedList<ServerPlayer> players;

    public PlayerHandler() {
        players = new LinkedList<>();
    }

    public ServerPlayer getPlayerByConnection(final Connection connection) {
        for(final ServerPlayer serverPlayer : players) {
            if(serverPlayer.getConnection() == connection) {
                return serverPlayer;
            }
        }
        return null;
    }

    public ServerPlayer getPlayerByUsername(final String username) {
        for(final ServerPlayer serverPlayer : players) {
            if(serverPlayer.getUsername().equals(username)) {
                return serverPlayer;
            }
        }
        return null;
    }

    public void update() {
        for (ServerPlayer player : players) {
            player.update();
        }
    }

    public void addPlayer(final ServerPlayer serverPlayer) {
        for(ServerPlayer all : players) {
            final PlayerAddEvent playerAddEvent = new PlayerAddEvent();
            playerAddEvent.username = all.getUsername();
            playerAddEvent.x = all.getX();
            playerAddEvent.y = all.getY();

            serverPlayer.getConnection().sendTCP(playerAddEvent);
        }

        final PlayerAddEvent playerAddEvent = new PlayerAddEvent();
        playerAddEvent.username = serverPlayer.getUsername();
        playerAddEvent.x = serverPlayer.getX();
        playerAddEvent.y = serverPlayer.getY();

        ServerFoundation.instance.getServer().sendToAllTCP(playerAddEvent);

        players.add(serverPlayer);
    }

    public void removePlayer(final ServerPlayer serverPlayer) {
        players.remove(serverPlayer);

        final PlayerRemoveEvent playerRemoveEvent = new PlayerRemoveEvent();
        playerRemoveEvent.username = serverPlayer.getUsername();

        ServerFoundation.instance.getServer().sendToAllTCP(playerRemoveEvent);
    }

    public LinkedList<ServerPlayer> getPlayers() {
        return players;
    }
}
