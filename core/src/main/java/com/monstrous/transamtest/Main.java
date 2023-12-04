package com.monstrous.transamtest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static Assets assets;

    @Override
    public void create() {
        Settings.supportControllers = (Gdx.app.getType() == Desktop);

        assets = new Assets();
        assets.finishLoading();
        setScreen( new GameScreen() );
    }

    @Override
    public void dispose() {
        assets.dispose();
        super.dispose();
    }
}
