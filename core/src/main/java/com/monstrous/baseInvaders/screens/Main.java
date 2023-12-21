package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.monstrous.baseInvaders.Assets;
import com.monstrous.baseInvaders.MusicManager;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.MyControllerMappings;
import com.monstrous.baseInvaders.leaderboard.GameJolt;
import com.monstrous.baseInvaders.leaderboard.LeaderBoardEntry;
import com.monstrous.baseInvaders.terrain.Terrain;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;


public class Main extends Game {
    public static Assets assets;
    public static Terrain terrain;
    public ControllerToInputAdapter controllerToInputAdapter;
    public MusicManager musicManager;
    public Array<LeaderBoardEntry> leaderBoard;
    public GameJolt gameJolt;
    private Preferences preferences;
    public String userName;

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

        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        userName = preferences.getString("username", "Player 1");

        leaderBoard = new Array<>();
        if( Gdx.app.getType() != Application.ApplicationType.WebGL) {
            gameJolt = new GameJolt();              // disabled because doesn't work on web (teavm) version
            gameJolt.init(leaderBoard);
        }

        setScreen( new LoadAssetsScreen(this) );
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
        // save username for next time
        preferences.putString("username", userName);   // save
        preferences.flush();

        assets.dispose();
        terrain.dispose();
        super.dispose();
    }
}
