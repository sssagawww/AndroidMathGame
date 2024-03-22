package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;

public class PaintState extends GameState implements InputProcessor {
    private ShapeRenderer shapeRenderer;
    private int rectX, rectY;
    private int rectWidth, rectHeight;
    private ArrayList<PixelPoint> points;

    public PaintState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        shapeRenderer = new ShapeRenderer();
        rectX = 0;
        rectY = 0;
        rectWidth = 5;
        rectHeight = 5;

        points = new ArrayList<>();

        cam.setBounds(0, V_WIDTH, 0, V_HEIGHT); //? //4864 2688

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //shapeRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < points.size(); i++) {
            shapeRenderer.rect(points.get(i).getX(), points.get(i).getY(), rectWidth, rectHeight);
        }
        shapeRenderer.end();
        sb.end();
    }

    @Override
    public void dispose() {

    }

    public ArrayList<PixelPoint> getPoints() {
        return points;
    }

    public ArrayList<Integer> getPointsX(){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(points.get(i).getX());
        }
        return list;
    }

    public ArrayList<Integer> getPointsY(){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(points.get(i).getY());
        }
        return list;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        points.add(new PixelPoint(screenX - rectWidth / 2, V_HEIGHT - screenY - rectHeight / 2));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
