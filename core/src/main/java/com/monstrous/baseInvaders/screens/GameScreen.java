package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.monstrous.baseInvaders.*;
import com.monstrous.baseInvaders.gui.GUI;
import com.monstrous.baseInvaders.input.CameraController;
import com.monstrous.baseInvaders.input.MyControllerAdapter;
import com.monstrous.baseInvaders.physics.PhysicsView;
import com.monstrous.baseInvaders.worlddata.Populator;
import com.monstrous.baseInvaders.terrain.TerrainChunk;
import com.monstrous.baseInvaders.worlddata.World;


public class GameScreen extends StdScreenAdapter {

    private Main game;
    private GameView gameView;
    private MiniMap minimap;
    private PhysicsView physicsView;
    private GridView gridView;
    private InstrumentView instrumentView;
    private GUI gui;
    private World world;
    private boolean debugRender = !Settings.release;
    private boolean carSettingsWindow = false;
    private boolean autoCam = Settings.release;
    private MyControllerAdapter controllerAdapter;
    private Controller currentController;
    public GLProfiler glProfiler;
    public boolean doProfiling = false;

    public GameScreen(Main game) {

        this.game = game;

        world = new World();
        Populator.populate(world);

        if(doProfiling){
            glProfiler = new GLProfiler(Gdx.graphics);
            glProfiler.enable();
        }
    }

    @Override
    public void show() {

        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        Gdx.input.setCatchKey(Input.Keys.F2, true);
        Gdx.input.setCatchKey(Input.Keys.F4, true);
        Gdx.input.setCatchKey(Input.Keys.F9, true);
        Gdx.input.setCatchKey(Input.Keys.F10, true);
        Gdx.input.setCatchKey(Input.Keys.F11, true);

        gameView = new GameView(world,false, 1.0f, 1000f);
        ((CameraController)gameView.getCameraController()).autoCam = autoCam;

        physicsView = new PhysicsView(world);
        gridView = new GridView();
        minimap = new MiniMap(TerrainChunk.MAP_SIZE, TerrainChunk.MAP_SIZE);

        // controller
        if (Settings.supportControllers) {
            currentController = Controllers.getCurrent();
            if (currentController != null) {
                Gdx.app.log("current controller", currentController.getName());
                controllerAdapter = new MyControllerAdapter(world.getUserCarController());
                // we define a listener that listens to all controllers, in case the current controller gets disconnected and reconnected
                Controllers.removeListener(game.controllerToInputAdapter);
                Controllers.addListener(controllerAdapter);
            } else
                Gdx.app.log("current controller", "none");
        }


        gui = new GUI(game, world.getPlayerCar(), world);


        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(gui.stage);
        im.addProcessor(gameView.getCameraController());
        im.addProcessor(world.getUserCarController());

        instrumentView = new InstrumentView();
        if(Settings.musicOn)
            game.musicManager.startMusic("music/sunny-day-copyright-free-background-rock-music-for-vlog-129471.mp3", true);


        world.scenery.populate();   // adjust to current performance settings
    }


    private void showLeaderBoard() {
        Gdx.input.setCursorCatched(false);
        gui.showLeaderBoard();
    }


    @Override
    public void render(float delta) {
        if(doProfiling)
            glProfiler.reset();

        super.render(delta);
        if(delta > 0.1f)    // in case we're running in the debugger
            delta = 0.1f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            (currentController != null && currentController.getButton(currentController.getMapping().buttonX))) {
            if(!Settings.release)
                Gdx.app.exit();
            game.setScreen( new PauseMenuScreen(game, this));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.HOME)||
            (currentController != null && currentController.getButton(currentController.getMapping().buttonY))) {
            game.setScreen(new PreGameScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            debugRender = !debugRender;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            autoCam = !autoCam;
            ((CameraController)gameView.getCameraController()).autoCam = autoCam;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            carSettingsWindow = !carSettingsWindow;
            gui.showCarSettings(carSettingsWindow);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            Settings.showFPS = !Settings.showFPS;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            showLeaderBoard();
        }

//        if( world.stats.techCollected > gui.numTechItems) {
//            gui.addTechIcon(true);
//        }

        world.update(delta);
        minimap.update(gameView.getCamera(), world);

        gameView.render(delta) ;

        if(debugRender) {
            //gridView.render(gameView.getCamera());
            physicsView.render(gameView.getCamera());
        }
        minimap.render();
        instrumentView.render(world.getPlayerCar());

        if(doProfiling) {
            Gdx.app.log("draw calls", "drawcalls:" + glProfiler.getDrawCalls()
                + "tex binds:" + glProfiler.getTextureBindings() + " sdr swtch:" + glProfiler.getShaderSwitches() + " items rendered:" + world.stats.itemsRendered);
        }

        if(world.stats.gameCompleted && Settings.musicOn)
            game.musicManager.stopMusic();
        gui.showLevelCompleted(world.stats.gameCompleted);
        gui.render(delta);
    }

    @Override
    public void resize(int width, int height) {

        gameView.resize(width, height);
        gui.resize(width, height);
        minimap.resize(width, height);
        instrumentView.resize(width, height);
    }


    @Override
    public void hide() {
        if(currentController != null) {
            Controllers.removeListener(controllerAdapter);
            Controllers.addListener(game.controllerToInputAdapter);
        }
        Gdx.input.setCursorCatched(false);
        if(Settings.musicOn)
            game.musicManager.stopMusic();

        gameView.dispose();
        physicsView.dispose();
        gridView.dispose();
        gui.dispose();
        minimap.dispose();
        instrumentView.dispose();
        Main.assets.sounds.ENGINE.stop();
    }

    @Override
    public void dispose() {
        world.dispose();
        if(doProfiling)
            glProfiler.disable();
    }
}
