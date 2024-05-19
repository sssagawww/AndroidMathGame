package com.mygdx.game.multiplayer.server.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.multiplayer.server.handlers.PlayerHandler;
import com.mygdx.game.multiplayer.server.supers.ServerPlayer;

public class LeaveListener extends Listener {
    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);

        final ServerPlayer leavePlayer = PlayerHandler.INSTANCE.getPlayerByConnection(connection);

        if(leavePlayer == null) return;

        PlayerHandler.INSTANCE.removePlayer(leavePlayer);
    }
}
