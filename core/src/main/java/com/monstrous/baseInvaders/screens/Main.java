package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.monstrous.baseInvaders.Assets;
import com.monstrous.baseInvaders.Settings;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;


public class Main extends Game {
    public static Assets assets;

    @Override
    public void create() {
        Settings.supportControllers = (Gdx.app.getType() == Desktop);

        assets = new Assets();

        setScreen( new LoadScreen(this) );
    }

    public void onLoadingComplete(){
        assets.finishLoading();
        setScreen( new GameScreen(this) );
    }

    @Override
    public void dispose() {
        assets.dispose();
        super.dispose();
    }
}
