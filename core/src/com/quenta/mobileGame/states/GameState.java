package com.quenta.mobileGame.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.quenta.mobileGame.MyGdxGame;
import com.quenta.mobileGame.UI.Controller;
import com.quenta.mobileGame.handlers.BoundedCamera;
import com.quenta.mobileGame.handlers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;
    protected MyGdxGame game;
    protected SpriteBatch sb;
    protected BoundedCamera cam;
    protected Controller controller;
    protected Stage controllerStage;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
        game = gsm.game();
        sb = game.getSb();
        cam = game.getCam();
        controller = game.getController();
        controllerStage = game.getControllerStage();
    }
    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}