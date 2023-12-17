package com.monstrous.baseInvaders.worlddata;

import com.monstrous.baseInvaders.Settings;

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
        placeRandom(world, "cactus", 9000);
        placeRandom(world, "cactus.001", 90);
        placeRandom(world, "cactus.002", 90);
        placeRandom(world, "warningSign", 20);
        placeRandom(world, "squadronSign", 20);
    }

    private void placeRandom(World world, String name, int count){
        for(int n = 0; n < count; n++) {
            float xx = (float) (Math.random())*(Settings.worldSize);    // don't plant trees to close to the edge
            float zz = (float) (Math.random())*(Settings.worldSize);
            float r = (float) (Math.random()*360f);
            world.dropItem( name, xx, zz, r);
        }
    }

}
