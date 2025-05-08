package by.roman.worldradio0.ui.fragments.auth;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.view_models.AccountViewModel;
import by.roman.worldradio0.ui.elements.view.InnerGlowMaterialCardView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EntranceFragment extends Fragment {
    private AccountViewModel viewModel;
    private EditText loginText;
    private EditText passwordText;
    private ImageView passButton;
    private ImageView enterButton;
    private InnerGlowMaterialCardView loginCard;
    private InnerGlowMaterialCardView passwordCard;
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
        observeResult();
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
    }
    private void buttons(){
        passButton.setOnClickListener(v -> {
            if (passwordText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passButton.setImageResource(R.drawable.eye);
            } else {
                passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passButton.setImageResource(R.drawable.eye_closed);
            }
            passwordText.setSelection(passwordText.getText().length());
        });
        enterButton.setOnClickListener(v -> {
            enterButton.setEnabled(false);
            login = loginText.getText().toString();
            password = passwordText.getText().toString();
            if(!login.isEmpty()){
                if(!password.isEmpty()) {
                    viewModel.enter(new UserRequest(login,password));
                } else {
                    startError();
                    //TODO пустой пароль
                    Log.e("1","pass1");
                }
            } else {
                startError();
                //TODO пустой логин
                Log.e("1","log");
            }
        });
    }
    private void startError(){
        enterButton.setEnabled(true);
        showError();
    }
    private void observeResult(){
        viewModel.getUser().observe(getViewLifecycleOwner(),result ->{
            switch (result.status){
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    requireActivity().finish();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    showError();
                    enterButton.setEnabled(true);
                    break;
            }
        });
    }
    private void showLoading(){
        //TODO
    }
    private void showError() {
        int errorColor = ContextCompat.getColor(requireContext(), R.color.red);
        int lightErrorColor = ContextCompat.getColor(requireContext(), R.color.lightRed);

        loginText.setText("");
        passwordText.setText("");
        Toast.makeText(requireContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();

        float elevationPx = dpToPx(30);
        int strokePx     = (int) dpToPx(2);
        loginCard.setInnerGlowEnabled(true);
        passwordCard.setInnerGlowEnabled(true);
        applyErrorStyle(loginCard, elevationPx, strokePx, errorColor,lightErrorColor);
        applyErrorStyle(passwordCard, elevationPx, strokePx, errorColor,lightErrorColor);
    }
    private void applyErrorStyle(@NonNull InnerGlowMaterialCardView card, float elevationPx, int strokePx, int color,int lightColor) {
        card.setCardElevation(elevationPx);

        card.setStrokeWidth(strokePx);
        card.setStrokeColor(color);

        card.setOutlineAmbientShadowColor(color);
        card.setOutlineSpotShadowColor(lightColor);
    }
    private float dpToPx(int dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}