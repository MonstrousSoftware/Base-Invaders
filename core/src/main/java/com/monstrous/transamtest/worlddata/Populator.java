package com.monstrous.transamtest.worlddata;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.transamtest.physics.CollisionShapeType;


public class Populator {

    public static void populate(World world) {
        world.clear();
        // use resetPosition=false for objects you have placed in Blender and true for objects you'll move around in code.
        //
        world.spawnObject(GameObjectType.TYPE_STATIC, "groundbox", null, CollisionShapeType.BOX, false, Vector3.Zero);

        world.spawnObject(GameObjectType.TYPE_STATIC, "ramp", null, CollisionShapeType.MESH, false, Vector3.Zero);


        world.spawnObject(GameObjectType.TYPE_DYNAMIC, "wheel", null, CollisionShapeType.CYLINDER, true,new Vector3(8,1,5));

        Vector3 carPos = new Vector3(5,1,5);

        GameObject go = world.spawnObject(GameObjectType.TYPE_PLAYER, "mustang",null, CollisionShapeType.BOX, true, carPos);
        world.setPlayer(go);
    }
}
