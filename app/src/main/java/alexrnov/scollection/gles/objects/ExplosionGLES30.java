package alexrnov.scollection.gles.objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

import alexrnov.scollection.gles.LinkedProgram;

import static alexrnov.scollection.gles.Textures.loadTextureNearestFromAsset;
import static alexrnov.scollection.utils.commonGL.Buffers.floatBuffer;


//import android.util.Log;
//import static alexrnov.cosmichunter.Initialization.TAG;

public class ExplosionGLES30 implements Explosion {
  private final byte NUM_COORDINATES = 3; // количество координат - 3 т.е. x, y, z
  private final byte FLOAT_SIZE = 4; // количество байт на тип float

  public float x, y = 0.0f; // координаты для центра взрыва
  public float z = 2.0f;
  private final Random random = new Random();
  private final int programObject;

  private final int lastTimeExplosionLink; // ссылка на uniform-переменную lastTimeExplosion
  private final int centerPositionLink; // ссылка на uniform-переменную centerPosition
  private final int colorLink;
  private final int sizeSpriteLink;
  private final int samplerLink; // ссылка на текстурный семплер
  private final int textureID;

  private final int lifeTimeLink; // ссылка на атрибут времени жизни частицы
  private final int startPositionLink; // ссылка на атрибут начального положения частицы в момент взрыва
  private final int endPositionLink; // ссылка на атрибут конечного положения частицы

  // данные вершин для частиц (время жизни, начальные и конечные координаты)
  private final float[] lifeTimeData; // время жизни
  private final float[] startPositionData; // начальные координаты
  private final float[] endPositionData; // конечные координаты
  private FloatBuffer lifeTimeAsFloatBuffer;
  private FloatBuffer startPositionAsFloatBuffer;
  private FloatBuffer endPositionAsFloatBuffer;

  private final int[] VBO = new int[3]; // массив буферов вершинных атрибутов
  private int[] VAO = new int[1]; // объект состояния вершинных буферов
  // время, прошедшее с начала взрыва. При инициализации сделать его
  // значение больше чем время взрыва, чтобы взрыв не рендерился при
  // запуске приложения
  private float lastTimeExplosion = 2.0f;
  // флаг определяет произошел ли взрыв только что. Это нужно для того
  // чтобы установить координаты центра взрыва
  //private boolean createExplosion = false;

  private final int numberParticles; // количество частиц
  private final float[] color; // цвет взрыва

  //private List<Explosion> explosions;

  public ExplosionGLES30(Context context, String textureFile) {
    this(context, textureFile, new float[] {1.0f, 0.7f, 0.1f, 1.0f});
  }

  public ExplosionGLES30(Context context, String textureFile, float[] color) {
    this.color = color;

    numberParticles = 2000;
    lifeTimeData = new float[numberParticles];
    startPositionData = new float[numberParticles * NUM_COORDINATES];
    endPositionData = new float[numberParticles * NUM_COORDINATES];

    LinkedProgram linkProgram = new LinkedProgram(context,
              "shaders/gles30/explosion_v.glsl",
              "shaders/gles30/explosion_f.glsl");

    programObject = linkProgram.get();

    //if (programObject == 0) {
      //Log.v(TAG, "error program link explosion_sound: " + programObject);
    //}

    lastTimeExplosionLink = GLES20.glGetUniformLocation(programObject, "u_lastTimeExplosion");
    centerPositionLink = GLES20.glGetUniformLocation(programObject, "u_centerPosition");
    sizeSpriteLink = GLES20.glGetUniformLocation(programObject, "u_sizeSprite");
    colorLink = GLES20.glGetUniformLocation(programObject, "u_color");
    samplerLink = GLES20.glGetUniformLocation(programObject, "s_texture");

    //textureID = loadTextureFromAsset(context, textureFile);
    //textureID = loadTextureWithMipMapFromAsset(context, textureFile);
    textureID = loadTextureNearestFromAsset(context, textureFile);

    // получить индексы атрибутов в вершинном шейдере
    lifeTimeLink = GLES20.glGetAttribLocation(programObject, "a_lifeTime");
    startPositionLink = GLES20.glGetAttribLocation(programObject, "a_startPosition");
    endPositionLink = GLES20.glGetAttribLocation(programObject, "a_endPosition");

    /*
    Log.v(TAG, this.getClass().getSimpleName() + ".class: " +
            "lastTimeExplosionLink: " + lastTimeExplosionLink + "; centerPositionLink: " +
            centerPositionLink + "; sizeSpriteLink: " + sizeSpriteLink + "; colorLink: " +
            colorLink + "; samplerLink: " + samplerLink + "; textureID: " + textureID);
    */
  }

  /**
   * Метод генерирует данные для каждой частицы, такие как: время жизни,
   * начальные и конечные координаты. Эти данные затем будут
   * передаваться в вершинный шейдер как вершинные атрибуты. Метод
   * также создает вершинные буферы для атрибутов вершин
   * @param width - ширина экрана
   * @param height - высота экрана
   */
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public void createDataVertex(int width, int height) {
    //GLES20.glViewport(0, 0, width, height);
    float aspect = (float) height / (float) width;

    // генерация времени жизни для частиц
    float maxLifeTime = 3.0f; // максимальное время жизни частицы
    for (int i = 0; i < numberParticles; i ++) {
      //время жизни частицы - случайное значение (0 - 3 секунды)
      lifeTimeData[i] = random.nextFloat() * maxLifeTime;
      //lifeTimeData[i] = 3.0f;
    }

    /* генерация начальных и конечных координат для частиц */
    float[] xyz;
    final float startRadius = 0.45f; // начальный радиус взрыва
    final float endRadius = 0.5f; // конечный радиус взрыва
    for (int i = 0; i < numberParticles * NUM_COORDINATES; i += NUM_COORDINATES) {
      xyz = getPointForSphere(startRadius); // начальная позиция частицы
      startPositionData[i] = xyz[0] * aspect;
      startPositionData[i + 1] = xyz[1];
      startPositionData[i + 2] = xyz[2];
      xyz = getPointForSphere(endRadius); // конечная позиция частицы
      endPositionData[i] = xyz[0] * aspect;
      endPositionData[i + 1] = xyz[1];
      endPositionData[i + 2] = xyz[2];
    }
    lifeTimeAsFloatBuffer = floatBuffer(lifeTimeData);
    startPositionAsFloatBuffer = floatBuffer(startPositionData);
    endPositionAsFloatBuffer = floatBuffer(endPositionData);
    createVertexBuffer();
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public void draw(float delta) {
    lastTimeExplosion += delta * 0.011f; // единица получается примерно через три секунды
    // отключить тест глубины - чтобы взрыв отображался правильно
    GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    GLES20.glUseProgram(programObject);
    GLES20.glUniform1f(lastTimeExplosionLink, lastTimeExplosion);
    GLES20.glUniform1i(samplerLink, 0); // Set the sampler texture unit to 0
    // Разрешить приложению альфа-блендинг с использованием следующей
    // функции для смешивания цветов. В результате этого кода значение альфа,
    // полученное во фрагментном шейдере, умножается на цвет фрагмента.
    // Это значение затем добавляется к тому значению, которое уже есть в
    // соответствующем месте во фреймбуфере. Результатом является
    // аддитивное смешивание цветов для системы частиц.
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    // Bind the texture
    // сделать текущей текстуру с дымом
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
    GLES30.glBindVertexArray(VAO[0]); // связать VAO(сделать его активным)
    // рендеринг частиц. В отличии от треугольников, у точечных спрайтов
    // нет ни какой связанности, поэтому использование glDrawElements не
    // дало бы никакого выигрыша в этом примере.
    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numberParticles);
    GLES30.glBindVertexArray(0); // вернуть VAO к исходному состоянию
    // отключить прозрачность, чтобы все объекты сцены не были прозрачными
    GLES20.glDisable(GLES20.GL_BLEND);
    // включить тест глубины - чтобы после взрыва ракеты не отображались
    // позади астероидов
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    if (lastTimeExplosion > 1.0) {
      // удалить данный взрыв из списка активных взрывов, чтобы условие
      // (lastTimeExplosion > 1.0) не проверялось при прорисовке каждого кадра
      //explosions.remove(this);
    }
  }

  /** создает новый взрыв */
  public void create(float x, float y, float z) {
    Log.i("P", "explosion create");
    lastTimeExplosion = 0.0f;
    //createExplosion = true;
    this.x = x;
    this.y = y;
    this.z = z;

    GLES20.glUseProgram(programObject);
    GLES20.glUniform3f(centerPositionLink, x, y, 0.0f);
    float sizeSprite = 50f / Math.abs(z) + 17f; // размер спрайта зависит от расстояния до астероида
    //Log.i(TAG, "z = " + z + ", sizeSprite = " + sizeSprite);
    // передать эти uniform-переменные только при создании взрыва
    GLES20.glUniform4f(colorLink, color[0], color[1], color[2], color[3]); // оранжевый
    GLES20.glUniform1f(sizeSpriteLink, sizeSprite);
  }

  // возвращает случайные координаты для точки, расположенной внутри сферы
  private float[] getPointForSphere(float size) {
    Log.i("P", "getPointForSphere");
    double d, x, y, z;
    do {
      x = Math.random() * 2.0 - 1.0;
      y = Math.random() * 2.0 - 1.0;
      z = Math.random() * 2.0 - 1.0;
      d = x * x + y * y + z * z;
    } while (d > 1.0);
    return new float[] { (float) x * size, (float) y * size, (float) z * size };
  }

  /* создать буферы для вершинных атрибутов */
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void createVertexBuffer() {
    VBO[0] = 0;
    VBO[1] = 0;
    VBO[2] = 0;
    GLES20.glGenBuffers(3, VBO, 0);

    lifeTimeAsFloatBuffer.position(0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            FLOAT_SIZE * numberParticles, lifeTimeAsFloatBuffer,
            GLES20.GL_STATIC_DRAW);

    startPositionAsFloatBuffer.position(0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[1]);
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            FLOAT_SIZE * NUM_COORDINATES * numberParticles,
            startPositionAsFloatBuffer, GLES20.GL_STATIC_DRAW);

    endPositionAsFloatBuffer.position(0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[2]);
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            FLOAT_SIZE * NUM_COORDINATES * numberParticles,
            endPositionAsFloatBuffer, GLES20.GL_STATIC_DRAW);

    //первый параметр - количество VAO, которые нужно вернуть
    GLES30.glGenVertexArrays(1, VAO, 0);
    //Связать VAO и затем установить вершинные атрибуты
    //после привязки VAO, все вызовы, изменяющие состояние настроек для
    //использования буферов (glBindBuffer, glVertexAttribPointer,
    //glEnableVertexAttribArray и glDisableVertexAttribArray), будут
    //влиять на текущий VAO.
    GLES30.glBindVertexArray(VAO[0]);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);
    GLES20.glEnableVertexAttribArray(lifeTimeLink);
    GLES20.glVertexAttribPointer(lifeTimeLink, 1, GLES20.GL_FLOAT, false, 4, 0);

    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[1]);
    GLES20.glEnableVertexAttribArray(startPositionLink);
    GLES20.glVertexAttribPointer(startPositionLink, NUM_COORDINATES, GLES20.GL_FLOAT,
            false, FLOAT_SIZE * NUM_COORDINATES, 0);

    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[2]);
    GLES20.glEnableVertexAttribArray(endPositionLink);
    GLES20.glVertexAttribPointer(endPositionLink, NUM_COORDINATES, GLES20.GL_FLOAT,
            false, FLOAT_SIZE * NUM_COORDINATES, 0);
    GLES30.glBindVertexArray(0); // сбросить к VAO по умолчанию
    //GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
  }

  public void setExplosions(List<Explosion> explosions) {

    //this.explosions = explosions;
  }
}
