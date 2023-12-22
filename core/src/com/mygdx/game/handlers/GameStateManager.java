package com.mygdx.game.handlers;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.states.*;

import java.util.Stack;

public class GameStateManager {
    private MyGdxGame game;
    private Play play;
    private Stack<GameState> gameStates;
    public static final int PLAY = 912837;
    public static final int MENU = 0;
    public static final int QUIT = 1;
    public static final int BATTLE = 2;
    public static final int NEW_GAME = 4;

    public GameStateManager(MyGdxGame game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        //play = new Play(this);
        pushState(MENU);
    }

    public MyGdxGame game(){
        return game;
    }

    public void update(float dt){
        gameStates.peek().update(dt);
    }
    public void render(){
        gameStates.peek().render();
    }

    private GameState getState(int state){
        if (state == PLAY) {
            return play;
        } else if (state == MENU) {
            return new Menu2(this);
        } else if (state == BATTLE) {
            return new BattleState2(this);
        } else if (state == NEW_GAME) {
            play = new Play(this);
            return play;
        }
        return null;
    }
    public void setState(int state){
        popState();
        pushState(state);
    }
    public void popState() {
        GameState gs = gameStates.pop();
        gs.dispose();
    }
    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public Play getPlay() {
        return play;
    }
}
