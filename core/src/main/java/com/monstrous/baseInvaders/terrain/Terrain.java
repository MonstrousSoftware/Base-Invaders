package com.monstrous.baseInvaders.terrain;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.Settings;

import java.util.HashMap;

// NOT USED!


public class Terrain implements Disposable {


    private HashMap<Integer, TerrainChunk> chunks;   // map of terrain chunk per grid point
    public Array<ModelInstance> instances; // model instances to be rendered

    public Terrain() {

        chunks = new HashMap<>();
        instances = new Array<>();

        int sideLength = 1; //(int)(Settings.worldSize / Settings.chunkSize);

        for(int cx = 0; cx < sideLength; cx++){
            for(int cz = 0; cz < sideLength; cz++){
                TerrainChunk chunk = new TerrainChunk(cx, cz);
                int key = makeKey(cx, cz);
                chunks.put(key, chunk);
                ModelInstance modelInstance = chunk.getModelInstance();
                modelInstance.transform.translate(cx*TerrainChunk.SCALE, 0, cz*TerrainChunk.SCALE);
                instances.add(modelInstance);
            }
        }
    }

    private int makeKey(int cx, int cz ){
        return cx + 1000*cz;
    }

//    public ModelInstance getModelInstance() {
//
//        return chunks.get(0).getModelInstance();
//    }

    public float getHeight(float x, float z) {
       return chunks.get(0).getHeight(x,z);
    }

    @Override
    public void dispose() {
        for(TerrainChunk chunk : chunks.values())
            chunk.dispose();
    }
}
