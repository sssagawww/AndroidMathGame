package com.mygdx.game.handlers;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.states.*;

import java.awt.Menu;
import java.util.Stack;

public class GameStateManager {
    private MyGdxGame game;
    private Play play;
    private Stack<GameState> gameStates;
    private Forest forest;
    private MazeState mazeState;
    private int lastState;
    public static final int PLAY = 912837;
    public static final int MENU = 0;
    public static final int PAINT = 1;
    public static final int BATTLE = 2;
    public static final int NEW_GAME = 4;
    public static final int FOREST = 5;
    public static final int MAZE = 6;
    public static final int RHYTHM = 8;
    public static final int BLACK_SCREEN = 100;

    public GameStateManager(MyGdxGame game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MENU);
    }

    //peek - get верхний элемент
    public void update(float dt) {
        gameStates.peek().update(dt);
    }

    public void render() {
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        //через switch?
        if (state == PLAY) {
            return play;
        } else if (state == MENU) {
            return new Menu2(this);
        } else if (state == BATTLE) {
            return new BattleState2(this);
        } else if (state == NEW_GAME) {
            play = new Play(this);
            return play;
        } else if (state == PAINT) {
            return new PaintState(this);
        } else if (state == MAZE) {
            if (mazeState==null) return mazeState = new MazeState(this);
            return mazeState;
        } else if (state == RHYTHM) {
            return new RhythmState(this);
        } else if (state == FOREST) {
            if (forest==null) return forest = new Forest(this);
            return forest;
        } else if (state == BLACK_SCREEN) {
            return new BlackScreen(this);
        }
        return null;
    }

    //сначала удаляет самый верхний и вместо него закидывает новый
    public void setState(int state) {
        popState();
        pushState(state);
    }

    //удаляет верхний элемент
    public void popState() {
        GameState gs = gameStates.pop();
        gs.dispose();
    }

    //кидает в стэк тот стейт (обозначаются int переменными), который передан
    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public Play getPlay() {
        return play;
    }

    public MyGdxGame game() {
        return game;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }
}
