package alexrnov.scollection.view

import alexrnov.scollection.utils.commonGL.Buffers
import android.opengl.Matrix
import android.util.Log
import java.nio.FloatBuffer

/**
 * Определяет поведение астероида, - такие его характеристики как:
 * скорость и направление движения, вращение
 */
class FogView3D(widthScreen: Int, heightScreen: Int):
        AsteroidView3D(widthScreen, heightScreen) {

    private var angle: Float = 0.0f
    private val modelViewMatrixForShader = FloatArray(16)

    override fun spotPosition(delta: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        angle += delta
        //Log.i("P", "angle = $angle")
        // переместить куб вверх/вниз и влево/вправо
        Matrix.translateM(modelMatrix, 0, 0.85f, -0.8f, -17.0f)
        // угол и направления вращения
        Matrix.rotateM(modelMatrix, 0, angle, -0.5f, 0.0f, 0.0f)
        // отдельная mv-матрица для загрузки в шейдер
        Matrix.multiplyMM(modelViewMatrixForShader, 0, viewMatrix, 0, modelMatrix, 0)
        //Matrix.scaleM(modelMatrix, 0, 1f, 1f, 1f) // увеличить объект
        // комбинировать видовую и модельные матрицы
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        // комбинировать модельно-видовую матрицу и проектирующую матрицу
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    }

    /**
     * Для планеты используется отдельная mv-матрица, которая не
     * подвергалась трансформации с помощью метода Matrix.scaleM()
     */
    fun getMVNoScaleMatrixAsFloatBuffer(): FloatBuffer = Buffers.floatBuffer(modelViewMatrixForShader)

}