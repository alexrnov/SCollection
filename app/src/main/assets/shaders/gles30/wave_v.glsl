#version 300 es
precision lowp float; // низкая точность для всех переменных, основанных на типе float
uniform mat4 u_mvpMatrix; // модельно-видо-проекционная матрица
uniform mat4 u_mvMatrix; // модельно-видовая матрица
uniform float u_lastTime;
uniform sampler2D s_texture;
//layout(location = 0) in vec4 a_position; // сюда загружаются данные вершин
//layout(location = 1) in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
in vec4 a_position; // сюда загружаются данные вершин
in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
in vec3 a_normal; // сюда загружаются нормали

out vec2 v_textureCoordinates; //out - вместо varying в OpenGL 2.0/GLSL 1.00
const float pi = 3.14285714286;
void main() {

    vec4 vertexCoord = a_position;

    float distance = length(vertexCoord);
    vertexCoord.y += sin(3.0 * pi * distance * 0.3 + u_lastTime) * 0.5;

    v_textureCoordinates = a_textureCoordinates;

    gl_Position = u_mvpMatrix * vertexCoord;
}