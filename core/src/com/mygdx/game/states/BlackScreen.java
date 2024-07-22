package com.mygdx.game.states;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import com.mygdx.game.handlers.GameStateManager;

import static com.mygdx.game.handlers.GameStateManager.*;

import java.util.ArrayList;


public class BlackScreen extends GameState {
    private String charName = "{VAR=FIRE}Элиас{VAR=ENDFIRE}";
    private InputMultiplexer multiplexer;
    private Stage uiStage;
    private Font font = new Font(String.valueOf(Gdx.files.internal("mcRus.fnt")));
    private TypingLabel label;
    private float time = 0;
    private float animatedTime;
    private ArrayList<String> phrases;
    private int curPhrase = 0;
    private static boolean finalTitles;

    public BlackScreen(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        phrases = new ArrayList<>();
        createPhrases();
        if(finalTitles){
            phrases.clear();
            createCredits();
        }
        initUI();

        gsm.setLastState(BLACK_SCREEN);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        time += dt;

        if (time >= animatedTime + 1.5f || Gdx.input.justTouched()) {
            time = 0;
            setLabelText();
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiStage.draw();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }

    private void setLabelText() {
        curPhrase = (curPhrase + 1) % phrases.size();
        if (curPhrase == 0) {
            label.clearActions();//фикс этой ошибки
            if(finalTitles){
                gsm.setState(MENU);
            } else {
                game.save = false;
                gsm.setState(NEW_GAME);
            }
        } else if (curPhrase + 1 == phrases.size()) {
            label.addAction(sequence(fadeOut(3f)));//крашает игру, если прокликивать при первом запуске
        }
        label.restart(phrases.get(curPhrase));
        animatedTime = label.getTextSpeed() * phrases.get(curPhrase).length();
    }

    public void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        ///font.scale(font.scaleX * 1.5f, font.scaleY * 1.5f);
        label = new TypingLabel(phrases.get(curPhrase), font);
        label.addAction(sequence(alpha(0f), fadeIn(2f)));
        label.setAlignment(Align.center);
        root.add(label);

        animatedTime = label.getTextSpeed() * phrases.get(curPhrase).length();

        multiplexer.addProcessor(uiStage);
    }

    private void createPhrases() {
        phrases.add("Давным-давно в далеких землях,\n \nпод звездами и среди волшебных лесов...");
        phrases.add("Рос и познавал мир " + charName + ", \n \nюный путешественник.");
        phrases.add("Он обладал редким даром - \n \nуправлять временем.");
        phrases.add("Однажды он получил письмо от своего наставника,\n\nв котором говорилось о древнем пророчестве.");
        phrases.add(" \"Только собрав 3 могущественных артефакта,\n\nможно спасти мир от катастрофы. \" ");
        phrases.add("И " + charName + " отправился в путешествие...");
    }

    private void createCredits() {
        phrases.add("Темные силы повержены,\n\nи мир вновь обрел покой.");
        phrases.add(charName + " возвращается домой,\n\nгде его встречают как великого спасителя.");
        phrases.add("Пусть и ваш путь освещается\n\nсветом надежды и доблести.");
        phrases.add("Конец.");
    }

    public static boolean isFinalTitles() {
        return finalTitles;
    }

    public static void setFinalTitles(boolean finalTitles) {
        BlackScreen.finalTitles = finalTitles;
    }
}
