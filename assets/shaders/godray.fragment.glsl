// underwater shader
// vignette, God rays (2d) and wavy effect
//
// God rays based on light rays shader from Elusive Pete (shadertoy.com)



#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif



const float TWOPI = 6.283185307;
const float waveStrength = 0.002;
const float vertWaves = 7.0;
const float horizWaves = 4.0;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord0;




float rayStrength(vec2 raySource, vec2 rayRefDirection, vec2 coord, float seedA, float seedB, float speed)
{
    vec2 sourceToCoord = coord - raySource;
    float cosAngle = dot(normalize(sourceToCoord), rayRefDirection);

    return clamp(
        (0.45 + 0.15 * sin(cosAngle * seedA + u_time * speed)) +
        (0.3 + 0.2 * cos(-cosAngle * seedB + u_time * speed)),
        0.0, 1.0) *
    clamp((u_resolution.x - length(sourceToCoord)) / u_resolution.x, 0.5, 1.0);
}

vec4 GodRayColor( in vec2 fragCoord )
{
    vec2 coord = vec2(fragCoord.x, u_resolution.y - fragCoord.y);

    // Set the parameters of the sun rays
    vec2 rayPos1 = vec2(u_resolution.x * 0.7, u_resolution.y * -0.4);
    vec2 rayRefDir1 = normalize(vec2(1.0, -0.116));
    float raySeedA1 = 36.2214;
    float raySeedB1 = 21.11349;
    float raySpeed1 = 1.5;

    vec2 rayPos2 = vec2(u_resolution.x * 0.8, u_resolution.y * -0.6);
    vec2 rayRefDir2 = normalize(vec2(1.0, 0.241));
    const float raySeedA2 = 22.39910;
    const float raySeedB2 = 18.0234;
    const float raySpeed2 = 1.1;

    // Calculate the colour of the sun rays on the current fragment
    vec4 rays1 =
    vec4(1.0, 1.0, 1.0, 1.0) *
    rayStrength(rayPos1, rayRefDir1, coord, raySeedA1, raySeedB1, raySpeed1);

    vec4 rays2 =
    vec4(1.0, 1.0, 1.0, 1.0) *
    rayStrength(rayPos2, rayRefDir2, coord, raySeedA2, raySeedB2, raySpeed2);

    vec4 fragColor = rays1 * 0.5 + rays2 * 0.4;

    // Attenuate brightness towards the bottom, simulating light-loss due to depth.
    // Give the whole thing a blue-green tinge as well.

    float brightness =  1.0 - (coord.y / u_resolution.y);
    fragColor.r *= 0.1 + (brightness * 0.8);
    fragColor.g *= 0.3 + (brightness * 0.6);
    fragColor.b *= 0.5 + (brightness * 0.5);

    fragColor.a = 1.0;
    return fragColor;
}



void main()
{

    // use some sine wave distortion animated in time
    float dx = cos(u_time);
    float dy = cos(1.7*u_time);
    vec2 coord = v_texCoord0;
    coord.y = coord.y + waveStrength * dy * sin(horizWaves * TWOPI * coord.x);
    coord.x = coord.x + waveStrength * dx * sin(vertWaves * TWOPI * coord.y);
    // note the use of sin() ensures the distortion is zero at the edges to avoid using
    // coordinates outside the texture area

	vec4 color = texture2D(u_texture, coord);
	vec2 uv = v_texCoord0;

    vec4 rayColor =  GodRayColor(gl_FragCoord.xy);
    color.rgb = mix(color.rgb, rayColor.rgb, 0.5);

    // vignette effect
    vec2 dist = uv * (1.0 - uv.yx);
    float vig = dist.x*dist.y * 25.0; // multiply with sth for intensity
    vig = pow(vig, 0.25); // change pow for modifying the extend of the  vignette
    color.rgb = mix(color.rgb, color.rgb*vig, 0.9);

    // increase contrast
    color.rgb = (color.rgb - 0.5) * 1.2 + 0.5;

    gl_FragColor = color;
}
