package com.mygdx.game.data;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class DataStorage implements Serializable {
    public Vector2 playerPosX;
    float playerPosY;
    boolean save = false;
}
