package alexrnov.scollection.ui.light;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class LightSurfaceView extends GLSurfaceView {
    private LightSceneRenderer renderer;

    public LightSurfaceView(Context context) {
        super(context);
    }

    public LightSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void init(Context context) {
        setPreserveEGLContextOnPause(true); // сохранять контескт OpenGL
        setEGLContextClientVersion(2);
        GLSurfaceView.Renderer renderer = new LightSceneRenderer();
        setRenderer(renderer);
        //осуществлять рендеринг только когда изминились данные для рисования
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
