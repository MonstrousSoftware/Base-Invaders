package com.monstrous.baseInvaders;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

// derived from DefaultRendeableSorter

public class MyRenderableSorter implements RenderableSorter, Comparator<Renderable> {
    private Camera camera;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    @Override
    public void sort (final Camera camera, final Array<Renderable> renderables) {
        this.camera = camera;
        renderables.sort(this);
    }

    private Vector3 getTranslation (Matrix4 worldTransform, Vector3 center, Vector3 output) {
        if (center.isZero())
            worldTransform.getTranslation(output);
        else if (!worldTransform.hasRotationOrScaling())
            worldTransform.getTranslation(output).add(center);
        else
            output.set(center).mul(worldTransform);
        return output;
    }

    @Override
    public int compare (final Renderable o1, final Renderable o2) {

        // FIXME implement better sorting algorithm
        // final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
        // o1.material.equals(o2.material);
        getTranslation(o1.worldTransform, o1.meshPart.center, tmpV1);
        getTranslation(o2.worldTransform, o2.meshPart.center, tmpV2);

        final float d1 = camera.position.dst2(tmpV1);
        final float d2 = camera.position.dst2(tmpV2);
        return Float.compare(d1, d2);
    }
}
