package com.monstrous.baseInvaders.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.Settings;

import java.util.HashMap;


public class Terrain implements Disposable {


    private HashMap<Integer, TerrainChunk> chunks;   // map of terrain chunk per grid point
    public Array<ModelInstance> instances; // model instances to be rendered
    private SpriteBatch batch;

    public Terrain() {
        Gdx.app.log("terrain", "generate...");

        chunks = new HashMap<>();
        instances = new Array<>();

        int sideLength = (int) (Settings.worldSize / Settings.chunkSize);
        int off = 0; //-(sideLength-1)/2;    // to center on chunk (0,0)
        Gdx.app.log("terrain", ""+sideLength+" x "+sideLength+ " chunks");

        for (int cx = off; cx < sideLength+off; cx++) {
            for (int cz = off; cz < sideLength+off; cz++) {
                TerrainChunk chunk = new TerrainChunk(cx, cz);
                //Gdx.app.error("generate chunk", "x:"+cx+", z:"+cz);
                int key = makeKey(cx, cz);
                chunks.put(key, chunk);
                ModelInstance modelInstance = chunk.getModelInstance();
                modelInstance.transform.translate(cx * Settings.chunkSize, 0, cz * Settings.chunkSize);
                instances.add(modelInstance);
            }
        }

        batch = new SpriteBatch();
    }

    private int makeKey(int cx, int cz) {
        return cx + 1000 * cz;
    }

//    public ModelInstance getModelInstance() {
//
//        return chunks.get(0).getModelInstance();
//    }

    public float getHeight(float x, float z) {
        int cx = Math.round(x/Settings.chunkSize);
        int cz = Math.round(z/Settings.chunkSize);
        int key = makeKey(cx, cz);
        TerrainChunk chunk = chunks.get(key);
        if(chunk == null){
            Gdx.app.error("position outside chunks", "x:"+x+", z:"+z);
            return 0;
        }
        return chunks.get(key).getHeight(x - cx*Settings.chunkSize, z - cz*Settings.chunkSize);
    }

    public void render() {
        batch.begin();

        for(TerrainChunk chunk : chunks.values()) {
            batch.draw(chunk.getHeightMapTexture(), chunk.coord.y*(TerrainChunk.MAP_SIZE+1), (3-chunk.coord.x)*(TerrainChunk.MAP_SIZE+1));
        }

        batch.end();

    }

    @Override
    public void dispose() {
        for(TerrainChunk chunk : chunks.values())
            chunk.dispose();
    }
}
