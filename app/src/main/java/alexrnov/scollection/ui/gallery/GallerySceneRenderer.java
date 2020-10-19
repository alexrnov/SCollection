package alexrnov.scollection.ui.gallery;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import alexrnov.scollection.gles.objects.Cell;
import alexrnov.scollection.gles.objects.Fog;
import alexrnov.scollection.gles.objects.MultiTexture;
import alexrnov.scollection.view.AnisotropyView3D;
import alexrnov.scollection.view.CellView3D;
import alexrnov.scollection.view.FogView3D;
import alexrnov.scollection.view.IridescenceView3D;
import alexrnov.scollection.gles.objects.Anisotropy;
import alexrnov.scollection.gles.objects.Diffuse;
import alexrnov.scollection.gles.objects.Iridescence;
import alexrnov.scollection.gles.objects.Refraction;
import alexrnov.scollection.gles.objects.Specular;
import alexrnov.scollection.utils.MeanValue;
import alexrnov.scollection.view.DiffuseView3D;
import alexrnov.scollection.view.MultiTextureView3D;
import alexrnov.scollection.view.RefractionView3D;
import alexrnov.scollection.view.SpecularView3D;

public class GallerySceneRenderer implements GLSurfaceView.Renderer {

    private Context context; // нужно ли синхронизировать?
    private double versionGL;
    private Iridescence iridescence;
    private Anisotropy anisotropy;
    private Specular specular;
    private Diffuse diffuse;
    private Refraction refraction;
    private Cell cell;
    private Fog fog;
    private MultiTexture multiTexture;

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

    public GallerySceneRenderer(double versionGL, Context context) {
        this.versionGL = versionGL;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        // реализация делает предпочтение на быстродействие
        GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);

        iridescence = new Iridescence(versionGL, context, 0.6f, "objects/planet.obj");
        anisotropy = new Anisotropy(versionGL, context, 0.6f, "objects/sphere.obj");
        specular = new Specular(versionGL, context, 0.6f, "objects/sphere.obj");
        diffuse = new Diffuse(versionGL, context, 0.6f, "objects/suzanne.obj");
        refraction = new Refraction(versionGL, context, 0.6f);
        cell = new Cell(versionGL, context, 0.6f, "objects/sphere.obj");
        fog = new Fog(versionGL, context, 0.6f, "objects/torus.obj");
        multiTexture = new MultiTexture(versionGL, context, 0.6f, "objects/sphere.obj");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        widthDisplay = width;
        heightDisplay = height;

        GLES20.glViewport(0, 0, width, height); // установить размер экрана
        if (firstRun) { // первый запуск приложения
           iridescence.setView(new IridescenceView3D(width, height));
           anisotropy.setView(new AnisotropyView3D(width, height));
           specular.setView(new SpecularView3D(width, height));
           diffuse.setView(new DiffuseView3D(width, height));
           refraction.setView(new RefractionView3D(width, height));
           cell.setView(new CellView3D(width, height));
           fog.setView(new FogView3D(width, height));
           multiTexture.setView(new MultiTextureView3D(width, height));
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

        iridescence.draw();
        iridescence.getView().spotPosition(delta);

        anisotropy.draw();
        anisotropy.getView().spotPosition(delta);

        specular.draw();
        specular.getView().spotPosition(delta);

        diffuse.draw();
        diffuse.getView().spotPosition(delta);

        refraction.draw();
        refraction.getView().spotPosition(delta);

        cell.draw();
        cell.getView().spotPosition(delta);

        fog.draw();
        fog.getView().spotPosition(delta);

        multiTexture.draw();
        multiTexture.getView().spotPosition(delta);

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
