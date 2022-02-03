#version 300 es
// во фрагментном шейдере точность по умолчанию должна быть указана явно
precision lowp float;
// текстурные координаты(входной параметр из вершинного шейдера)
// фрагментный шейдер использует их для чтения из текстуры
in vec2 v_textureCoordinates; //in - вместо varying в OpenGL 2.0/GLSL 1.00
smooth in vec4 v_commonLight;

in float CosViewAngle;
in float LightIntensity;

out vec4 outColor; // вместо mediump vec4 gl_FragColor в OpenGL 2.0/GLSL 1.00
// сэмплер - специальный тип uniform-переменных, используемых для
// чтения из текстуры. В эту переменную записывается номер текстурного
// блока, к которому привязана данная текстура. Текстуры привязываются
// к текстурным блокам при помощи функции glActiveTexture
uniform sampler2D s_texture;
const lowp vec3 defaultColor = vec3(0.5, 0.9, 0.1);
void main() {
    // встроенная функция texture для чтения значений из текстуры.
    // первый параметр - семплер, задающий к какому текстурному блоку
    // привязана текстура, из которой читать значения
    // второй параметр - двухмерные текстурные координаты, используемыя
    // для чтения из текстуры возвращает значение типа vec4,
    // представляющее цвет, прочитанный из текстуры.
    //outColor = texture(s_texture, v_textureCoordinates) * v_commonLight;

    lowp float intensity = 0.40;
    if (CosViewAngle > 0.33) {
        intensity = 0.45;
        if (LightIntensity > 0.76) {
            intensity = 1.0;
        } else if (LightIntensity > 0.51) {
            intensity = 0.84;
        } else if (LightIntensity > 0.26) {
            intensity = 0.67;
        } else if (LightIntensity > 0.1) {
            intensity = 0.55;
        }
    }

    outColor = vec4(defaultColor.xyz * intensity, 1.0);
}