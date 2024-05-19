package com.mygdx.game.multiplayer.server.handlers;

import com.mygdx.game.multiplayer.events.PlayerUpdateEvent;
import com.mygdx.game.multiplayer.server.ServerFoundation;
import com.mygdx.game.multiplayer.server.supers.ServerPlayer;

public class PlayerUpdateHandler implements Runnable {
    public static final PlayerUpdateHandler INSTANCE = new PlayerUpdateHandler();
    private boolean running;

    public synchronized void start() {
        running = true;

        final Thread thread = new Thread(this);
        thread.start();

    }

    private void tick() {
        for (ServerPlayer player : PlayerHandler.INSTANCE.getPlayers()) {
            // Update server player
            player.update();

            // Send update to all clients
            final PlayerUpdateEvent playerUpdateEvent = new PlayerUpdateEvent();
            playerUpdateEvent.username = player.getUsername();
            playerUpdateEvent.x = player.getX();
            playerUpdateEvent.y = player.getY();

            ServerFoundation.instance.getServer().sendToAllUDP(playerUpdateEvent);
        }
    }

    @Override
    public void run() {
        long pastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            try {
                Thread.sleep((long) (60F / amountOfTicks));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long now = System.nanoTime();
            delta += (now - pastTime) / ns;
            pastTime = now;

            while (delta > 0) {
                tick();
                delta--;
            }

        }
    }
}

