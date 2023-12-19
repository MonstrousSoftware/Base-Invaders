package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.monstrous.baseInvaders.Assets;
import com.monstrous.baseInvaders.MusicManager;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.MyControllerMappings;
import com.monstrous.baseInvaders.terrain.Terrain;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;


public class Main extends Game {
    public static Assets assets;
    public static Terrain terrain;
    public ControllerToInputAdapter controllerToInputAdapter;
    public MusicManager musicManager;;

    @Override
    public void create() {
        Settings.supportControllers = (Gdx.app.getType() == Desktop);

        assets = new Assets();

        if (Settings.supportControllers) {
            controllerToInputAdapter = new ControllerToInputAdapter(new MyControllerMappings());
            // bind controller events to keyboard keys
            controllerToInputAdapter.addButtonMapping(MyControllerMappings.BUTTON_FIRE, Input.Keys.ENTER);
            controllerToInputAdapter.addAxisMapping(MyControllerMappings.AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
            Controllers.addListener(controllerToInputAdapter);
        }

        setScreen( new LoadScreen(this) );
    }

    public void onLoadingComplete(){
        assets.finishLoading();
        musicManager = new MusicManager(assets);

        terrain = new Terrain();
        if(Settings.release)
            setScreen( new MainMenuScreen(this) );
        else
            setScreen( new GameScreen(this) );
    }

    @Override
    public void dispose() {
        assets.dispose();
        terrain.dispose();
        super.dispose();
    }
}
