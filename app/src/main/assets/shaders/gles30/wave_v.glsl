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
    vec3 modelViewNormal = vec3(u_mvMatrix * vec4(a_normal, 0.0));
    vec3 modelViewVertex = vec3(u_mvMatrix * a_position); // eye coord

    vec4 vertexCoord = a_position;

    float distance = length(vertexCoord);
    //float distance = distance(modelViewVertex, vec3(0.0, 0.0, -2.0));

    vertexCoord.y += sin(2.0 * pi * distance * 0.2 + u_lastTime) * 0.5;
    //vertexCoord.y += sin(distance + u_lastTime) * 0.5;

    v_textureCoordinates = a_textureCoordinates;

    gl_Position = u_mvpMatrix * vertexCoord;

    //float h = texture(s_texture, a_position.xy).w;
    //vec4 v_position = vec4(a_position.xy, h/0.5, a_position.w);


    //gl_Position = u_mvpMatrix * v_position;
}