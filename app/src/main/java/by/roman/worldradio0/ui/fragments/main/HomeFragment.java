package by.roman.worldradio0.ui.fragments.main;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.activities.FilterActivity;
import by.roman.worldradio0.ui.activities.TimerActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private StateViewModel stateViewModel;
    private ImageView timerButton;
    private ImageView filterButton;
    private ConstraintLayout map;
    private ConstraintLayout list;
    private ImageView mapImage;
    private ImageView listImage;
    private boolean isFilter = false;
    private boolean isMap = true;

    @Override
    public void onResume(){
        super.onResume();
        timerButton.setEnabled(true);
        filterButton.setEnabled(true);
        if(isFilter){
            isFilter = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("HomeFragment: performance", "onViewCreated started");
        findAllId(view);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        map.setOnClickListener(v1 -> mode(new MapFragment(),0));
        list.setOnClickListener(v1 -> mode(new ListFragment(),1));

        Log.v("HomeFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
        timerButton.setOnClickListener(v -> {
            timerButton.setEnabled(false);
            Intent intent = new Intent(getContext(), TimerActivity.class);
            startActivity(intent);
        });
        filterButton.setOnClickListener(v -> {
            filterButton.setEnabled(false);
            isFilter = true;
            Intent intent = new Intent(getContext(), FilterActivity.class);
            startActivity(intent);
        });
    }
    
    private void mode(Fragment f, int num){
        change(f);
        switch (num){
            case 0:
                isMap = true;
                stateViewModel.setMapOpen(isMap);
                map.setBackgroundColor(getColor(requireContext(), R.color.selectedMode));
                mapImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.map));
                list.setBackgroundColor(getColor(requireContext(), R.color.unselectedMode));
                listImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.unselected_list));
                break;
            case 1:
                isMap = false;
                stateViewModel.setMapOpen(isMap);
                map.setBackgroundColor(getColor(requireContext(), R.color.unselectedMode));
                mapImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.unselected_map));
                list.setBackgroundColor(getColor(requireContext(), R.color.selectedMode));
                listImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.list));
                break;
        }
    }
    private void change(Fragment f){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        ft.replace(R.id.fragmentContainerView_Home,f);
        ft.commit();
    }
    private void findAllId(View view){
        timerButton = view.findViewById(R.id.timerButtonView);
        filterButton = view.findViewById(R.id.filterButtonView);
        map = view.findViewById(R.id.mapMode);
        list = view.findViewById(R.id.listMode);
        mapImage = view.findViewById(R.id.mapImage);
        listImage = view.findViewById(R.id.listImage);
    }
}