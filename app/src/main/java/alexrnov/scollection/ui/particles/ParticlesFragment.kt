package alexrnov.scollection.ui.particles

import alexrnov.scollection.R
import alexrnov.scollection.ui.slideshow.SlideSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ParticlesFragment : Fragment() {

    private var particlesSurfaceView: ParticlesSurfaceView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_particles, container, false)
        particlesSurfaceView = root.findViewById(R.id.oglViewParticles)
        particlesSurfaceView?.init(this.activity)
        return root
    }
}