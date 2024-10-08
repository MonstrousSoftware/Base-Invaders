package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.worlddata.GameObjectType;
import com.monstrous.baseInvaders.worlddata.World;

public class MiniMap implements Disposable {
    private int mapSize;       // in pixels
    private int border;       // in pixels

    private OrthographicCamera orthoCam;
    private SpriteBatch batch;
    private SpriteBatch fboBatch;
    private FrameBuffer fboMiniMap;

    private Rectangle miniMapRect;
    private Rectangle mapFrameRect;
    private int viewWidth;
    private int viewHeight;
    private final Vector3 position;
    private Texture carMarker;
    private Texture jeepMarker;
    private Texture techMarker;

    public MiniMap(float worldWidth, float worldDepth) {

        mapSize = 200;  // pixels
        border = 10;    // pixels
        fboMiniMap = new FrameBuffer(Pixmap.Format.RGBA8888, mapSize, mapSize, true);
        carMarker = new Texture(Gdx.files.internal("textures/orangeMarker.png"));
        jeepMarker = new Texture(Gdx.files.internal("textures/purpleMarker.png"));
        techMarker = new Texture(Gdx.files.internal("textures/greenMarker.png"));
        miniMapRect = new Rectangle();
        mapFrameRect = new Rectangle();
        batch = new SpriteBatch();
        fboBatch = new SpriteBatch();
        fboBatch.getProjectionMatrix().setToOrtho2D(0, 0, mapSize, mapSize);
        //shapeRenderer = new ShapeRenderer();

        // ortho cam used for mini map
        float orthoCamHeight = 100f;
        float max = worldWidth;
        if(worldDepth > max)
            max = worldDepth;
        orthoCam = new OrthographicCamera( max, max );
        orthoCam.position.set(0,orthoCamHeight,0);
        orthoCam.lookAt(0,0,0);
        orthoCam.up.set(0,0,1);
        orthoCam.near = 1;
        orthoCam.far = 500;
        orthoCam.update();

        position = new Vector3();
    }

    public void resize(int viewWidth, int viewHeight) {

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        miniMapRect = new Rectangle();
        miniMapRect.width = mapSize;
        miniMapRect.height = mapSize;
        miniMapRect.x = viewWidth-(miniMapRect.width+border);
        miniMapRect.y = viewHeight-(miniMapRect.height+border+50);

        float wf = mapSize + 2*border;
        mapFrameRect = new Rectangle();
        mapFrameRect.width = wf;
        mapFrameRect.height = wf;
        mapFrameRect.x = viewWidth-wf;
        mapFrameRect.y = viewHeight-wf;

        batch.getProjectionMatrix().setToOrtho2D(0, 0, viewWidth, viewHeight);
    }

    private Vector3 carPosition = new Vector3();

    public void update(Camera cam , World world) {


        fboMiniMap.begin();
            Gdx.gl.glClearColor(0,0,0, 0.5f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            batch.begin();

            float cx = viewWidth/2f;
            float cy = viewHeight/2f;

            carPosition.set( world.getPlayer().getPosition() );
            Vector3 carDirection =  world.getPlayer().getDirection();
            float angle = (float)Math.atan2(carDirection.z, carDirection.x);

            for(int i = 0; i < world.getNumGameObjects(); i++) {
                GameObject go = world.getGameObject(i);
                if(go.type.isCar || go.type == GameObjectType.TYPE_PICKUP_ITEM) {

                    if(go.health <=0)       // hide dead objects from map
                        continue;
                    position.set(go.getPosition());

                    float wx = (position.x - carPosition.x)/ Settings.worldSize;
                    float wz = (position.z - carPosition.z)/ Settings.worldSize;

                    float y = wx*(float)Math.cos(angle)+wz*(float)Math.sin(angle);
                    float x = -wx*(float)Math.sin(angle)+wz*(float)Math.cos(angle);

                    x *= viewWidth;
                    y *= viewHeight;
                    Texture symbol = techMarker;
                    if (go.type == GameObjectType.TYPE_PLAYER)
                        symbol = carMarker;
                    else if (go.type == GameObjectType.TYPE_ENEMY_CAR)
                        symbol = jeepMarker;

                    if (symbol != null)
                        batch.draw(symbol, cx+x, cy+y, viewWidth/32f, viewWidth/32f);
                }
            }

            batch.end();


        fboMiniMap.end();
    }

    public void render() {
        batch.begin();
		TextureRegion s = new TextureRegion(fboMiniMap.getColorBufferTexture());
		s.flip(false, true); // coordinate system in buffer differs from screen

		batch.draw(s, miniMapRect.x,  miniMapRect.y,  miniMapRect.width,  miniMapRect.height);
		batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
