package com.mygdx.game.entities;

import static com.mygdx.game.handlers.B2DVars.PPM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;

public class PlayEntities {
    public Texture tex;
    public TextureRegion[] sprites;
    private ArrayList<B2DSprite> list;
    private int width;
    private int height;

    public PlayEntities() {
        list = new ArrayList<>();
    }

    public void addEntity(Body body, String texName) {
        B2DSprite sprite = new B2DSprite(body);
        body.setUserData(texName);
        tex = MyGdxGame.res.getTexture(texName);
        sprites = TextureRegion.split(tex, tex.getHeight(), tex.getHeight())[0];
        sprite.setAnimation(sprites, 1 / 5f);
        list.add(sprite);
    }

    public void render(SpriteBatch sb, float widthK, float heightK) {
        for (B2DSprite i : list) {
            i.render(sb, i.getHeight()*widthK, i.getHeight()*heightK);
        }
    }

    public void update(float dt) {
        for (B2DSprite i : list) {
            i.update(dt);
        }
    }
}
