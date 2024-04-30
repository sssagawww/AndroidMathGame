package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;

public class PlayEntities {
    public ArrayList<Texture> texs = new ArrayList<>();
    public TextureRegion[] sprites;
    private ArrayList<B2DSprite> list;
    private int curEntity=0;

    public PlayEntities() {
        list = new ArrayList<>();
    }

    public void addEntity(Body body, String texName) {
        B2DSprite sprite = new B2DSprite(body);
        body.setUserData(texName);
        Texture tex = MyGdxGame.res.getTexture(texName);
        texs.add(tex);
        if (tex.getHeight() > 58 * 2) { //??? изменить
            sprites = TextureRegion.split(tex, 58, 58)[0];
        } else {
            sprites = TextureRegion.split(tex, tex.getHeight(), tex.getHeight())[0];
        }
        sprite.setAnimation(sprites, 1 / 5f);
        list.add(sprite);
    }

    public void render(SpriteBatch sb, float width, float height) {
        for (B2DSprite i : list) {
            if(i.isVisible()) i.render(sb, i.getHeight() * 1.5f, i.getHeight() * 1.5f);
        }
    }

    public void update(float dt) {
        for (B2DSprite i : list) {
            if(i.isVisible()) i.update(dt);
        }
    }

    public void setNewAnimation(int entityID, int row, int width, int height) {
        B2DSprite sprite = list.get(entityID);
        TextureRegion[] sprites = TextureRegion.split(texs.get(entityID), width, height)[row];
        sprite.setAnimation(sprites, 1 / 12f);
    }

    public int getEntityCount(){
        return list.size();
    }

    public B2DSprite getEntity(int id) {
        return list.get(id);
    }

    public int getCurEntity() {
        return curEntity;
    }

    public void setCurEntity(int curEntity) {
        this.curEntity = curEntity;
    }
    public void removeEntity(Body body){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getBody().equals(body)){
                list.remove(i);
            }
        }
    }
}
