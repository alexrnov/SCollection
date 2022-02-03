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

    //float distance = length(vertexCoord);

    vec2 origin1 = vertexCoord.xz + vec2(300.0, 300.0);
    vec2 origin2 = vertexCoord.xz + vec2(-300.0, 300.0);
    vec2 origin3 = vertexCoord.xz + vec2(300.0, -300.0);
    vec2 origin4 = vertexCoord.xz + vec2(-300.0, -300.0);

    float distance1 = length(origin1);
    float distance2 = length(origin2);
    float distance3 = length(origin3);
    float distance4 = length(origin4);

    float wave = sin(3.3 * pi * distance1 * 0.13 + u_lastTime) * 0.125 +
    sin(3.2 * pi * distance2 * 0.12 + u_lastTime) * 0.125 +
    sin(3.1 * pi * distance3 * 0.24 + u_lastTime) * 0.125 +
    sin(3.5 * pi * distance4 * 0.32 + u_lastTime) * 0.125;

    //float distance = distance(modelViewVertex, vec3(0.0, 0.0, -2.0));

    //vertexCoord.y += sin(2.0 * pi * distance * 0.2 + u_lastTime) * 0.5;
    vertexCoord.y += wave;

    v_textureCoordinates = a_textureCoordinates;

    gl_Position = u_mvpMatrix * vertexCoord;

    //float h = texture(s_texture, a_position.xy).w;
    //vec4 v_position = vec4(a_position.xy, h/0.5, a_position.w);


    //gl_Position = u_mvpMatrix * v_position;
}