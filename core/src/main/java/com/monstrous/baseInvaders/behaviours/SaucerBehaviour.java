package com.monstrous.baseInvaders.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.physics.CollisionShapeType;
import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.worlddata.GameObjectType;
import com.monstrous.baseInvaders.worlddata.World;


// the ufo spirals down to a position close above ground, then waits a bit, drops tech and then spirals away again

public class SaucerBehaviour extends Behaviour {

    static final float ROTATION_SPEED = (float) (Math.PI/2f);	// per sec
    static final float DROP_SPEED = 2f;
    static final float RAISE_SPEED = 5f;

    public Vector3 focalPoint;
    private float height;
    private float distance;
    private float angle;
    private Vector3 pos;
    private float landTime;
    private float flyTime;
    private int phase = 0;	// 0 circle to land, 1 wait on ground, 2 circle away


    public SaucerBehaviour(GameObject go) {
        super(go);
        focalPoint = new Vector3();
        go.scene.modelInstance.transform.getTranslation(focalPoint);        // this is where we want to end up
        pos = new Vector3();
        distance = 20f;
        height = 20f;
    }

    @Override
    public void update(World world, float deltaTime ) {

        if(deltaTime > 0.1f)
            deltaTime = 0.1f;

        angle += ROTATION_SPEED * deltaTime;


        float x = (float)Math.sin(angle)*distance;
        float z = (float)Math.cos(angle)*distance;
        pos.set(x, height, z);
        pos.add(focalPoint);
        go.setPosition(pos);
        float rot = -45f*deltaTime;
        if(phase == 1)  // rotate in alternate direction in egg laying mode
            rot *= -4f;
        go.scene.modelInstance.transform.rotate(Vector3.Y, rot);

        if(phase == 0) {
            height -= deltaTime * DROP_SPEED;
            distance -= deltaTime * DROP_SPEED;
        }
        else if (phase == 1) {
            distance = 0;
            height = 0;
        }
        else if (phase == 2) {
            height += deltaTime * RAISE_SPEED;
            distance += deltaTime * RAISE_SPEED;
        }

        if(height <= 0 && phase == 0){
            phase = 1;
            landTime = 4f;
            Gdx.app.log("UFO going to phase 1","");
        } else if (phase == 1) {
            landTime -= deltaTime;

            if(landTime <= 0) {
                world.spawnObject(GameObjectType.TYPE_PICKUP_ITEM, "alienTech", null, CollisionShapeType.BOX, true, new Vector3(pos));
                Gdx.app.log("UFO going to phase 2","");
                phase = 2;
                flyTime = 0;
            }
        } else if (phase == 2) {
            flyTime += deltaTime;
            if(flyTime > 5f) {
                go.health = 0;
            }
        }

    }

}
