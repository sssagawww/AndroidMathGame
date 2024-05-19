package com.mygdx.game.multiplayer.server.supers;

import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.mygdx.game.entities.Player2;

public class ServerPlayer {
    private Player2 player;
    private final Connection connection;
    private final String username;

    public ServerPlayer(String username, Connection connection) {
        this.connection = connection;
        this.username = username;



    }

    public void update() {
        player.updatePL();
    }

    public String getUsername() {
        return username;
    }

    public float getX() {
        return player.getPosition().x;
    }

    public float getY() {
        return player.getPosition().y;
    }

    public Connection getConnection() {
        return connection;
    }
}
