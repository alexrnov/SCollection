#version 300 es
// во фрагментном шейдере точность по умолчанию должна быть указана явно
precision lowp float;
uniform mat4 u_mvpMatrix;
uniform mat4 u_mvMatrix;
uniform mat4 u_vMatrix;
// текстурные координаты(входной параметр из вершинного шейдера)
// фрагментный шейдер использует их для чтения из текстуры
//in vec2 v_textureCoordinates; //in - вместо varying в OpenGL 2.0/GLSL 1.00
smooth in vec4 v_commonLight;

in vec3 v_normal;

in lowp float SpecularIntensity;

out vec4 outColor; // вместо mediump vec4 gl_FragColor в OpenGL 2.0/GLSL 1.00
uniform samplerCube s_texture; // skybox
in vec3 v_eyeDirectModel;
void main() {

    // Calculate refraction direction in model space
    vec3 refractDirect = refract(v_eyeDirectModel, normalize(v_normal), 0.65);
    // Project refraction
    refractDirect = (u_mvpMatrix * vec4(refractDirect, 0.0)).xyw;
    // Map refraction direction to 2d coordinates
    vec2 refractCoord = 0.5 * (refractDirect.xy / refractDirect.z) + 0.5;

    // расчет рефракции в фрагментном шейдере
    vec4 glassColor = texture(s_texture, vec3(refractCoord, 1.0));
    outColor = glassColor + SpecularIntensity + v_commonLight/3.0;
    outColor.a = 0.8;
}