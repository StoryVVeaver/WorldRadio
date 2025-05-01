package by.roman.worldradio0.ui.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.AccountViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EntranceFragment extends Fragment {
    private AccountViewModel viewModel;
    private EditText loginText;
    private EditText passwordText;
    private ImageView passButton;
    private ImageView enterButton;
    private CardView loginCard;
    private CardView passwordCard;
    private boolean passVisibility;
    private String login;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrance, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        findAll(view);
        initAll();
        buttons();
        Log.v("EntranceFragment","Performance - onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAll(@NonNull View view){
        loginText = view.findViewById(R.id.loginInput_Entrance);
        passwordText = view.findViewById(R.id.passwordInput_Entrance);
        passButton = view.findViewById(R.id.passStatus_Entrance);
        enterButton = view.findViewById(R.id.enterButton);
        loginCard = view.findViewById(R.id.loginCardView_Entrance);
        passwordCard = view.findViewById(R.id.passwordCardView_Entrance);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        showError();
    }
    private void buttons(){
        passButton.setOnClickListener(v -> {
            if(passVisibility){
                passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.eye_closed));
            } else {
                passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.eye));
            }
            passVisibility = !passVisibility;
        });
        enterButton.setOnClickListener(v -> {
            enterButton.setEnabled(false);
            login = loginText.getText().toString();
            password = passwordText.getText().toString();
            if(viewModel.enter()){
                requireActivity().finish();
            } else {
                enterButton.setEnabled(true);
                showError();
            }
        });
    }
    private void showError(){
        loginText.setText("");
        passwordText.setText("");
        loginCard.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.glow_red_border));
        loginCard.setCardElevation(16f);
    }
}