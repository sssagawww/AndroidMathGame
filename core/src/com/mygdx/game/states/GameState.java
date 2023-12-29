package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.handlers.B2DVars;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;
    protected MyGdxGame game;
    protected SpriteBatch sb;
    protected BoundedCamera cam;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
        game = gsm.game();
        sb = game.getSb();
        cam = game.getCam();
    }
    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}