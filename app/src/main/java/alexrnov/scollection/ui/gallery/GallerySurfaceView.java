package alexrnov.scollection.ui.gallery;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import alexrnov.scollection.ui.light.LightSceneRenderer;

public class GallerySurfaceView extends GLSurfaceView {
    private GallerySceneRenderer renderer;

    public GallerySurfaceView(Context context) {
        super(context);
    }

    public GallerySurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void init(Context context) {
        setPreserveEGLContextOnPause(true); // сохранять контескт OpenGL
        setEGLContextClientVersion(2);
        renderer = new GallerySceneRenderer();
        setRenderer(renderer);
        //осуществлять рендеринг только когда изминились данные для рисования
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
