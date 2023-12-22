package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.UI.MenuBtn;
import com.mygdx.game.handlers.GameStateManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.GameStateManager.PLAY;
import static com.mygdx.game.handlers.GameStateManager.QUIT;

public class Menu extends GameState implements StateMethods{
    private World world;
    //private MenuBtn[] btns = new MenuBtn[2];

    public Menu(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        loadBtn();
    }

    private void loadBtn() {
        //btns[0] = new MenuBtn(V_WIDTH / 2, 250*4, 0, PLAY, gsm);
        //btns[1] = new MenuBtn(V_WIDTH / 2, 500*4, 3, QUIT, gsm);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        world.step(dt, 6, 2);
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gsm.setState(PLAY);
        }
        /*for (MenuBtn mb : btns)
            mb.update();*/
    }

    @Override
    public void render() {
        /*for (MenuBtn mb : btns)
            mb.render(sb);*/
    }

    @Override
    public void dispose() {

    }

    @Override
    public void draw(Graphics g) {
        /*for (MenuBtn mb : btns)
            mb.draw(g);
        g.drawString("asdsad", 123, 123);
        g.setColor(Color.CYAN);*/
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
       /* for (MenuBtn mb : btns){
            if(mb.isIn(e, mb)){
                mb.setMousePressed(true);
                break;
            }
        }*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {
       /* for (MenuBtn mb : btns){
            if(mb.isIn(e,mb)){
                if(mb.isMousePressed())
                    mb.applyGameState();
                break;
            }
        }
        resetBtns();*/
    }

    private void resetBtns() {
        /*for (MenuBtn mb : btns){
            mb.resetBooleans();
        }*/
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        /*for (MenuBtn mb : btns)
            mb.setMouseOver(false);
        for (MenuBtn mb : btns)
            if(mb.isIn(e,mb)) {
                mb.setMouseOver(true);
                break;
            }*/
    }

    @Override
    public void keyPressed(KeyEvent k) {

    }

    @Override
    public void keyReleased(KeyEvent k) {

    }
}
