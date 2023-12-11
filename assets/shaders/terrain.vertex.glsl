#ifdef GL_ES
precision mediump float;
#endif


// attributes of this vertex
attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;


uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_cameraPosition;

varying vec4 v_normal;
varying vec2 v_texCoords;
varying vec3 v_worldPosition;
varying vec3 v_worldNormal;

varying float v_fog;

#ifdef shadowMapFlag
uniform mat4 u_shadowMapProjViewTrans;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag
#endif //shadowMapFlag

void main() {
   	vec4 vertPos   = u_worldTrans * a_position;
    v_worldPosition = vertPos.xyz;
    v_worldNormal  = normalize(mat3(u_worldTrans) * a_normal);
	v_normal =  vec4(a_normal, 1);
	v_texCoords = a_texCoord0;

		#ifdef shadowMapFlag
    		vec4 spos = u_shadowMapProjViewTrans * vertPos;
    		v_shadowMapUv.xyz = (spos.xyz / spos.w) * 0.5 + 0.5;
    		v_shadowMapUv.z = min(v_shadowMapUv.z, 0.998);
    	#endif //shadowMapFlag

    	vec3 flen = u_cameraPosition.xyz - vertPos.xyz;
        float fog = dot(flen, flen) * u_cameraPosition.w;
        v_fog = min(fog, 1.0);

   	gl_Position = u_projViewTrans * u_worldTrans * a_position;
}
