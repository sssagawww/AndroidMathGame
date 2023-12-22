package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.handlers.GameStateManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.UI.Buttons.*;

public class MenuBtn {
    private int xPos, yPos, rowIndex, index, state;
    private int xCenter = V_WIDTH / 2;
    private GameStateManager gsm;
    private Texture tex;
    private TextureRegion[] imgs;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    public MenuBtn(int xPos, int yPos, int rowIndex, int state, GameStateManager gsm) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;
        this.gsm = gsm;
        loadImages();
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos - xCenter, yPos, BTN_WIDTH, BTN_HEIGHT);
    }

    private void loadImages() {
        imgs = new TextureRegion[9];
        tex = MyGdxGame.res.getTexture("btn");
        TextureRegion texR = new TextureRegion(tex);
        for (int i = 0; i < imgs.length; i++) {
            imgs[i].setTexture(texR.getTexture());
            imgs[i].setRegion(i * BTN_WIDTH_DEF, rowIndex * BTN_HEIGHT_DEF, BTN_WIDTH_DEF, BTN_HEIGHT_DEF);
        }
    }

    public void render(SpriteBatch batch){
        batch.begin();
        batch.draw(imgs[index], xPos - xCenter, yPos, BTN_WIDTH, BTN_HEIGHT);
        batch.end();
    }
    public void draw(Graphics g){
    }

    public void update(){
        index = 0;
        if(mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void applyGameState(){
        gsm.setState(state);
    }
    public void resetBooleans(){
        mouseOver = false;
        mousePressed = false;
    }
    public boolean isIn(MouseEvent e, MenuBtn mb){
        return mb.getBounds().contains(e.getX(), e.getY());
    }
}
