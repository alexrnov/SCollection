package alexrnov.scollection;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SceneRenderer implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // установить размер экрана
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //установить буфер цвета
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // ориентация против часовой стрелки (используется по умолчанию)
        // GLES30.glFrontFace(GLES30.GL_CCW);
        GLES20.glEnable(GLES20.GL_CULL_FACE); // разрешить отбрасывание
        // отбрасывать заднюю грань примитивов при рендеринге
        GLES20.glCullFace(GLES20.GL_BACK);
        // включить тест глубины, который нужен для правильного отображения
        // материала, иначе объекты будут выглядеть неправильно
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }
}
