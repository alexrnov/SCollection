package alexrnov.scollection.ui.light;

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

import alexrnov.scollection.OGLView;
import alexrnov.scollection.R;

public class LightFragment extends Fragment {

    private LightViewModel lightViewModel;
    private OGLView oglView; // используется в случае вывода рендера в отдельный компонент интерфейса

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        lightViewModel =
                ViewModelProviders.of(this).get(LightViewModel.class);
        View root = inflater.inflate(R.layout.fragment_light, container, false);

        oglView = root.findViewById(R.id.oglView);
        oglView.init(this.getActivity());
        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        oglView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        oglView.onPause();
    }
}