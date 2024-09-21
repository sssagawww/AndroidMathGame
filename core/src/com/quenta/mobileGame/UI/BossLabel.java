package com.quenta.mobileGame.UI;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingConfig;
import com.github.tommyettinger.textra.TypingLabel;

public class BossLabel extends Table {
    private ProgressBar hpBar;
    private final Table hpTable;
    private final Table btnsTable;
    private final Table timeTable;
    private final TextButton.TextButtonStyle style;
    private TextButton mathBtn;
    private TextButton paintBtn;
    private TextButton rhythmBtn;
    private ATTACK_STATES state = ATTACK_STATES.NON_ATTACK;
    private final Cell cell;

    public enum ATTACK_STATES {
        MATH_ATTACK,
        RHYTHM_ATTACK,
        PAINT_ATTACK,
        NON_ATTACK
    }

    public BossLabel(Skin skin) {
        super(skin);
        Table uiTable = new Table();
        this.add(uiTable).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight());

        BitmapFont labelFont = getSkin().getFont("font");
        labelFont.getData().setScale(1.2f);
        Label.LabelStyle lstyle = new Label.LabelStyle(labelFont, Color.BLACK);
        style = new TextButton.TextButtonStyle();
        style.font = labelFont;
        style.font.getData().setScale(1.2f);
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        hpTable = new Table(skin);
        btnsTable = new Table();

        TypingConfig.GLOBAL_VARS.put("COLD", "{OCEAN=0.7;0.25;0.11;0.20;1.35}");
        TypingConfig.GLOBAL_VARS.put("ENDCOLD", "{ENDOCEAN}");

        Font font = new Font(getSkin().getFont("font"));
        font.scale(1.2f,1.2f);
        TypingLabel label1 = new TypingLabel("{VAR=COLD}Азрот Поглотитель{VAR=ENDCOLD}", font);
        label1.addAction(sequence(alpha(0f), fadeIn(2f)));
        label1.setAlignment(Align.center);

        Table labelTable = new Table(skin);
        labelTable.setBackground("label");
        labelTable.add(label1).width(Gdx.graphics.getWidth() / 2f).center().padTop(25f);

        Image timeImage = new Image(new Texture("UI/artefacts.png"));
        Label label = new Label("Воспользоваться артефактами\nи остановить тьму", lstyle);
        label.setAlignment(Align.center);
        label.setColor(Color.BLACK);

        timeTable = new Table(skin);
        timeTable.setBackground(skin.getDrawable("GUI_img"));
        timeTable.add(timeImage).width(Gdx.graphics.getHeight() / 8f).height(Gdx.graphics.getHeight() / 8f).expand().padRight(15f).left();
        timeTable.add(label).width(Gdx.graphics.getWidth() / 2f).height(Gdx.graphics.getHeight() / 8f).expand().right();
        timeTable.setVisible(false);

        hpTable.add(labelTable).height(Gdx.graphics.getHeight() / 10f).row();
        createBars();
        uiTable.add(hpTable).top().expand().row();
        createBtns();
        uiTable.add(btnsTable).bottom().padBottom(Gdx.graphics.getHeight() / 10f).expand();
        cell = uiTable.getCell(btnsTable);
    }

    private void createBars() {
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle(getSkin().getDrawable("GUI_img"), getSkin().getDrawable("blue"));
        progressBarStyle.background.setMinHeight(Gdx.graphics.getHeight()/18f);
        progressBarStyle.knobBefore = getSkin().getDrawable("blue");
        progressBarStyle.knobBefore.setMinHeight(Gdx.graphics.getHeight()/36f);
        hpBar = new ProgressBar(0, 100, .1f, false, progressBarStyle);
        hpBar.setAnimateDuration(.15f);
        hpTable.add(hpBar).width(Gdx.graphics.getWidth() / 2f).height(Gdx.graphics.getHeight()/16f).row();
        hpBar.setValue(100f);
    }

    private void createBtns() {
        mathBtn = addBtn("Использовать\nзнания", ATTACK_STATES.MATH_ATTACK);
        paintBtn = addBtn("Провести\nпалочкой", ATTACK_STATES.PAINT_ATTACK);
        rhythmBtn = addBtn("Взмахнуть\nмечом", ATTACK_STATES.RHYTHM_ATTACK);

        btnsTable.add(mathBtn).align(Align.bottom).width(Gdx.graphics.getWidth() / 3.5f).height(Gdx.graphics.getHeight() / 8f).space(25f);
        btnsTable.add(paintBtn).align(Align.bottom).width(Gdx.graphics.getWidth() / 3.5f).height(Gdx.graphics.getHeight() / 8f).space(25f);
        btnsTable.add(rhythmBtn).align(Align.bottom).width(Gdx.graphics.getWidth() / 3.5f).height(Gdx.graphics.getHeight() / 8f).space(25f);
    }

    private TextButton addBtn(String btnText, final ATTACK_STATES newState) {
        TextButton btn = new TextButton(btnText, style);
        style.up = getSkin().getDrawable("GUI_img");
        style.down = getSkin().getDrawable("GUI_img");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                state = newState;
            }
        });

        return btn;
    }

    public ATTACK_STATES getState() {
        return state;
    }

    public void setState(ATTACK_STATES state) {
        this.state = state;
    }

    public void changeCell() {
        cell.setActor(timeTable);
    }

    public TextButton getMathBtn() {
        return mathBtn;
    }

    public TextButton getPaintBtn() {
        return paintBtn;
    }

    public TextButton getRhythmBtn() {
        return rhythmBtn;
    }

    public ProgressBar getHpBar() {
        return hpBar;
    }

    public Table getTimeTable() {
        return timeTable;
    }
}