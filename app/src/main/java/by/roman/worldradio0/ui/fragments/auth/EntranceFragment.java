package by.roman.worldradio0.ui.fragments.auth;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.LocationUtil;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.view_models.AccountViewModel;
import by.roman.worldradio0.ui.activities.MainActivity;
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
    private TextView textReg;
    private TextView errorText;
    private TextView loadingText;
    private ProgressBar progressBar;
    private ProgressBar progressBar_loading;
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
        //viewModel.useradd();
        Log.v("EntranceFragment","Performance - onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAll(@NonNull View view){
        progressBar_loading = view.findViewById(R.id.progressBar_loading_Entrance);
        loadingText = view.findViewById(R.id.textStationsLoading_Entrance);
        loginText = view.findViewById(R.id.loginInput_Entrance);
        passwordText = view.findViewById(R.id.passwordInput_Entrance);
        passButton = view.findViewById(R.id.passStatus_Entrance);
        enterButton = view.findViewById(R.id.enterButton);
        loginCard = view.findViewById(R.id.loginCardView_Entrance);
        passwordCard = view.findViewById(R.id.passwordCardView_Entrance);
        textReg = view.findViewById(R.id.textEnter_Entrance);
        errorText = view.findViewById(R.id.errorText_Entrance);
        progressBar = view.findViewById(R.id.progressBar_Entrance);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
    }
    @SuppressLint("SetTextI18n")
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
            hideError();
            hideKeyboard(requireActivity());
            login = loginText.getText().toString();
            password = passwordText.getText().toString();
            enterButton.setEnabled(false);
            if(!login.isEmpty() && !password.isEmpty()) {
                viewModel.enter(new UserRequest(login, password));

            } else if(login.isEmpty() && password.isEmpty()){
                enterButton.setEnabled(true);
                error(loginText,loginCard);
                error(passwordText,passwordCard);
                errorText.setVisibility(VISIBLE);
                errorText.setText(getResources().getString(R.string.error_log_pass));

            } else if(login.isEmpty()){
                enterButton.setEnabled(true);
                error(loginText, loginCard);
                errorText.setVisibility(VISIBLE);
                errorText.setText(getResources().getString(R.string.err_log));
            } else {
                enterButton.setEnabled(true);
                error(passwordText, passwordCard);
                errorText.setVisibility(VISIBLE);
                errorText.setText(getResources().getString(R.string.err_pass));
            }
            LocationUtil.requestLocationNetwork(requireActivity(), new LocationUtil.LocationCallback() {
                @Override
                public void onLocationReceived(double latitude, double longitude, String countryName, String countryCode) {
                    viewModel.setRegion(countryCode);
                }

                @Override
                public void onError(String error) {
                    Log.e("AccountViewModel", error);
                }
            });
        });
    }
    @SuppressLint("SetTextI18n")
    private void observeResult(){
        viewModel.getUser().observe(getViewLifecycleOwner(),result ->{
            switch (result.status){
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    onSuccess();
                    break;
                case ERROR:
                    hideError();
                    errorText.setVisibility(VISIBLE);
                    if(result.message.startsWith("failed to connect")){
                        errorText.setText(getResources().getString(R.string.err_inet));
                    } else {
                        error(loginText,loginCard);
                        error(passwordText,passwordCard);
                        errorText.setText(getResources().getString(R.string.err_bad_data));
                    }
                    hideLoading();
                    enterButton.setEnabled(true);
                    break;
            }
        });
    }
    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void hideError(){
        int defaultStrokeColor = ContextCompat.getColor(requireContext(), R.color.background);
        int defaultShadowColor = ContextCompat.getColor(requireContext(), R.color.black);

        InnerGlowMaterialCardView[] cards = {loginCard, passwordCard};

        for (InnerGlowMaterialCardView card : cards) {
            card.setInnerGlowEnabled(false);
            card.setCardElevation(dpToPx(0));
            card.setStrokeColor(Color.TRANSPARENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                card.setOutlineAmbientShadowColor(defaultShadowColor);
                card.setOutlineSpotShadowColor(defaultShadowColor);
            }
        }

        errorText.setText("");
        errorText.setVisibility(View.GONE);
    }
    private void showLoading(){
        textReg.setVisibility(INVISIBLE);
        progressBar.setVisibility(VISIBLE);
    }
    private void hideLoading(){
        textReg.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
    }
    private void error(@NonNull EditText text, @NonNull InnerGlowMaterialCardView card) {
        int errorColor = ContextCompat.getColor(requireContext(), R.color.red);
        int lightErrorColor = ContextCompat.getColor(requireContext(), R.color.lightRed);

        text.getText().clear();

        float elevationPx = dpToPx(30);
        int strokePx     = (int) dpToPx(2);
        card.setInnerGlowEnabled(true);
        applyErrorStyle(card, elevationPx, strokePx, errorColor,lightErrorColor);
    }
    private void applyErrorStyle(@NonNull InnerGlowMaterialCardView card, float elevationPx, int strokePx, int color,int lightColor) {
        card.setCardElevation(elevationPx);

        card.setStrokeWidth(strokePx);
        card.setStrokeColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            card.setOutlineAmbientShadowColor(color);
            card.setOutlineSpotShadowColor(lightColor);
        }
    }
    private float dpToPx(int dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
    private void onSuccess(){
        viewModel.loadStations();
        viewModel.getStationsLoading().observe(getViewLifecycleOwner(), stations -> {
            switch (stations.status){
                case LOADING:
                    progressBar_loading.setVisibility(VISIBLE);
                    loadingText.setVisibility(VISIBLE);
                    if(stations.data == 0){
                        progressBar_loading.setIndeterminate(true);
                        loadingText.setText("Загрузка данных");
                    } else {
                        progressBar_loading.setIndeterminate(false);
                        loadingText.setText("Сохранение данных");
                        progressBar_loading.setProgress(stations.data);
                    }
                    break;

                case SUCCESS:
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();
                    break;

                case ERROR:
                    Toast.makeText(requireContext(), stations.message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}