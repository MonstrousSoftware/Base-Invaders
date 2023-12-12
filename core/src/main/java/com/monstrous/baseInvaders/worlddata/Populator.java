package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.physics.CollisionShapeType;


public class Populator {

    public static void populate(World world) {
        world.clear();
        // use resetPosition=false for objects you have placed in Blender and true for objects you'll move around in code.
        //
        //world.spawnObject(GameObjectType.TYPE_STATIC, "groundbox", null, CollisionShapeType.MESH, false, Vector3.Zero);

        world.spawnObject(GameObjectType.TYPE_TERRAIN, "groundbox", null, CollisionShapeType.MESH, false, Vector3.Zero);


        // world.spawnObject(GameObjectType.TYPE_STATIC, "ramp", null, CollisionShapeType.MESH, false, Vector3.Zero);

//         world.spawnObject(GameObjectType.TYPE_STATIC, "cactus", null, CollisionShapeType.CYLINDER, false, new Vector3(12,3,5));
//
//        world.spawnObject(GameObjectType.TYPE_STATIC, "cactus.001", null, CollisionShapeType.CYLINDER, false, new Vector3(18,3,5));
//
//        world.spawnObject(GameObjectType.TYPE_STATIC, "cactus.002", null, CollisionShapeType.CYLINDER, false, new Vector3(22,3,5));

      //  world.spawnObject(GameObjectType.TYPE_PICKUP_FLAG, "flag", null, CollisionShapeType.CYLINDER, true,new Vector3(32,2,5));

        world.spawnObject(GameObjectType.TYPE_STATIC, "ufo", null, CollisionShapeType.SPHERE, true, new Vector3(32, 8, 40));

        world.spawnObject(GameObjectType.TYPE_PICKUP_ITEM, "alienTech", null, CollisionShapeType.BOX, true, new Vector3(32, 2, 5));

        world.spawnObject(GameObjectType.TYPE_STATIC, "warningSign", null, CollisionShapeType.BOX, true, new Vector3(12, 2, 15));


        world.spawnObject(GameObjectType.TYPE_DYNAMIC, "wheel", null, CollisionShapeType.CYLINDER, true,new Vector3(8,1,5));

        Vector3 carPos = new Vector3(5,3,5);

        GameObject go = world.spawnObject(GameObjectType.TYPE_PLAYER, "mustang","mustangProxy", CollisionShapeType.MESH, true, carPos);
        world.setPlayer(go);
    }
}
