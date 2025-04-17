package by.roman.worldradio0.ui.fragments.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.roman.worldradio0.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        return view;
    }
}