package com.mygdx.game.battle.render_controller;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BattleRenderer {
    private AssetManager assetManager;
    private TextureRegion background;

    public BattleRenderer(AssetManager assetManager){
        this.assetManager = assetManager;
        TextureAtlas atlas = assetManager.get("testAtlas.atlas", TextureAtlas.class);
        background = atlas.findRegion("background");
    }

    public void render(SpriteBatch batch){
        batch.draw(background, 0, 0, 1216, 672);
    }
}
