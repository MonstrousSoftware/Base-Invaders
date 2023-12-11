#ifdef GL_ES
precision mediump float;
#endif


// terrain shader for Under the Sea
// tri planar texture mapping
// directional light and point light
// shadows
// less light at greater depth
// fog


#if defined(numDirectionalLights) && (numDirectionalLights > 0)
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
#endif // numDirectionalLights

#if defined(numPointLights) && (numPointLights > 0)
struct PointLight
{
    vec3 color;
    vec3 position;
};
uniform PointLight u_pointLights[numPointLights];
#endif // numPointLights

uniform sampler2D u_diffuseTexture;
uniform vec3 u_ambientLight;
uniform vec4 u_cameraPosition;
uniform float u_shininess;
uniform vec4 u_fogColor;
uniform vec3 u_attenuation;

varying vec4 v_normal;
varying vec2 v_texCoords;
varying vec3 v_worldNormal;         // normal vector of fragment in world space
varying vec3 v_worldPosition;
varying float v_fog;

#ifdef shadowMapFlag
uniform float u_shadowBias;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;
#define separateAmbientFlag

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts) + u_shadowBias); // (1.0/255.0)
}

float getShadow()
{
	return (//getShadowness(vec2(0,0)) +
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif



void main() {

	vec3 N = v_worldNormal;

   vec3 eyeV =  normalize( u_cameraPosition.xyz - v_worldPosition.xyz );	// vector towards the eye
   vec3 diffuseLight = vec3(0); // sum of diffuse light sources
   vec3 diffusePointLight = vec3(0); // sum of diffuse light sources
   vec3 specularLight = vec3(0); // sum of specular light


 #if defined(numDirectionalLights) && (numDirectionalLights > 0)
    for(int i = 0; i < numDirectionalLights; i++)
    {
        vec3 L      =   - u_dirLights[i].direction; // unit vector towards the light source

        float intensity = clamp(dot(N, L), 0.0, 1.0);
        vec3 contrib = intensity * u_dirLights[i].color;
        diffuseLight += contrib;
    }
 #endif

  #if defined(numPointLights) && (numPointLights > 0)
     for(int i = 0; i < numPointLights; i++)
     {
         vec3 toLightVector      =  u_pointLights[i].position - v_worldPosition.xyz; //vector towards the light source
         float D = length(toLightVector);	// distance to light source (for attenuation)

         vec3 L = normalize(toLightVector);	// normalized light vector (pointing towards light)

         float intensity = clamp(dot(N, L), 0.0, 1.0);

         D *= 1000.0;      // scaling factor (attenuation is related to world scale)
         float att =  1.0/(1.0 + 0.001*D + 0.00001*D*D);		// hard coded c1, c2, c3 for attenuation
         //float att =  1.0/(u_attenuation.x+ u_attenuation.y*D + u_attenuation.z*D*D);		//  c1, c2, c3 for attenuation
         att = clamp(att, 0.0, 1.0);
         vec3 contrib = intensity * att * u_pointLights[i].color;
         diffusePointLight += contrib;

         vec3 H = normalize( eyeV + L);	// halfway vector between light vector and eye vector
         float NdotH = clamp(dot(N, H), 0.0, 1.0);
         specularLight +=  pow( NdotH, u_shininess); // specular light
     }
 #endif

#if defined(ambientLightFlag)       // broken?
    vec3 ambientLight = u_ambientLight;
#else
    vec3 ambientLight = vec3(0.2);
#endif

    vec3 blendAxes = abs(N);
    normalize(blendAxes);
    blendAxes /= blendAxes.x +blendAxes.y + blendAxes.z;

    vec3 pos = v_worldPosition;


    // triplanar texture mapping, note we're ignoring the vertex uv coordinates
    vec4 xProjection = texture2D(u_diffuseTexture, pos.yz ) * blendAxes.x;
    vec4 yProjection = texture2D(u_diffuseTexture, pos.xz ) * blendAxes.y;
    vec4 zProjection = texture2D(u_diffuseTexture, pos.xy ) * blendAxes.z;

    vec4 diffuse = xProjection + yProjection + zProjection;



    float shadows = getShadow();

    // less light as we go deeper, minimum at y=50, max at y=90
    float depthShade = clamp((pos.y -50.0) / 40.0, 0.1, 1.0);

    vec3  finalCol       = diffuse.rgb * ((shadows * depthShade * diffuseLight) + diffusePointLight + depthShade * ambientLight);

	#ifdef fogFlag
		finalCol.rgb = mix(finalCol.rgb, u_fogColor.rgb, v_fog * u_fogColor.a);
	#endif // end fogFlag

    gl_FragColor         = vec4(finalCol.rgb, 1.0 );
}
