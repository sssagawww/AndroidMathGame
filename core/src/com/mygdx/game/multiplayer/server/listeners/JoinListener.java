package com.mygdx.game.multiplayer.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.multiplayer.events.JoinRequestEvent;
import com.mygdx.game.multiplayer.events.JoinResponseEvent;
import com.mygdx.game.multiplayer.server.handlers.PlayerHandler;
import com.mygdx.game.multiplayer.server.supers.ServerPlayer;

import java.util.Random;

public class JoinListener extends Listener {
    @Override
    public void received(Connection connection, Object object) {

        // Join request
        if (object instanceof JoinRequestEvent) {
            final JoinRequestEvent joinRequestEvent = (JoinRequestEvent) object;

            final ServerPlayer serverPlayer = new ServerPlayer(joinRequestEvent.username, connection);


            // Name already in use
            if(PlayerHandler.INSTANCE.getPlayerByUsername(((JoinRequestEvent) object).username) != null) {
                return;
            }

            PlayerHandler.INSTANCE.addPlayer(serverPlayer);

            final JoinResponseEvent joinResponseEvent = new JoinResponseEvent();
            connection.sendTCP(joinResponseEvent);
        }

        super.received(connection, object);
    }
}
