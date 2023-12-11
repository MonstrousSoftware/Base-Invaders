package com.monstrous.transamtest.worlddata;

import com.badlogic.gdx.utils.Disposable;
import com.monstrous.transamtest.Settings;

// rocks, vegetation, etc.


public class Scenery  {

    private World world;
    public Scenery(World world ) {
        this.world = world;
    }

    public void populate() {

        // place all scenery in a model cache
        // we can delete all the items as game objects once we have the cache constructed.
        //
        placeRandom(world, "cactus", 300);
        placeRandom(world, "cactus.001", 200);
        placeRandom(world, "cactus.002", 200);
        placeRandom(world, "warningSign", 20);
        placeRandom(world, "squadronSign", 20);
    }

    private void placeRandom(World world, String name, int count){
        for(int n = 0; n < count; n++) {
            float xx = (float) (Math.random()-0.5f)*(Settings.worldSize-5f);    // don't plant trees to close to the edge
            float zz = (float) (Math.random()-0.5f)*(Settings.worldSize-5f);
            float r = (float) (Math.random()*360f);
            world.dropItem( name, xx, zz, r);
        }
    }

}
