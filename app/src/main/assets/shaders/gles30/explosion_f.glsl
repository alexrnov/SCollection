#version 300 es
precision lowp float;
in float v_lifeTime; // входная переменная - оставшееся время жизни частицы
// цвет взрыва (вектор-константа). Значение влияет на цвет частицы
//const vec4 c_color = vec4(0.1, 0.9, 0.2, 1.0);
uniform vec4 u_color;
out vec4 fragColor;
// текстура, являющаяся двухмерным изображением "кусочка дыма"
uniform sampler2D s_texture;
void main()
{
    // чтение из текстуры использует встроенную переменную gl_PointCoord
    // в качестве текстурных координат. Для точечных спрайтов эта специальная
    // переменная получает фиксированные значения в углах точечного спрайта
    vec4 texColor = texture(s_texture, gl_PointCoord);
    // texColor = texture(s_texture, vec2(gl_PointCoord.x, gl_PointCoord.y));
    fragColor = u_color * texColor;
    //fragColor = u_color;
    // увеличить прозрачность спрайта, чем ближе конец жизненного
    // цикла, тем больше прозрачность частицы
    fragColor.a *= v_lifeTime;
}