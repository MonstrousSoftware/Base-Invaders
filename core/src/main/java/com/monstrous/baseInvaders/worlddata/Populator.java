package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.physics.CollisionShapeType;


public class Populator {

    public static void populate(World world) {


        world.clear();

        //world.spawnObject(GameObjectType.TYPE_UFO, "ufo", null, CollisionShapeType.SPHERE, true, new Vector3(32, 8, 40));


        // use resetPosition=false for objects you have placed in Blender and true for objects you'll move around in code.
        //
        //world.spawnObject(GameObjectType.TYPE_STATIC, "groundbox", null, CollisionShapeType.MESH, false, Vector3.Zero);

        world.spawnObject(GameObjectType.TYPE_TERRAIN, "groundbox", null, CollisionShapeType.MESH, false, Vector3.Zero, 1f);


        // world.spawnObject(GameObjectType.TYPE_STATIC, "ramp", null, CollisionShapeType.MESH, false, Vector3.Zero);

//         world.spawnObject(GameObjectType.TYPE_STATIC, "cactus", null, CollisionShapeType.CYLINDER, false, new Vector3(12,3,5));
//
//        world.spawnObject(GameObjectType.TYPE_STATIC, "cactus.001", null, CollisionShapeType.CYLINDER, false, new Vector3(18,3,5));
//
//        world.spawnObject(GameObjectType.TYPE_STATIC, "cactus.002", null, CollisionShapeType.CYLINDER, false, new Vector3(22,3,5));

      //  world.spawnObject(GameObjectType.TYPE_PICKUP_FLAG, "flag", null, CollisionShapeType.CYLINDER, true,new Vector3(32,2,5));


       // world.spawnObject(GameObjectType.TYPE_PICKUP_ITEM, "alienTech", null, CollisionShapeType.BOX, true, new Vector3(32, 2, 5));


        world.spawnObject(GameObjectType.TYPE_STATIC, "fence", null, CollisionShapeType.BOX, true, new Vector3(0, 0, -100), 1f);
        world.spawnObject(GameObjectType.TYPE_STATIC, "fence", null, CollisionShapeType.BOX, true, new Vector3(10, 0, -100), 1f);
        world.spawnObject(GameObjectType.TYPE_STATIC, "fence", null, CollisionShapeType.BOX, true, new Vector3(20, 0, -100), 1f);



        world.spawnObject(GameObjectType.TYPE_DYNAMIC, "wheel", null, CollisionShapeType.CYLINDER, true,new Vector3(8,1,5), Settings.wheelDensity);

        Vector3 carPos = new Vector3(5,3,5);


        world.spawnObject(GameObjectType.TYPE_ENEMY_CAR, "jeep",null, CollisionShapeType.BOX, true, new Vector3(20,3,-40), Settings.chassisDensity);

        GameObject go = world.spawnObject(GameObjectType.TYPE_PLAYER, "mustang","mustangProxy", CollisionShapeType.MESH, true, carPos, Settings.chassisDensity);
        world.setPlayer(go);
    }
}
