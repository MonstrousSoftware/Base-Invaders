package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


// on web this screen is to get a user key press, so we can start playing sound
// also shows progress on asset loading

public class LoadScreen extends StdScreenAdapter {

    private Main game;
    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private Texture texture;
    private Texture titleTexture;
    private boolean loaded;
    private Label prompt;


    public LoadScreen(Main game) {
        Gdx.app.log("LoadScreen constructor", "");
        this.game = game;

    }


    @Override
    public void show() {
        Gdx.app.log("LoadScreen show()", "");

        skin = new Skin(Gdx.files.internal("skin/invaders.json"));
        stage = new Stage(new ScreenViewport());

        progressBar = new ProgressBar(0f, 1.0f, 0.01f, false, skin);
        progressBar.setSize(300, 50);
        progressBar.setValue(0);

        Label textLabel = new Label("Monstrous Software",skin,"default" );
        textLabel.setAlignment(Align.center);

        texture =  new Texture(Gdx.files.internal("images/monstrous.png"));
        Image logo = new Image( new TextureRegion(texture));

        titleTexture =  new Texture(Gdx.files.internal("images/title.png"));
        Image gameLogo = new Image( new TextureRegion(titleTexture));

        prompt = new Label("Continue",skin, "default");
        prompt.setColor(Color.DARK_GRAY);
        prompt.setVisible(false);

        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(gameLogo).pad(10).row();
        screenTable.add(logo).pad(10).row();
        screenTable.add(textLabel).pad(10).row();
        screenTable.add(progressBar).row();
        screenTable.add(prompt).pad(60);
        screenTable.pack();

        stage.addActor(screenTable);
        loaded = false;

    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        // load assets asynchronously
        if(!loaded) {
            loaded = game.assets.update();
            float fraction = game.assets.getProgress();
            progressBar.setValue(fraction);
        }
        else {
            if(Gdx.app.getType() != Application.ApplicationType.WebGL){
                game.onLoadingComplete();
                return;
            }
            prompt.setVisible(true);
            if( Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                game.onLoadingComplete();
                return;
            }
        }

        ScreenUtils.clear(.2f, .5f, .6f, 1f);
        stage.act(deltaTime);
        stage.draw();

    }

    @Override
    public void resize(int w, int h) {

        Gdx.app.log("LoadScreen resize()", "");
        stage.getViewport().update(w, h, true);

    }

    @Override
    public void hide() {
        Gdx.app.log("LoadScreen hide()", "");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("LoadScreen dispose()", "");
        stage.dispose();
        skin.dispose();
        texture.dispose();
        titleTexture.dispose();
    }

}
