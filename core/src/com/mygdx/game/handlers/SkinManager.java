package com.mygdx.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinManager {
    public static Skin generateSkin(AssetManager assetManager) {
        Skin skin = new Skin();
        TextureAtlas uiAtlas = assetManager.get("UI/testAtlas.atlas"); //uipack
        TextureAtlas uiAtlas2 = assetManager.get("UI/testAtlas2.atlas");
        TextureAtlas uiAtlas3 = assetManager.get("UI/uipack.atlas");

        //ninePatch - растягиваемая картинка
        NinePatch dialog = new NinePatch(uiAtlas.findRegion("background"), 10, 10, 5, 5);//dialoguebox
        skin.add("GUI_img", dialog);
        NinePatch dialog2 = new NinePatch(uiAtlas2.findRegion("back"), 10, 10, 5, 5);
        skin.add("GUI_2x", dialog2);
        NinePatch background2 = new NinePatch(uiAtlas2.findRegion("light_back"), 10, 10, 5, 5);
        skin.add("background2", background2);
        NinePatch option = new NinePatch(uiAtlas3.findRegion("optionbox"),6, 6, 6, 6);
        skin.add("optionbox", option);
        NinePatch status = new NinePatch(uiAtlas.findRegion("background"),5, 4, 2, 2);
        skin.add("status", status);
        NinePatch menuBtn_up = new NinePatch(uiAtlas2.findRegion("menuBtn_up"),10, 10, 10, 10);
        skin.add("menuBtn_up", menuBtn_up);
        NinePatch menuBtn_down = new NinePatch(uiAtlas2.findRegion("menuBtn_down"),10, 10, 10, 10);
        skin.add("menuBtn_down", menuBtn_down);
        NinePatch borders = new NinePatch(uiAtlas2.findRegion("borders"),10, 10, 10, 10);
        skin.add("borders", borders);
        NinePatch prLine = new NinePatch(uiAtlas.findRegion("prLine"),1, 1, 1, 1);
        skin.add("prLine", prLine);
        /*NinePatch next_btn = new NinePatch(uiAtlas2.findRegion("next_btn"),10, 10, 1, 1);
        skin.add("next_btn", next_btn);*/

        NinePatch battleinfobox = new NinePatch(uiAtlas3.findRegion("battleinfobox"),14, 14, 5, 8);
        battleinfobox.setPadLeft((int)battleinfobox.getTopHeight());
        skin.add("battleinfobox", battleinfobox);

        skin.add("hpbar_side", uiAtlas3.findRegion("hpbar_side"), TextureRegion.class);
        skin.add("hpbar_bar", uiAtlas3.findRegion("hpbar_bar"), TextureRegion.class);
        skin.add("green", uiAtlas3.findRegion("green"), TextureRegion.class);
        skin.add("yellow", uiAtlas3.findRegion("yellow"), TextureRegion.class);
        skin.add("red", uiAtlas3.findRegion("red"), TextureRegion.class);
        skin.add("background_hpbar", uiAtlas3.findRegion("background_hpbar"), TextureRegion.class);

        skin.add("arrow", uiAtlas3.findRegion("arrow"), TextureRegion.class);
        skin.add("next_btn", uiAtlas2.findRegion("next_btn"), TextureRegion.class);
        BitmapFont font = assetManager.get("mcRus.fnt", BitmapFont.class);
        skin.add("font", font);

        skin.add("ok", uiAtlas.findRegion("ok"), TextureRegion.class);
        skin.add("ok_down", uiAtlas.findRegion("ok_down"), TextureRegion.class);
        skin.add("wrong", uiAtlas.findRegion("wrong"), TextureRegion.class);

        skin.add("Квадрат", new Texture("controller/square.png"));
        skin.add("Круг", new Texture("controller/circle.png"));
        skin.add("Звезда", new Texture("controller/star.png"));
        skin.add("Треугольник", new Texture("controller/triangle.png"));
        skin.add("Ромб", new Texture("controller/rhombus.png"));

        skin.add("bg", new Texture("UI/bg.png"));

        Label.LabelStyle lstyle = new Label.LabelStyle();
        lstyle.font = skin.getFont("font");
        lstyle.fontColor = Color.BLACK;
        skin.add("lstyle", lstyle);
        //skin.add("defJson", Gdx.files.internal("uiskin.json"));

        return skin;
    }
}
