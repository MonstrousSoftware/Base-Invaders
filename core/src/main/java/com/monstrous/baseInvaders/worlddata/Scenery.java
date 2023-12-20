package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.Settings;


// rocks, vegetation, etc.


public class Scenery implements Disposable {

    private World world;
    private ModelCache cache;
    private Array<ModelInstance> instances;

    public Scenery(World world ) {

        this.world = world;
        cache = new ModelCache();
        instances = new Array<>();
    }

    public void populate() {

        // place all scenery in a model cache
        // we can delete all the items as game objects once we have the cache constructed.
        //
        placeRandom(world, "cactus", 1800);
        placeRandom(world, "cactus.001", 1800);
        placeRandom(world, "cactus.002", 1800);
        placeRandom(world, "Stone1", 6800);
        placeRandom(world, "Stone2", 3800);
        placeRandom(world, "Stone3", 3800);
        placeRandom(world, "warningSign", 60);
        placeRandom(world, "squadronSign", 60);

        placeFences(world, "fence");

        // put all items in a model cache
        // this make a huge difference to the number of draw calls, and therefore performance
        cache.begin();
        cache.add(instances);
        cache.end();

        instances.clear();
    }

    public ModelCache getCache() {
        return cache;
    }


    private void placeRandom(World world, String name, int count){
        Vector3 tmpPosition = new Vector3();

        for(int n = 0; n < count; n++) {
            float x = (float) (Math.random())*(Settings.worldSize);
            float z = (float) (Math.random())*(Settings.worldSize);
            float r = (float) (Math.random()*360f);

            float y = world.terrain.getHeight(x, z);
            tmpPosition.set(x, y, z);
            ModelInstance modelInstance = world.spawnScenery( name,  tmpPosition);
            modelInstance.transform.rotate(Vector3.Y, r);
            instances.add(modelInstance);
        }
    }

    private Vector3 tmpPosition = new Vector3();

    private void placeFences(World world, String name){
        float fenceLength = 10f;
        for(float u = 0; u < Settings.worldSize; u+= fenceLength) {
            placeFence(world, name, u, 0, 0);
            placeFence(world, name, u, Settings.worldSize-.5f, 180);
            placeFence(world, name, 0, u, 90);
            placeFence(world, name, Settings.worldSize-0.5f, u, -90);
        }
    }

    private void placeFence(World world, String name, float x, float z, float r){
        float y = world.terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        ModelInstance modelInstance = world.spawnScenery( name,  tmpPosition);
        modelInstance.transform.rotate(Vector3.Y, r);
        instances.add(modelInstance);
    }

    @Override
    public void dispose() {
        cache.dispose();
    }
}
