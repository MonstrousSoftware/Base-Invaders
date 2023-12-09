package com.monstrous.transamtest.worlddata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.transamtest.Car;
import com.monstrous.transamtest.CarState;
import com.monstrous.transamtest.Main;
import com.monstrous.transamtest.Settings;
import com.monstrous.transamtest.input.PlayerController;
import com.monstrous.transamtest.input.UserCarController;
import com.monstrous.transamtest.physics.*;
//import com.monstrous.transamtest.worlddata.Terrain;
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
    private final UserCarController userCarController;
    public final PhysicsRayCaster rayCaster;
    private Car theCar;
    private Terrain terrain;
    private Scenery scenery;


    public World() {
        gameObjects = new Array<>();
        cars = new Array<>();
        stats = new GameStats();
        sceneAsset = Main.assets.sceneAsset;
        for(Node node : sceneAsset.scene.model.nodes){  // print some debug info
            Gdx.app.log("Node ", node.id);
        }
        physicsWorld = new PhysicsWorld(this);
        factory = new PhysicsBodyFactory(physicsWorld);
        rayCaster = new PhysicsRayCaster(physicsWorld);
        userCarController = new UserCarController();
        terrain = new Terrain();
        scenery = new Scenery(this);

    }

    public void clear() {
        physicsWorld.reset();

        stats.reset();
        gameObjects.clear();
        cars.clear();
        player = null;
        userCarController.reset();
        scenery.populate();
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

    public void setPlayer( GameObject player ){
        this.player = player;
        theCar = new Car(userCarController);
//        player.body.setCapsuleCharacteristics();
    }


    public UserCarController getUserCarController() {
        return userCarController;
    }


    public GameObject spawnObject(GameObjectType type, String name, String proxyName, CollisionShapeType shapeType, boolean resetPosition, Vector3 position){
        if(type == GameObjectType.TYPE_TERRAIN)
            return spawnTerrain();

        Scene scene = loadNode( name, resetPosition, position );
        ModelInstance collisionInstance = scene.modelInstance;
        if(proxyName != null) {
            Scene proxyScene = loadNode( proxyName, resetPosition, position );
            collisionInstance = proxyScene.modelInstance;
        }
        PhysicsBody body = null;
        if(type != GameObjectType.TYPE_SCENERY)
            body = factory.createBody(collisionInstance, shapeType, type.isStatic);
        GameObject go = new GameObject(type, scene, body);
        gameObjects.add(go);
        if(type.isCar)
            addWheels(go);

        return go;
    }

    private GameObject spawnTerrain() {
        Scene scene = new Scene(terrain.getModelInstance());
        PhysicsBody body = factory.createBody(scene.modelInstance,
            CollisionShapeType.MESH, true);
        GameObject go = new GameObject(GameObjectType.TYPE_TERRAIN, scene, body);
        gameObjects.add(go);

        return go;
    }

    private Vector3 tmpPosition = new Vector3();

    // spawn an item at terrain heightScenery

    public  GameObject dropItem( String name, float x, float z, float angle){
        float y = terrain.getHeight(x, z);
        //y = 3;
        tmpPosition.set(x, y, z);
        return spawnObject(GameObjectType.TYPE_SCENERY, name, null, CollisionShapeType.CYLINDER, true, tmpPosition);
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

        Car car = new Car( userCarController );
        factory.connectWheels(car, chassis, w0, w1, w2, w3);
        cars.add(car);
    }

    // index: 0=front left, 1=front right, 2 =rear left, 3 = rear right
    public  GameObject makeWheel(GameObject chassis, int index) {
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
        wheelPos.set( chassisPos.x + dx, chassisPos.y + dy, chassisPos.z + dz);

        GameObject go = spawnObject(GameObjectType.TYPE_WHEEL, "wheel", null, CollisionShapeType.CYLINDER, true, wheelPos);

        // turn cylinder axis from Z to X axis, as the car is oriented towards Z, and cylinder by default points to Z
        Quaternion Q = new Quaternion();
        if (index == 1 || index == 3) // right
            Q.setEulerAngles(90,-90,0);
        else
            Q.setEulerAngles(90,90,0);
        go.body.setOrientation(Q);


        return go;
    }

    private Scene loadNode( String nodeName, boolean resetPosition, Vector3 position ) {
        Scene scene = new Scene(sceneAsset.scene, nodeName);
        if(scene.modelInstance.nodes.size == 0)
            throw new RuntimeException("Cannot find node in GLTF file: " + nodeName);
        applyNodeTransform(resetPosition, scene.modelInstance, scene.modelInstance.nodes.first());         // incorporate nodes' transform into model instance transform
        scene.modelInstance.transform.translate(position);
        return scene;
    }

    private void applyNodeTransform(boolean resetPosition, ModelInstance modelInstance, Node node ){
        if(!resetPosition)
            modelInstance.transform.mul(node.globalTransform);
        node.translation.set(0,0,0);
        node.scale.set(1,1,1);
        node.rotation.idt();
        modelInstance.calculateTransforms();
    }

    public void removeObject(GameObject gameObject){
        gameObject.health = 0;
        if(gameObject.type == GameObjectType.TYPE_ENEMY)
            stats.numEnemies--;
        gameObjects.removeValue(gameObject, true);
        gameObject.dispose();
    }



    public void update( float deltaTime ) {

        userCarController.update(deltaTime);

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

        removeObject(pickup);
//        if(pickup.type == GameObjectType.TYPE_PICKUP_COIN) {
//            stats.coinsCollected++;
//            Main.assets.sounds.COIN.play();
//        }
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
