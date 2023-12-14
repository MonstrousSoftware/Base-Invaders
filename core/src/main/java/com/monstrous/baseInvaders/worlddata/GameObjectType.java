package com.monstrous.baseInvaders.worlddata;

public class GameObjectType {
    public final static GameObjectType TYPE_STATIC = new GameObjectType("static", true, false, false, false, false, false, false, false);
    public final static GameObjectType TYPE_PLAYER = new GameObjectType("player", false, true, true, false, false, false, false, false);
    public final static GameObjectType TYPE_ENEMY_CAR = new GameObjectType("enemy", false, false, true, false, true, false, false, false);


    public final static GameObjectType TYPE_PICKUP_FLAG = new GameObjectType("flag", false, false, false,true, false , false, false, false);

    public final static GameObjectType TYPE_PICKUP_ITEM = new GameObjectType("item", false, false, false,true, false , false, false, false);
    public final static GameObjectType TYPE_PICKUP_HEALTH = new GameObjectType("healthpack", false, false, false,true, false , false, false, false);
    public final static GameObjectType TYPE_PICKUP_GUN = new GameObjectType("gun", false, false, false,true, false , false, false, false);
    public final static GameObjectType TYPE_DYNAMIC = new GameObjectType("dynamic", false, false, false,false, false, false, false, false);
    public final static GameObjectType TYPE_ENEMY = new GameObjectType("enemy", false, false, false,false, true, false, false, false);
    public final static GameObjectType TYPE_FRIENDLY_BULLET = new GameObjectType("bullet", false, false,false, false, false, true,false, false);
    public final static GameObjectType TYPE_ENEMY_BULLET = new GameObjectType("bullet", false, false,false, false, false,false, true, false);
    public final static GameObjectType TYPE_NAVMESH = new GameObjectType("NAVMESH", true, false, false,false, false,false, false, true);
    public final static GameObjectType TYPE_WHEEL = new GameObjectType("wheel", false, false, false,false, false, false, false, false);
    public final static GameObjectType TYPE_TERRAIN = new GameObjectType("static", true, false, false, false, false, false, false, false);
    public final static GameObjectType TYPE_SCENERY = new GameObjectType("scenery", true, false, false, false, false, false, false, false);
    public final static GameObjectType TYPE_UFO = new GameObjectType("ufo", true, false, false, false, false, false, false, false);


    public String typeName;
    public boolean isStatic;
    public boolean isPlayer;
    public boolean isCar;
    public boolean canPickup;
    public boolean isEnemy;
    public boolean isFriendlyBullet;
    public boolean isEnemyBullet;
    public boolean isNavMesh;


    public GameObjectType(String typeName, boolean isStatic, boolean isPlayer, boolean isCar, boolean canPickup, boolean isEnemy, boolean isFriendlyBullet, boolean isEnemyBullet, boolean isNavMesh) {
        this.typeName = typeName;
        this.isStatic = isStatic;
        this.isPlayer = isPlayer;
        this.isCar = isCar;
        this.canPickup = canPickup;
        this.isEnemy = isEnemy;
        this.isFriendlyBullet = isFriendlyBullet;
        this.isEnemyBullet = isEnemyBullet;
        this.isNavMesh = isNavMesh;
    }
}
