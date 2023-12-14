package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Assets implements Disposable {

    public class AssetSounds {

        // constants for sounds in game
        public final Sound ENGINE;
        public final Sound BRAKE;
        public final Sound PICK_UP;


        public AssetSounds() {
            ENGINE = assets.get("sound/engine-6000.ogg");
            BRAKE = assets.get("sound/brake.ogg");
            PICK_UP = assets.get("sound/pick-up-sfx.ogg");

        }
    }

    public AssetSounds sounds;
    public Skin skin;
    public SceneAsset sceneAsset;
    public BitmapFont uiFont;

    private AssetManager assets;

    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.load("ui/uiskin.json", Skin.class);

        assets.load("font/Gasoek32.fnt", BitmapFont.class);

        assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        assets.load( Settings.GLTF_FILE, SceneAsset.class);

        assets.load("sound/engine-6000.ogg", Sound.class);
        assets.load("sound/brake.ogg", Sound.class);
        assets.load("sound/pick-up-sfx.ogg", Sound.class);
//        assets.load("sound/hit1.ogg", Sound.class);
//        assets.load("sound/jump1.ogg", Sound.class);
//        assets.load("sound/secret1.ogg", Sound.class);
//        assets.load("sound/upgrade1.ogg", Sound.class);
//        assets.load("sound/9mm-pistol-shoot-short-reverb-7152.mp3", Sound.class);

    }


    public boolean update() {
        return assets.update();
    }


    public void finishLoading() {
        assets.finishLoading();
        initConstants();
    }

    public float getProgress() {
        return assets.getProgress();
    }



    private void initConstants() {
        sounds = new AssetSounds();
        skin = assets.get("ui/uiskin.json");
        uiFont = assets.get("font/Gasoek32.fnt");
        sceneAsset = assets.get(Settings.GLTF_FILE);
//        scopeImage = assets.get("images/scope.png");
    }

    public <T> T get(String name ) {
        return assets.get(name);
    }

    @Override
    public void dispose() {
        Gdx.app.log("Assets dispose()", "");
        assets.dispose();
        assets = null;
    }
}
