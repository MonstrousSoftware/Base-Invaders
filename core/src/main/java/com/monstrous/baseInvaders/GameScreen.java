package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.gui.GUI;
import com.monstrous.baseInvaders.input.CameraController;
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
    private GameView instrumentView;
    private MiniMap minimap;
    private PhysicsView physicsView;
    private GridView gridView;
    private GUI gui;
    private World world;
    private World instrumentWorld;
    private int windowedWidth, windowedHeight;
    private boolean debugRender = false;
    private boolean carSettingsWindow = false;
    private int techCollected = 0;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        Gdx.input.setCatchKey(Input.Keys.F1, true);
        Gdx.input.setCatchKey(Input.Keys.F2, true);
        Gdx.input.setCatchKey(Input.Keys.F3, true);
        Gdx.input.setCatchKey(Input.Keys.F11, true);

        world = new World();

        Populator.populate(world);
        gameView = new GameView(world,false, 1.0f, 300f);

        physicsView = new PhysicsView(world);
        gridView = new GridView();
        minimap = new MiniMap(TerrainChunk.MAP_SIZE, TerrainChunk.MAP_SIZE);

        gui = new GUI(world.getUserCarController(), world);


        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(gui.stage);
        im.addProcessor(gameView.getCameraController());
        im.addProcessor(world.getUserCarController());

        Gdx.app.log("No Controller enabled", "");

        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
//        Gdx.input.setCursorCatched(true);
//        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);



        instrumentWorld = new World();

        instrumentWorld.spawnObject(GameObjectType.TYPE_STATIC, "dial", null, CollisionShapeType.BOX, false, new Vector3(1,1,1));
        instrumentView = new GameView(instrumentWorld,true, 0.1f, 10f);
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

    private boolean autoCam = false;

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
        if (Gdx.input.isKeyJustPressed(Input.Keys.R))
            restart();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1))
            debugRender = !debugRender;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2) || world.stats.techCollected > techCollected) {
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

        //float moveSpeed = world.getPlayer().body.getVelocity().len();
        gameView.render(delta) ;

        instrumentView.render(delta) ;

        if(debugRender) {
            //gridView.render(gameView.getCamera());
            physicsView.render(gameView.getCamera());
        }


        minimap.render();

        gui.render(delta);

    }

    @Override
    public void resize(int width, int height) {

        gameView.resize(width, height);
        gui.resize(width, height);
        minimap.resize(width, height);
    }


    @Override
    public void hide() {
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
    }
}
