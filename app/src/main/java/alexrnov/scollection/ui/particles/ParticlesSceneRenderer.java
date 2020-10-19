package alexrnov.scollection.ui.particles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import alexrnov.scollection.R;
import alexrnov.scollection.gles.objects.BackgroundObject3D;
import alexrnov.scollection.gles.objects.Explosion;
import alexrnov.scollection.gles.objects.ExplosionGLES30;
import alexrnov.scollection.gles.objects.Wave;
import alexrnov.scollection.gles.objects.Wave2;
import alexrnov.scollection.utils.MeanValue;
import alexrnov.scollection.view.BackgroundView3D;

public class ParticlesSceneRenderer implements GLSurfaceView.Renderer {

    private Context context; // нужно ли синхронизировать?
    private double versionGL;

    // переменные используются в другом потоке (main)
    private volatile int widthDisplay;
    private volatile int heightDisplay;

    private float smoothedDeltaRealTime_ms = 16.0f; // initial value, Optionally you can save the new computed value (will change with each hardware) in Preferences to optimize the first drawing frames
    private float movAverageDeltaTime_ms = smoothedDeltaRealTime_ms; // mov Average start with default value
    private long lastRealTimeMeasurement_ms; // temporal storage for last time measurement

    // smooth constant elements to play with
    private static final float movAveragePeriod = 5; // #frames involved in average calc (suggested values 5-100)
    private static final float smoothFactor = 0.1f; // adjusting ratio (suggested values 0.01-0.5)

    private boolean firstRun = true;
    private MeanValue meanValue = new MeanValue((short) 5000);

    private float totalVirtualRealTime_ms = 0;
    private float speedAdjustments_ms = 0; // to introduce a virtual Time for the animation (reduce or increase animation speed)
    private float totalAnimationTime_ms=0;
    private float fixedStepAnimation_ms = 20; // 20ms for a 50FPS descriptive animation
    private float interpolationRatio = 0;

    private float delta;

    //private BackgroundObject3D backgroundObject3D; // нужно ли синхронизировать?
    //private BackgroundObject3D backgroundObject3D2;

    Explosion explosion;
    Explosion explosion2;

    public ParticlesSceneRenderer(double versionGL, Context context) {
        this.versionGL = versionGL;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        // реализация делает предпочтение на быстродействие
        GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);
        //backgroundObject3D = new Wave(versionGL, context, 2.4f, R.raw.grid_texture);
        //backgroundObject3D2 = new Wave2(versionGL, context, 2.4f, R.raw.grid_texture);
        explosion = new ExplosionGLES30(context, "explosion/star.png", new float[] {1.0f, 0.5f, 0.1f, 1.0f});
        explosion2 = new ExplosionGLES30(context, "explosion/star.png", new float[] {0.3f, 1.0f, 0.3f, 1.0f});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // установить размер экрана


        widthDisplay = width;
        heightDisplay = height;

        GLES20.glViewport(0, 0, width, height); // установить размер экрана
        if (firstRun) { // первый запуск приложения
            //backgroundObject3D.setView(new BackgroundView3D(-5.0f, 4.0f, -4.0f, width, height));
            //backgroundObject3D2.setView(new BackgroundView3D(5.0f, 4.0f, -4.0f, width, height));
            explosion.create(-0.4f, 0.0f, 0.7f);
            explosion.createDataVertex(width, height);

            explosion2.create(0.4f, 0.0f, 0.7f);
            explosion2.createDataVertex(width, height);
        }
        firstRun = false;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        delta = meanValue.add(interpolationRatio);
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

        //backgroundObject3D.draw(delta);
        //backgroundObject3D2.draw(delta);

        explosion.draw(delta);
        explosion2.draw(delta);

        defineDeltaTime();
    }

    // resolve javqui https://stackoverflow.com/questions/10648325/android-smooth-game-loop
    private void defineDeltaTime() {
        totalVirtualRealTime_ms += smoothedDeltaRealTime_ms + speedAdjustments_ms;
        while (totalVirtualRealTime_ms > totalAnimationTime_ms) {
            totalAnimationTime_ms += fixedStepAnimation_ms;
        }

        interpolationRatio = (totalAnimationTime_ms - totalVirtualRealTime_ms)
                / fixedStepAnimation_ms;

        long currTimePick_ms = SystemClock.uptimeMillis();
        float realTimeElapsed_ms;
        if (lastRealTimeMeasurement_ms > 0) {
            realTimeElapsed_ms = (currTimePick_ms - lastRealTimeMeasurement_ms);
        } else {
            realTimeElapsed_ms = smoothedDeltaRealTime_ms; // just the first time
        }
        movAverageDeltaTime_ms = (realTimeElapsed_ms + movAverageDeltaTime_ms
                * (movAveragePeriod-1)) / movAveragePeriod;

        // Calc a better approximation for smooth stepTime
        smoothedDeltaRealTime_ms = smoothedDeltaRealTime_ms +
                (movAverageDeltaTime_ms - smoothedDeltaRealTime_ms) * smoothFactor;

        lastRealTimeMeasurement_ms = currTimePick_ms;
    }

}
