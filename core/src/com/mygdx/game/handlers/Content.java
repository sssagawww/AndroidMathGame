package com.mygdx.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Content {
    private HashMap<String, Texture> textures;

    public Content(){
        textures = new HashMap<String, Texture>();
    }

    public void loadTexture(String path, String key){
        Texture tex = new Texture(Gdx.files.internal(path));
        textures.put(key, tex);
    }

    /*public static BufferedImage getBISprite(String path){
        BufferedImage bimg = null;
        InputStream is = Content.class.getResourceAsStream("/" + path);
        try {
            bimg = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return bimg;
    }*/
    /*public void loadBufImg(String path, String key){
        BufferedImage bimg = getBISprite(path);
        bufImgs.put(key, bimg);
    }*/
    /*public BufferedImage getBufImg(String key) {
        return bufImgs.get(key);
    }*/

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    public void disposeTexture(String key){
        Texture tex = textures.get(key);
        if (tex != null) {
            tex.dispose();
        }
    }
}
