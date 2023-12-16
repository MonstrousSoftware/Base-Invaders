package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.Car;
import com.monstrous.baseInvaders.input.AICarController;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.UserCarController;
import com.monstrous.baseInvaders.physics.*;
//import com.monstrous.transamtest.worlddata.Terrain;
import com.monstrous.baseInvaders.terrain.Terrain;
import com.monstrous.baseInvaders.terrain.TerrainChunk;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<GameObject> gameObjects;
    private final Array<Car> cars;
    private GameObject player;
    public GameStats stats;
    private final SceneAsset sceneAsset;
    private final PhysicsWorld physicsWorld;
    private final PhysicsBodyFactory factory;
    private UserCarController userCarController;
    public final PhysicsRayCaster rayCaster;
    private Car playerCar;
    public Terrain terrain;
    private Scenery scenery;
    private float ufoSpawnTimer;


    public World() {
        Gdx.app.log("world", "constructor");

        gameObjects = new Array<>();
        cars = new Array<>();
        stats = new GameStats();
        sceneAsset = Main.assets.sceneAsset;
        for (Node node : sceneAsset.scene.model.nodes) {  // print some debug info
            Gdx.app.log("Node ", node.id);
        }
        physicsWorld = new PhysicsWorld(this);
        factory = new PhysicsBodyFactory(physicsWorld);
        rayCaster = new PhysicsRayCaster(physicsWorld);
        //userCarController = nullnew UserCarController();
        terrain = new Terrain();
        scenery = new Scenery(this);

        playerCar = new Car();
        userCarController = new UserCarController(playerCar);

    }

    public void clear() {
        physicsWorld.reset();

        stats.reset();
        gameObjects.clear();
        cars.clear();
        player = null;
        scenery.populate();
        ufoSpawnTimer = 3f;
    }

    public int getNumGameObjects() {
        return gameObjects.size;
    }

    public GameObject getGameObject(int index) {
        return gameObjects.get(index);
    }

    public GameObject getPlayer() {
        return player;
    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

    public Car getPlayerCar() {
        return playerCar;
    }

    public UserCarController getUserCarController() {

        return userCarController;
    }


    public GameObject spawnObject(GameObjectType type, String name, String proxyName, CollisionShapeType shapeType, boolean resetPosition, Vector3 position, float density) {
        if (type == GameObjectType.TYPE_TERRAIN)
            return spawnTerrain();

        Scene scene = loadNode(name, resetPosition, position);
        ModelInstance collisionInstance = scene.modelInstance;
        if (proxyName != null) {
            Scene proxyScene = loadNode(proxyName, resetPosition, position);
            collisionInstance = proxyScene.modelInstance;
        }
        if (type.isCar)
            density = Settings.chassisDensity;
        PhysicsBody body = null;
        if (type != GameObjectType.TYPE_SCENERY && type != GameObjectType.TYPE_UFO)
            body = factory.createBody(collisionInstance, shapeType, type.isStatic, density);
        GameObject go = new GameObject(type, scene, body);
        gameObjects.add(go);
        if (type.isCar)
            addWheels(go);

        return go;
    }

    private GameObject spawnTerrain() {
        for(ModelInstance instance : terrain.instances) {

            Scene scene = new Scene(instance);
            PhysicsBody body = factory.createBody(scene.modelInstance,
                CollisionShapeType.MESH, true, 1f);
            GameObject go = new GameObject(GameObjectType.TYPE_TERRAIN, scene, body);
            gameObjects.add(go);
        }
        return null;
    }

    private Vector3 tmpPosition = new Vector3();

    // spawn an item at terrain heightScenery

    public GameObject dropItem(String name, float x, float z, float angle) {
        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        GameObject go = spawnObject(GameObjectType.TYPE_SCENERY, name, null, CollisionShapeType.CYLINDER, true, tmpPosition, 1f);
        go.scene.modelInstance.transform.rotate(Vector3.Y, angle);
        return go;
    }

    private void addWheels(GameObject chassis) {

        GameObject w0 = makeWheel(chassis, 0);
        GameObject w1 = makeWheel(chassis, 1);
        GameObject w2 = makeWheel(chassis, 2);
        GameObject w3 = makeWheel(chassis, 3);
        gameObjects.add(w0);
        gameObjects.add(w1);
        gameObjects.add(w2);
        gameObjects.add(w3);

        Car car;
        if(chassis.type == GameObjectType.TYPE_PLAYER) {
            car = playerCar;
        }
        else {// enemy car
            //AICarController aiCarController = new AICarController();
            car = new Car();
        }

        factory.connectWheels(car, chassis, w0, w1, w2, w3);
        cars.add(car);
    }

    // index: 0=front left, 1=front right, 2 =rear left, 3 = rear right
    public GameObject makeWheel(GameObject chassis, int index) {
        Vector3 chassisPos = new Vector3();
        chassis.scene.modelInstance.transform.getTranslation(chassisPos);
        float dx = Settings.wheelSide;    // side
        float dy = Settings.wheelDown;   // down
        float dz;

        if (index == 1 || index == 3) // right
            dx = -dx;
        if (index == 2 || index == 3) // rear
            dz = -Settings.wheelBack;
        else
            dz = Settings.wheelForward;

        Vector3 wheelPos = new Vector3();
        wheelPos.set(chassisPos.x + dx, chassisPos.y + dy, chassisPos.z + dz);

        GameObject go = spawnObject(GameObjectType.TYPE_WHEEL, "wheel", null, CollisionShapeType.CYLINDER, true, wheelPos, Settings.wheelDensity);

        // turn cylinder axis from Z to X axis, as the car is oriented towards Z, and cylinder by default points to Z
        Quaternion Q = new Quaternion();
        if (index == 1 || index == 3) // right
            Q.setEulerAngles(90, 90, 0);          // BUGFIX!!!
        else
            Q.setEulerAngles(90, 90, 0);
        go.body.setOrientation(Q);


        return go;
    }

    private Scene loadNode(String nodeName, boolean resetPosition, Vector3 position) {
        Scene scene = new Scene(sceneAsset.scene, nodeName);
        if (scene.modelInstance.nodes.size == 0)
            throw new RuntimeException("Cannot find node in GLTF file: " + nodeName);
        applyNodeTransform(resetPosition, scene.modelInstance, scene.modelInstance.nodes.first());         // incorporate nodes' transform into model instance transform
        scene.modelInstance.transform.translate(position);
        return scene;
    }

    private void applyNodeTransform(boolean resetPosition, ModelInstance modelInstance, Node node) {
        if (!resetPosition)
            modelInstance.transform.mul(node.globalTransform);
        node.translation.set(0, 0, 0);
        node.scale.set(1, 1, 1);
        node.rotation.idt();
        modelInstance.calculateTransforms();
    }

    public void removeObject(GameObject gameObject) {
        gameObject.health = 0;
        if (gameObject.type == GameObjectType.TYPE_ENEMY_CAR)
            stats.numEnemies--;
        gameObjects.removeValue(gameObject, true);
        gameObject.dispose();
    }

    private void ufoSpawner(float deltaTime) {
        if(stats.ufosSpawned >= 7)  // max nr of items
            return;
        ufoSpawnTimer -= deltaTime;
        if(ufoSpawnTimer<=0) {

            float x = (float) (Math.random())*0.9f*Settings.worldSize;    // not too close to the edge
            float z = (float) (Math.random())*0.9f*Settings.worldSize;
            float y = terrain.getHeight(x, z);
            spawnObject(GameObjectType.TYPE_UFO, "ufo", null, CollisionShapeType.SPHERE, true, new Vector3(x, y+5f, z), 1f);
            stats.ufosSpawned++;
            ufoSpawnTimer = 15f;
        }
    }


    public void update( float deltaTime ) {

        stats.gameTime += deltaTime;
        stats.speed = (int)cars.get(0).speedKPH;
        ufoSpawner(deltaTime);
        userCarController.update(deltaTime);

        for(GameObject go : gameObjects)
            go.update(this, deltaTime);

        for(Car car: cars )
            car.update(deltaTime);

        physicsWorld.update();
        syncToPhysics();
    }

    private void syncToPhysics() {
        for(GameObject go : gameObjects){
            if( go.body != null && go.body.geom.getBody() != null) {
                go.scene.modelInstance.transform.set(go.body.getPosition(), go.body.getBodyOrientation());
            }
        }
    }





    public void onCollision(GameObject go1, GameObject go2){
        // try either order
        if(go1.type.isStatic || go2.type.isStatic)
            return;

        handleCollision(go1, go2);
        handleCollision(go2, go1);
    }

    private void handleCollision(GameObject go1, GameObject go2) {
        if (go1.type.isPlayer && go2.type.canPickup) {
            pickup(go1, go2);
        }
//        if (go1.type.isPlayer && go2.type.isEnemyBullet) {
//            removeObject(go2);
//            bulletHit(go1);
//        }
//
//        if(go1.type.isEnemy && go2.type.isFriendlyBullet) {
//            removeObject(go2);
//            bulletHit(go1);
//        }
    }

    private void pickup(GameObject character, GameObject pickup){

        Main.assets.sounds.PICK_UP.play();
        removeObject(pickup);
        if(pickup.type == GameObjectType.TYPE_PICKUP_ITEM) {
            stats.techCollected++;

        }
//        else if(pickup.type == GameObjectType.TYPE_PICKUP_HEALTH) {
//            character.health = Math.min(character.health + 0.5f, 1f);   // +50% health
//            Main.assets.sounds.UPGRADE.play();
//        }
//        else if(pickup.type == GameObjectType.TYPE_PICKUP_GUN) {
//            weaponState.haveGun = true;
//            weaponState.currentWeaponType = WeaponType.GUN;
//            Main.assets.sounds.UPGRADE.play();
//        }
    }

//    private void bulletHit(GameObject character) {
//        character.health -= 0.25f;      // - 25% health
//        Main.assets.sounds.HIT.play();
//        if(character.isDead()) {
//            removeObject(character);
//            if (character.type.isPlayer)
//                Main.assets.sounds.GAME_OVER.play();
//        }
//    }

    @Override
    public void dispose() {
        physicsWorld.dispose();
        rayCaster.dispose();
        terrain.dispose();
    }
}
