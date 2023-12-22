package com.mygdx.game.Dialog;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.UI.OptionBox;

public class OptionBoxController extends InputAdapter {
    private OptionBox box;

    public OptionBoxController(OptionBox box){
        this.box = box;
    }
    @Override
    public boolean keyUp(int keycode){
        if(keycode == Input.Keys.E){
            box.moveDown();
        } else if(keycode == Input.Keys.Q){
            box.moveUp();
        }
        return false;
    }
   /* @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button){
        if(button == Input.Buttons.LEFT && (screenX >= 670 && screenX <=753) && (screenY >= 453 && screenY <= 490)){
            box.moveDown();
            System.out.println(box.getHeight());
        } else if(button == Input.Buttons.LEFT && screenX == box.getWidth()/2 && screenY == box.getHeight()/2){
            box.moveUp();
            System.out.println(screenX + " " + screenY);
        }
        return false;
    }*/
}
