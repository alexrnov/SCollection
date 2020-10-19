package alexrnov.scollection.ui.particles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import alexrnov.scollection.ui.slideshow.SlideSceneRenderer;

public class ParticlesSurfaceView extends GLSurfaceView {
    private ParticlesSceneRenderer renderer;

    public ParticlesSurfaceView(Context context) {
        super(context);
    }

    public ParticlesSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void init(Context context) {
        setPreserveEGLContextOnPause(true); // сохранять контескт OpenGL
        setEGLContextClientVersion(3);
        Renderer renderer = new ParticlesSceneRenderer(3.0, context);
        setRenderer(renderer);
        //осуществлять рендеринг только когда изминились данные для рисования
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
