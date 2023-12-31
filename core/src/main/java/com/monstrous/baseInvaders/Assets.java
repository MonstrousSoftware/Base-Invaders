package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
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
        public final Sound MENU_CLICK;


        public AssetSounds() {
            ENGINE = assets.get("sound/engine-6000.ogg");
            BRAKE = assets.get("sound/brake.ogg");
            PICK_UP = assets.get("sound/pick-up-sfx.ogg");
            MENU_CLICK = assets.get("sound/click_002.ogg");

        }
    }

    public AssetSounds sounds;
    public Skin skin;
    public SceneAsset sceneAsset;
    public Music gameMusic;

    private AssetManager assets;

    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.load("skin/invaders.json", Skin.class);

        assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        assets.load( Settings.GLTF_FILE, SceneAsset.class);

        assets.load("sound/engine-6000.ogg", Sound.class);
        assets.load("sound/brake.ogg", Sound.class);
        assets.load("sound/pick-up-sfx.ogg", Sound.class);
        assets.load("sound/click_002.ogg", Sound.class);

        assets.load("images/title.png", Texture.class);

        assets.load("music/sunny-day-copyright-free-background-rock-music-for-vlog-129471.mp3", Music.class);

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
        skin = assets.get("skin/invaders.json");
        sceneAsset = assets.get(Settings.GLTF_FILE);

        gameMusic = assets.get("music/sunny-day-copyright-free-background-rock-music-for-vlog-129471.mp3");
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
