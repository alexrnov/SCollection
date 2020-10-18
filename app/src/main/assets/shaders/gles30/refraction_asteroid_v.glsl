#version 300 es
// точность вычислений по умолчанию. Если в вершинном шейдере не задана
// точность по умолчанию, то точность по умолчанию для float и int будет highp
precision lowp float; // низкая точность для всех переменных, основанных на типе float
uniform mat4 u_mvpMatrix; // модельно-видо-проекционная матрица
uniform mat4 u_mvMatrix; // модельно-видовая матрица
uniform mat4 u_vMatrix;
uniform mat4 u_mMatrix;

// атрибуты(переменные вершин) принимают значения, задаваемые для выводимых
// вершин. Обычно атрибуты хранят такие данные, как положение, нормаль,
// текстурные координаты и цвета. Описатель layout в начале используется
// для задания индекса соответствующего атрибута. in вместо attribute в OpenGL 2.0/GLSL 1.00
// layout(location = 0) in vec4 a_position; // сюда загружаются данные вершин
// layout(location = 1) in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
// layout(location = 2) in vec3 a_normal; // сюда загружаются нормали
in vec4 a_position; // сюда загружаются данные вершин
//in vec2 a_textureCoordinates; // сюда загружаются двухкомпонентные текстурные координаты
in vec3 a_normal; // сюда загружаются нормали


out vec3 v_normal;
// выходные переменные вершинного шейдера описываются ключевым словом out
// эти переменные будут также описаны во фрагментном шейдере с помощью
// ключевого слова in(и теми же типами) и будут линейно проинтерполированы
// вдоль примитива во время растеризации. Для выходных переменных вершинного
// шейдера/входных переменных фрагментного шейдера не могут иметь описателей
// размещения(layout)
out vec2 v_textureCoordinates; //out - вместо varying в OpenGL 2.0/GLSL 1.00

out lowp float SpecularIntensity;

// smooth - описатель интерполяции. Smooth(линейная интерполяция вдоль примитива)
// - используется по умолчанию. Другие возможные варианты flat(плоское закрашивние)
// и centroid(интерполяция внутри примитива)
smooth out vec4 v_commonLight; //интерполятор для общего освещения(фоновое + диффузное)

struct AmbientLight { // структура для внешнего освещения
    vec3 color; // цвет внешнего освещения
    float intensity; // интенсивность внешнего освещения
};

struct DiffuseLight { // структура для диффузного освещения
    vec3 color; // цвет внешнего освещения
    float intensity; // интенсивность диффузного освещения
};

uniform AmbientLight u_ambientLight; // переменная для внешнего освещения
uniform DiffuseLight u_diffuseLight; // переменная для диффузного освещения

out vec3 v_eyeDirectModel;
const vec3 lightDirection = vec3(0.7, 0.0, -1.0); // вектор направленного освещения

float getSpecularIntensity(vec4 position, vec3 a_normal, vec3 eyeDirectModel) {
    float shininess = 30.0;
    vec3 lightPosition = vec3(-20.0, 0.0, 0.0);
    // We ignore that N dot L could be negative (light coming from behind the surface)
    mediump vec3 LightDirModel = normalize(lightPosition - position.xyz);
    mediump vec3 halfVector = normalize(LightDirModel + eyeDirectModel);
    lowp float NdotH = max(dot(a_normal, halfVector), 0.0);
    return pow(NdotH, shininess);
}

void main() {
    v_normal = a_normal;

    vec4 eyePositionModel = u_mvMatrix * a_position;
    // Eye direction in model space
    vec3 eyeDirectModel = normalize(- eyePositionModel.xyz);

    // specular lighting
    SpecularIntensity = getSpecularIntensity(a_position, a_normal, eyeDirectModel);

    v_eyeDirectModel = eyeDirectModel;



    // расчитать итоговый цвет для внешнего освещение
    lowp vec3 ambientColor = u_ambientLight.color * u_ambientLight.intensity;
    // преобразовать ориентацию нормали в пространство глаза
    vec3 modelViewNormal = vec3(u_mvMatrix * vec4(a_normal, 0.0));


    float diffuse = max(-dot(modelViewNormal, lightDirection), 0.0);
    // расчитать итоговый цвет для диффузного освещения
    lowp vec3 diffuseColor = diffuse * u_diffuseLight.color * u_diffuseLight.intensity;

    v_commonLight = vec4((ambientColor + diffuseColor), 1.0);
    //v_textureCoordinates = a_textureCoordinates;
    gl_Position = u_mvpMatrix * a_position;
}