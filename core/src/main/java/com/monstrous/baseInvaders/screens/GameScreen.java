package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.*;
import com.monstrous.baseInvaders.gui.GUI;
import com.monstrous.baseInvaders.input.CameraController;
import com.monstrous.baseInvaders.input.MyControllerAdapter;
import com.monstrous.baseInvaders.physics.CollisionShapeType;
import com.monstrous.baseInvaders.physics.PhysicsView;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.worlddata.GameObjectType;
import com.monstrous.baseInvaders.worlddata.Populator;
import com.monstrous.baseInvaders.terrain.TerrainChunk;
import com.monstrous.baseInvaders.worlddata.World;


public class GameScreen extends ScreenAdapter {

    private Main game;
    private GameView gameView;
    private MiniMap minimap;
    private PhysicsView physicsView;
    private GridView gridView;
    private InstrumentView instrumentView;
    private GUI gui;
    private World world;
    private int windowedWidth, windowedHeight;
    private boolean debugRender = !Settings.release;
    private boolean carSettingsWindow = false;
    private int techCollected = 0;
    private boolean autoCam = Settings.release;
    private MyControllerAdapter controllerAdapter;
    private Controller currentController;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);


        Gdx.input.setCatchKey(Input.Keys.F1, true);
        Gdx.input.setCatchKey(Input.Keys.F2, true);
        Gdx.input.setCatchKey(Input.Keys.F3, true);
        Gdx.input.setCatchKey(Input.Keys.F5, true);
        Gdx.input.setCatchKey(Input.Keys.F11, true);

        world = new World();

        Populator.populate(world);
        gameView = new GameView(world,false, 1.0f, 400f);
        ((CameraController)gameView.getCameraController()).autoCam = autoCam;
        //gameView.useFBO = !debugRender;

        physicsView = new PhysicsView(world);
        gridView = new GridView();
        minimap = new MiniMap(TerrainChunk.MAP_SIZE, TerrainChunk.MAP_SIZE);

        gui = new GUI(world.getPlayerCar(), world);


        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(gui.stage);
        im.addProcessor(gameView.getCameraController());
        im.addProcessor(world.getUserCarController());

        Gdx.app.log("No Controller enabled", "");

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


        instrumentView = new InstrumentView();
    }


    public void restart() {
        Populator.populate(world);
    }



    private void toggleFullScreen() {        // toggle full screen / windowed screen
        if (!Gdx.graphics.isFullscreen()) {
            windowedWidth = Gdx.graphics.getWidth();        // remember current width & height
            windowedHeight = Gdx.graphics.getHeight();
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            Gdx.graphics.setWindowedMode(windowedWidth, windowedHeight);
            resize(windowedWidth, windowedHeight);
        }
    }



    @Override
    public void render(float delta) {

        if(delta > 0.1f)    // in case we're running in the debugger
            delta = 0.1f;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            (currentController != null && currentController.getButton(currentController.getMapping().buttonX))) {
            if(!Settings.release)
                Gdx.app.exit();
            game.setScreen( new MainMenuScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)||
            (currentController != null && currentController.getButton(currentController.getMapping().buttonY)))
            restart();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            debugRender = !debugRender;
            //gameView.useFBO = false; //!debugRender;
        }
        if( world.stats.techCollected > techCollected) {
            gui.addTechIcon();
            techCollected++;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            autoCam = !autoCam;
            ((CameraController)gameView.getCameraController()).autoCam = autoCam;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            carSettingsWindow = !carSettingsWindow;
            gui.showCarSettings(carSettingsWindow);
        }

        world.update(delta);
        minimap.update(gameView.getCamera(), world);

        gameView.render(delta) ;


        if(debugRender) {
            //gridView.render(gameView.getCamera());
            physicsView.render(gameView.getCamera());
        }


        minimap.render();

        instrumentView.render(world.getPlayerCar());

        if(world.stats.levelComplete)
            gui.showLevelCompleted(true);
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
        dispose();
    }

    @Override
    public void dispose() {
        gameView.dispose();
        physicsView.dispose();
        gridView.dispose();
        world.dispose();
        gui.dispose();
        minimap.dispose();
        instrumentView.dispose();
    }
}
