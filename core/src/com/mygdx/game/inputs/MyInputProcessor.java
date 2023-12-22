package com.mygdx.game.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int k){
        if (k == Input.Keys.W) {
            GameKeys.setKey(GameKeys.KEY_W, true);
        }
        if (k == Input.Keys.S) {
            GameKeys.setKey(GameKeys.KEY_S, true);
        }
        if (k == Input.Keys.A) {
            GameKeys.setKey(GameKeys.KEY_A, true);
        }
        if (k == Input.Keys.D) {
            GameKeys.setKey(GameKeys.KEY_D, true);
        }
        return true;
    }
    @Override
    public boolean keyUp(int k){
        if (k == Input.Keys.W) {
            GameKeys.setKey(GameKeys.KEY_W, false);
        }
        if (k == Input.Keys.S) {
            GameKeys.setKey(GameKeys.KEY_S, false);
        }
        if (k == Input.Keys.A) {
            GameKeys.setKey(GameKeys.KEY_A, false);
        }
        if (k == Input.Keys.D) {
            GameKeys.setKey(GameKeys.KEY_D, false);
        }
        return true;
    }
}
