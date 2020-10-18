#version 300 es
// точность вычислений по умолчанию. Если в вершинном шейдере не задана
// точность по умолчанию, то точность по умолчанию для float и int будет highp
precision lowp float; // низкая точность для всех переменных, основанных на типе float
uniform mat4 u_mvpMatrix; // модельно-видо-проекционная матрица
uniform mat4 u_mvMatrix; // модельно-видовая матрица
uniform mat4 u_pointViewMatrix; // матрица вида для точки обзора
// атрибуты(переменные вершин) принимают значения, задаваемые для выводимых
// вершин. Обычно атрибуты хранят такие данные, как положение, нормаль,
// текстурные координаты и цвета. Описатель layout в начале используется
// для задания индекса соответствующего атрибута. in вместо attribute в OpenGL 2.0/GLSL 1.00
// layout(location = 0) in vec4 a_position; // сюда загружаются данные вершин
// layout(location = 1) in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
// layout(location = 2) in vec3 a_normal; // сюда загружаются нормали
in vec4 a_position; // сюда загружаются данные вершин
in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
in vec3 a_normal; // сюда загружаются нормали

// выходные переменные вершинного шейдера описываются ключевым словом out
// эти переменные будут также описаны во фрагментном шейдере с помощью
// ключевого слова in(и теми же типами) и будут линейно проинтерполированы
// вдоль примитива во время растеризации. Для выходных переменных вершинного
// шейдера/входных переменных фрагментного шейдера не могут иметь описателей
// размещения(layout)
out vec2 v_textureCoordinates; //out - вместо varying в OpenGL 2.0/GLSL 1.00

out lowp float v_DiffuseIntensity;
out lowp float v_SpecularIntensity;

const vec3 lightPosition = vec3(-1.0, 0.0, 5.0); // позиция источника света

const lowp vec3 grainDirection = vec3(-35.0, 10.8, 45.0);
void main() {
    // преобразовать ориентацию нормали в пространство глаза
    vec3 modelViewNormal = vec3(u_mvMatrix * vec4(a_normal, 0.0));
    vec3 modelViewVertex = vec3(u_mvMatrix * a_position); // eye coord

    mediump vec3 eyeDirection = normalize(-modelViewVertex);

    vec3 lightVector = normalize(lightPosition - modelViewVertex);
    lightVector = mat3(u_pointViewMatrix) * lightVector;

    mediump vec3 normalXgrain = cross(modelViewNormal, grainDirection);
    mediump vec3 tangent = normalize(cross(normalXgrain, modelViewNormal));
    mediump float LdotT = dot(tangent, normalize(lightVector));
    mediump float VdotT = dot(tangent, normalize(mat3(u_mvMatrix)*vec3(0.0, 0.0, 0.0)));
    mediump float NdotL = sqrt(max(0.0, 1.0 - pow(max(0.0, LdotT), 2.0)));
    float VdotR = NdotL * sqrt(max(0.0, 1.0 - pow(max(0.0, VdotT), 2.0))) - VdotT * LdotT;

    v_DiffuseIntensity = max(NdotL * 0.4 + 0.6, 0.0);
    v_SpecularIntensity = max(VdotR * VdotR * 0.9, 0.0);

    v_textureCoordinates = a_textureCoordinates;
    gl_Position = u_mvpMatrix * a_position;
}