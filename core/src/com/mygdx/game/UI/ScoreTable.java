package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;

public class ScoreTable extends Table {
    private Label.LabelStyle style;
    private Table uiTable;
    private HashMap<String, Integer> players;

    public ScoreTable(Skin skin) {
        super(skin);
        background(getSkin().getDrawable("menuBtn_up"));

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);
        style = new Label.LabelStyle(font, Color.BLACK);

        Label title = new Label("\n", style);
        title.setText("Рейтинг");
        title.setAlignment(Align.center);

        uiTable = new Table();

        players = new HashMap<>();

        add(title).center().row();
        add(uiTable).center();
    }

    public void addPlayerScore(String playerName, float score) {
        Label title = new Label("\n", style);
        title.setText(playerName);
        title.setAlignment(Align.center);

        Label scoreLabel = new Label("\n", style);
        scoreLabel.setText("" + score);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setName(playerName + "_label");

        Table table = new Table();
        table.setName(playerName);
        table.add(title).align(Align.left).width(V_WIDTH / 8f).height(V_HEIGHT / 12f).expand();
        table.add(scoreLabel).align(Align.right).width(V_WIDTH / 10f).height(V_HEIGHT / 12f);

        uiTable.add(table).align(Align.right).row();
        players.put(playerName, Math.round(score));
    }

    public HashMap<String, Integer> getPlayers() {
        return players;
    }

    public int getPlayerScore(String playerName) {
        return players.get(playerName);
    }

    public void setPlayerScore(String playerName, float score) {
        players.replace(playerName, Math.round(score));
        if (playerName != null && !playerName.equals("")) {
            Table t = uiTable.findActor(playerName);
            Label l = t.findActor(playerName + "_label");
            l.setText(Math.round(score));
        }
    }

    public void clear(){
        players.clear();
    }
}
