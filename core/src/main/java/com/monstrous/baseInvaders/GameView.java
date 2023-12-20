package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.input.CameraController;
import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.worlddata.World;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
//import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;

public class GameView implements Disposable {

    private final World world;                                // reference to World
    private final SceneManager sceneManager;
    private final PerspectiveCamera cam;
    private final Cubemap diffuseCubemap;
    private final Cubemap environmentCubemap;
    private final Cubemap specularCubemap;
    private final Texture brdfLUT;
    private SceneSkybox skybox;
    private DirectionalShadowLight shadowLight;
    private final CameraController camController;
    private final boolean isOverlay;
    private CascadeShadowMap csm = null;
    private ParticleEffects particleEffects = null;

    // if the view is an overlay, we don't clear screen on render, only depth buffer
    //
    public GameView(World world, boolean overlay, float near, float far) {
        this.world = world;
        this.isOverlay = overlay;

        sceneManager = new SceneManager();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
        cam.position.set(0f, Settings.eyeHeight, 0f);
        cam.lookAt(0,Settings.eyeHeight,10f);
        cam.near = near;
        cam.far = far;
        cam.update();

        sceneManager.setCamera(cam);
        camController = new CameraController(cam);

//        csm = new CascadeShadowMap(2);
//        sceneManager.setCascadeShadowMap(csm);

        // setup light
        int viewPortSize = 64;  // smaller value gives sharper shadow
        shadowLight = new DirectionalShadowLight(Settings.shadowMapSize, Settings.shadowMapSize)
            .setViewport(viewPortSize,viewPortSize,1f,100);
        shadowLight.direction.set(1, -3, 1).nor();

        shadowLight.color.set(Color.WHITE);
        shadowLight.intensity = 5f;
        sceneManager.environment.add(shadowLight);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(shadowLight);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(0.1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 1f/256f)); // reduce shadow acne

        // setup skybox
        if(!isOverlay) {
            skybox = new SceneSkybox(environmentCubemap);
            sceneManager.setSkyBox(skybox);

//            particleEffects = new ParticleEffects(cam);
//            particleEffects.addFire( new Vector3(Settings.worldSize/2,8,Settings.worldSize/2));
        }
    }


    public PerspectiveCamera getCamera() {
        return cam;
    }

    public void setFieldOfView( float fov ){
        cam.fieldOfView = fov;
        cam.update();
    }

    public InputProcessor getCameraController() {
        return camController;
    }


    private Vector3 pos = new Vector3();

    public void refresh(Camera cam) {

        sceneManager.getRenderableProviders().clear();        // remove all scenes

        sceneManager.getRenderableProviders().add(world.scenery.getCache());        /// add model cache for scenery items

        int count = 0;
        // add scene for each game object

        int num = world.getNumGameObjects();
        for(int i = 0; i < num; i++){
            GameObject go = world.getGameObject(i);
            if (!go.visible || go.scene == null)
                continue;

            go.boundingBox.getCenter(pos);
            pos.add( go.getPosition() );

            if(!go.type.isStatic || cam.frustum.boundsInFrustum(pos, go.dimensions)) {
                Scene scene = world.getGameObject(i).scene;
                sceneManager.addScene(scene, false);
                count++;
            }
        }
        world.stats.itemsRendered = count;
    }

    public void render(float delta ) {

        // animate camera
        if(!isOverlay) {
            camController.update(delta, world.getPlayer());

//        DirectionalShadowLight shadowLight = sceneManager.getFirstDirectionalShadowLight();
//            csm.setCascades(sceneManager.camera, shadowLight, 400f, 3f);

            refresh(cam);
            shadowLight.setCenter(cam.position);
            sceneManager.update(delta);
        }
//        sceneManager.renderShadows();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);   // clear depth buffer only

        sceneManager.renderColors();

        if (!isOverlay) {
//            particleEffects.update(delta);
//            particleEffects.render();
        }
            // post-processing of game screen content : vignette effect and underwater wavy effect

    }


    public void resize(int width, int height){
        sceneManager.updateViewport(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }



    @Override
    public void dispose() {
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        if(!isOverlay) {
            skybox.dispose();
//            particleEffects.dispose();
        }
    }
}
