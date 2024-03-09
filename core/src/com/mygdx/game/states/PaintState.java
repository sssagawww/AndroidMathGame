package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.handlers.GameStateManager;

public class PaintState extends GameState{

    public PaintState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        //cam.setBounds(0, V_WIDTH, 0, V_HEIGHT); //? //4864 2688
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {

    }
}
