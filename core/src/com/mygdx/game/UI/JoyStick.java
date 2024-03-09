package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

public class JoyStick {
    private Circle circle, circle2;

    public JoyStick(float x, float y, float radius) {
        circle = new Circle(x, y, radius);
        circle2 = new Circle(x, y, radius / 4);
    }

    public void update(float x, float y) {
        //когда внутренний круг доходит до края, он застывает и не двигается
        if (circle2.contains(x, y)) {
            circle2.setPosition(x, y);
            if (circle2.x > circle.x + circle.radius) {
                circle2.x = circle.x + circle.radius;
            }
            if (circle2.y > circle.y + circle.radius) {
                circle2.y = circle.y + circle.radius;
            }
            if (circle2.x < circle.x - circle.radius) {
                circle2.x = circle.x - circle.radius;
            }
            if (circle2.y < circle.y - circle.radius) {
                circle2.y = circle.y - circle.radius;
            }
        }

        // другой метод расчета положения кругов, тоже плохо работает..
        /*double deltaX = x - circle.x;
        double deltaY = y - circle.y;
        double delta = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        if (delta < circle.radius + circle.x) {
            circle2.x = circle.x + (float) deltaX;
            circle2.y = circle.y + (float) deltaX;
        } else {
            circle2.x = circle.x + (float) (deltaX / delta) * circle.radius;
            circle2.y = circle.y + (float) (deltaY / delta) * circle.radius;
        }
        System.out.println(deltaY + " " + delta + " radius " + circle.radius);*/
    }

    public void setDefaultPos() {
        circle2.setPosition(circle.x, circle.y);
    }

    public int getState() {
        if (circle2.x == circle.x && circle2.y > circle.y) {
            System.out.println(1);
            return 1; //up
        } else if (circle2.x > circle.x && circle2.y > circle.y) {
            System.out.println(2);
            return 2; //up'n'right
        } else if (circle2.x > circle.x && circle2.y == circle.y) {
            System.out.println(3);
            return 3; //right
        } else if (circle2.x > circle.x && circle2.y < circle.y) {
            System.out.println(4);
            return 4; //down'n'right
        } else if (circle2.x == circle.x && circle2.y < circle.y) {
            System.out.println(5);
            return 5; //down
        } else if (circle2.x < circle.x && circle2.y < circle.y) {
            System.out.println(6);
            return 6; //down'n'left
        } else if (circle2.x < circle.x && circle2.y == circle.y) {
            System.out.println(7);
            return 7; //left
        } else if (circle2.x < circle.x && circle2.y > circle.y) {
            System.out.println(8);
            return 8; //up'n'left
        } else {
            return 0;
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(circle2.x, circle2.y, circle2.radius);
        shapeRenderer.end();
    }
}
