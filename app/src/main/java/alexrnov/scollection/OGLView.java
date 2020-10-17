package alexrnov.scollection;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import androidx.core.view.GestureDetectorCompat;

public class OGLView extends GLSurfaceView {
    private SceneRenderer renderer;

    public OGLView(Context context) {
        super(context);
    }

    public OGLView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void init(Context context) {
        setPreserveEGLContextOnPause(true); // сохранять контескт OpenGL
        setEGLContextClientVersion(2);
        GLSurfaceView.Renderer renderer = new SceneRenderer();
        setRenderer(renderer);
        //осуществлять рендеринг только когда изминились данные для рисования
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
