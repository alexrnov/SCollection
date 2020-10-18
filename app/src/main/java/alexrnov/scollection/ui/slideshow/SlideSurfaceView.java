package alexrnov.scollection.ui.slideshow;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class SlideSurfaceView extends GLSurfaceView {
    private SlideSceneRenderer renderer;

    public SlideSurfaceView(Context context) {
        super(context);
    }

    public SlideSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void init(Context context) {
        setPreserveEGLContextOnPause(true); // сохранять контескт OpenGL
        setEGLContextClientVersion(3);
        Renderer renderer = new SlideSceneRenderer();
        setRenderer(renderer);
        //осуществлять рендеринг только когда изминились данные для рисования
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
