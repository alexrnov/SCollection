package alexrnov.scollection.gles.objects;


import alexrnov.cosmichunter.view.View3D;
import alexrnov.scollection.view.AsteroidView3D;

public interface Asteroid {
  AsteroidView3D getView();
  void setView(View3D view);

  void setExplosion(Explosion explosion);
  Explosion getExplosion();

  void draw();
}
