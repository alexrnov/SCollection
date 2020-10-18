package alexrnov.scollection.view

import alexrnov.cosmichunter.view.View3D
import android.opengl.GLU
import android.opengl.Matrix
import android.util.Log
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.util.*

/**
 * Определяет поведение астероида, - такие его характеристики как:
 * скорость и направление движения, вращение
 */
open class AsteroidView3D(widthScreen: Int, heightScreen: Int):
        View3D(widthScreen, heightScreen) {

  private val r = Random()
  private var angle: Float = 0.0f
  private var xDirection: Float = 0.0f
  private var yDirection: Float = 0.0f
  var hit: Boolean = false
  private var view:IntArray = intArrayOf(0, 0, widthScreen, heightScreen)
  // массив с экранными координатами [x, y ,z] объекта (в пикселах)
  private var pixelCoordinates = FloatArray(3)

  val pixelX: Float /** координата х объекта в пикселах экрана */
    get() = pixelCoordinates[0]

  val pixelY: Float /** координата y объекта в пикселах экрана */
    get() = pixelCoordinates[1]

  init { beginning() }

  override fun spotPosition(delta: Float) {
    Matrix.setIdentityM(modelMatrix, 0)

    /*
     * В выражениях для расчета x и y ставится минус, чтобы объект
     * преодолевал как можно большее расстояние в области экрана.
     * Это сделано потому, что из-за перспективы, объекты разлетаются
     * даже при нулевых смещениях по x и y. Поэтому здесь используется
     * отрицательное смещение, чтобы объекты тяготели к центру.
     */
    x -= delta * 0.1f * xDirection
    y -= delta * 0.1f * yDirection
    z += delta * 0.5f
    angle += delta * 5f

    Log.i("P", "angle = " + angle)
    // переместить куб вверх/вниз и влево/вправо
    Matrix.translateM(modelMatrix, 0, x, y, z)
    // угол и направления вращения
    Matrix.rotateM(modelMatrix, 0, angle, 0.5f, 0.5f, 0.5f)
    //Matrix.scaleM(modelMatrix, 0, 1f, 5f, 5f) // увеличить объект
    // комбинировать видовую и модельные матрицы
    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    // комбинировать модельно-видовую матрицу и проектирующую матрицу
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    if (z > -2) beginning() // проверяется факт что астероид почти достиг экрана (камеры)"
  }



  // Отобразить астероид на "горизонте", т.е. переместить объект в начало
  fun beginning() {
    xDirection = if (r.nextBoolean()) -1f else 1f // направление полета + сектор неба
    yDirection = if (r.nextBoolean()) -1f else 1f
    x = r.nextInt(50) * xDirection
    y = r.nextInt(30) * yDirection
    z = -105f
    angle = r.nextInt(360).toFloat()
  }
}