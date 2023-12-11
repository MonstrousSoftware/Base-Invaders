// underwater shader
// just adds a bit of blue-green and a wavy effect

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

uniform vec2 u_resolution;
uniform sampler2D u_texture;

varying vec2 v_texCoord0;
uniform float u_time;

const float TWOPI = 6.283185307;
const float waveStrength = 0.002;
const float vertWaves = 7.0;
const float horizWaves = 4.0;

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

	//olor.rgb = mix(color.rgb, vec3(0.0,0.5, 0.3), 0.2);	// mix with a bit of blue-green


    // vignette effect
    vec2 uv = v_texCoord0;
    uv *=  1.0 - uv.yx;   //vec2(1.0)- uv.yx; -> 1.-u.yx; Thanks FabriceNeyret !
    float vig = uv.x*uv.y * 25.0; // multiply with sth for intensity
    vig = pow(vig, 0.25); // change pow for modifying the extend of the  vignette
    color.rgb = mix(color.rgb, color.rgb*vig, 0.9);

	gl_FragColor = color;
}
