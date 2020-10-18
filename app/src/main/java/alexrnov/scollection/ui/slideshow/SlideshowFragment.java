package alexrnov.scollection.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import alexrnov.scollection.R;
import alexrnov.scollection.ui.gallery.GallerySurfaceView;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private SlideSurfaceView slideSurfaceView; // используется в случае вывода рендера в отдельный компонент интерфейса

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideSurfaceView = root.findViewById(R.id.oglViewSlide);
        slideSurfaceView.init(this.getActivity());

        return root;
    }
}