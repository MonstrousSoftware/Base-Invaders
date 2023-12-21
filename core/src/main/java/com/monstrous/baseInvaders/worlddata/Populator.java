package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.physics.CollisionShapeType;


public class Populator {

    public static void populate(World world) {
        world.clear();
        Vector3 carPos = new Vector3(Settings.worldSize/2,-1001,Settings.worldSize/2);

        world.spawnObject(GameObjectType.TYPE_TERRAIN, "groundbox", null, CollisionShapeType.MESH, false, Vector3.Zero, 1f);
        world.spawnObject(GameObjectType.TYPE_ENEMY_CAR, "jeep",null, CollisionShapeType.BOX, true, new Vector3(Settings.worldSize/3,18,Settings.worldSize/2), Settings.chassisDensity);
        world.spawnObject(GameObjectType.TYPE_ENEMY_CAR, "jeep",null, CollisionShapeType.BOX, true, new Vector3(2*Settings.worldSize/3,18,Settings.worldSize/4), Settings.chassisDensity);
        world.spawnObject(GameObjectType.TYPE_ENEMY_CAR, "jeep",null, CollisionShapeType.BOX, true, new Vector3(-Settings.worldSize/2,18,2*Settings.worldSize/3), Settings.chassisDensity);
        world.spawnObject(GameObjectType.TYPE_ENEMY_CAR, "jeep",null, CollisionShapeType.BOX, true, new Vector3(2*Settings.worldSize/3,18,Settings.worldSize/3), Settings.chassisDensity);

        GameObject go = world.spawnObject(GameObjectType.TYPE_PLAYER, "mustang","mustangProxy", CollisionShapeType.MESH, true, carPos, Settings.chassisDensity);
        world.setPlayer(go);

    }
}
