package alexrnov.scollection.ui.light;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import alexrnov.scollection.R;

public class LightFragment extends Fragment {

    private LightViewModel lightViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        lightViewModel =
                ViewModelProviders.of(this).get(LightViewModel.class);
        View root = inflater.inflate(R.layout.fragment_light, container, false);

        //lightSurfaceView = root.findViewById(R.id.oglViewLight);
        //lightSurfaceView.init(this.getActivity());
        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        //lightSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //lightSurfaceView.onPause();
    }
}