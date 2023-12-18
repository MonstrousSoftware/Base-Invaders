package com.monstrous.baseInvaders.behaviours;

import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.worlddata.GameObjectType;
import com.monstrous.baseInvaders.worlddata.World;


public class Behaviour {
    protected final GameObject go;

    protected Behaviour(GameObject go) {
        this.go = go;
    }

    public void update(World world, float deltaTime ) { }

    // factory for Behaviour instance depending on object type
    public static Behaviour createBehaviour(GameObject go){
        if(go.type == GameObjectType.TYPE_UFO)
            return new SaucerBehaviour(go);
        else if(go.type == GameObjectType.TYPE_PLAYER)
            return new CarBehaviour(go);
        else if(go.type == GameObjectType.TYPE_ENEMY_CAR)
            return new JeepBehaviour(go);
        return null;
    }
}
