package alexrnov.scollection.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import alexrnov.scollection.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private GallerySurfaceView gallerySurfaceView; // используется в случае вывода рендера в отдельный компонент интерфейса

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        gallerySurfaceView = root.findViewById(R.id.oglViewGallery);
        gallerySurfaceView.init(this.getActivity());
        return root;
    }
}