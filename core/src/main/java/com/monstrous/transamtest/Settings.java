package com.monstrous.transamtest;


import com.badlogic.gdx.math.Vector3;

public class Settings {
    static public boolean supportControllers = true;       // disable in case it causes issues
    static public float verticalReadjustSpeed = 4f;

    static public float eyeHeight = 2.5f;   // meters

    static public float walkSpeed = 10f;    // m/s
    static public float runFactor = 2f;     // multiplier for walk speed
    static public float turnSpeed = 120f;   // degrees/s
    static public float jumpForce = 5.0f;
    static public float groundRayLength = 1.2f;


    static public boolean invertLook = false;
    static public boolean freeLook = true;
    static public float degreesPerPixel = 0.1f; // mouse sensitivity

    static public float gravity = -9.8f; // meters / s^2

    static public final int shadowMapSize = 4096;

    static public float playerLinearDamping = 0.05f;
    static public float playerAngularDamping = 0.5f;


    public static float chassisMass = 10f;
    public static float wheelMass = 1f;

    // geometry
    public static float chassisWidth = 2.49f;
    public static float chassisHeight = 1.36f;
    public static float chassisLength = 6f;
    // positioning of wheels
    public static float wheelSide = 1.0f;           // offset from centre of chassis
    public static float wheelForward = 1.83f;    // as measured in Blender
    public static float wheelBack = 1.84f;   // as measured in Blender
    public static float wheelDown = -0.7f;
    // wheel dimensions
    public static float wheelWidth = 0.2f;
    public static float wheelRadius = 0.37f; // as measured in Blender



    public static float suspensionCFM = 0.14f;
    public static float suspensionERP = 0.8f;

    public static float maxSteerAngle = (float) (0.2f*Math.PI);

    static public final String GLTF_FILE = "models/transam.gltf";
}
