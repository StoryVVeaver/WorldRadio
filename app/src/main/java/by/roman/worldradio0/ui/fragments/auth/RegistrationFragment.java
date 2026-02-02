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
public class RegistrationFragment extends Fragment {
    private InnerGlowMaterialCardView loginCard;
    private InnerGlowMaterialCardView password1Card;
    private InnerGlowMaterialCardView password2Card;
    private EditText loginText;
    private EditText password1Text;
    private EditText password2Text;
    private ImageView pass1Butt;
    private ImageView pass2Butt;
    private ImageView reg;
    private TextView textReg;
    private TextView errorText;
    private ProgressBar progressBar;
    private ProgressBar progressBar_loading;
    private TextView loadingText;
    private AccountViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        findAll(view);
        initAll();
        //viewModel.useradd();
        buttons();
        observeResult();
        Log.v("RegistrationFragment","Performance - onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAll(@NonNull View view){
        progressBar_loading = view.findViewById(R.id.progressBar_loading_Registration);
        loadingText = view.findViewById(R.id.textStationsLoading_Registration);
        loginText = view.findViewById(R.id.loginInput_Registration);
        password1Text = view.findViewById(R.id.password1Input_Registration);
        password2Text = view.findViewById(R.id.password2Input_Registration);
        loginCard = view.findViewById(R.id.loginCardView_Registration);
        password1Card = view.findViewById(R.id.password1CardView_Registration);
        password2Card = view.findViewById(R.id.password2CardView_Registration);
        pass1Butt = view.findViewById(R.id.pass1Status_Registration);
        pass2Butt = view.findViewById(R.id.pass2Status_Registration);
        textReg = view.findViewById(R.id.textReg_Registration);
        errorText = view.findViewById(R.id.errorText_Registration);
        progressBar = view.findViewById(R.id.progressBar_Registration);
        reg = view.findViewById(R.id.regButton);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
    }
    @SuppressLint("SetTextI18n")
    private void buttons(){
        pass1Butt.setOnClickListener(v -> {
            if (password1Text.getTransformationMethod() instanceof PasswordTransformationMethod) {
                password1Text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pass1Butt.setImageResource(R.drawable.eye);
            } else {
                password1Text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pass1Butt.setImageResource(R.drawable.eye_closed);
            }
            password1Text.setSelection(password1Text.getText().length());
        });
        pass2Butt.setOnClickListener(v -> {
            if (password2Text.getTransformationMethod() instanceof PasswordTransformationMethod) {
                password2Text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pass2Butt.setImageResource(R.drawable.eye);
            } else {
                password2Text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pass2Butt.setImageResource(R.drawable.eye_closed);
            }
            password2Text.setSelection(password2Text.getText().length());
        });

        reg.setOnClickListener(v -> {
            reg.setEnabled(false);
            hideKeyboard(requireActivity());
            hideError();
            String login = loginText.getText().toString();
            String password1 = password1Text.getText().toString();
            String password2 = password2Text.getText().toString();
            boolean isValid = true;
            String errorMessage = "";
            boolean loginEmpty = login.isEmpty();
            boolean pass1Empty = password1.isEmpty();
            boolean pass2Empty = password2.isEmpty();
            if (loginEmpty) {
                error(loginText, loginCard);
                isValid = false;
            }
            if (pass1Empty) {
                error(password1Text, password1Card);
                isValid = false;
            }
            if (pass2Empty) {
                error(password2Text, password2Card);
                isValid = false;
            }
            if (!isValid) {
                if (loginEmpty && (pass1Empty || pass2Empty)) {
                    errorMessage = getString(R.string.error_log_pass);
                } else if (loginEmpty) {
                    errorMessage = getString(R.string.err_log);
                } else {
                    errorMessage = getString(R.string.err_pass);
                }
            }
            else if (!password1.equals(password2)) {
                error(password1Text, password1Card);
                error(password2Text, password2Card);
                errorMessage = getString(R.string.err_pass_not_same);
                isValid = false;
            }
            if (isValid) {
                errorText.setVisibility(View.GONE);
                hideError();
                viewModel.enter(new UserRequest(login, password1));
            } else {
                reg.setEnabled(true);
                errorText.setText(errorMessage);
                errorText.setVisibility(View.VISIBLE);
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
                    if(result.message.equals(getResources().getString(R.string.err_exist))) {
                        error(loginText,loginCard);
                        errorText.setText(result.message);
                    }
                    if(result.message.startsWith("failed to connect")){
                        errorText.setText(getResources().getString(R.string.err_inet));
                    } else {
                        error(loginText,loginCard);
                        error(password1Text,password1Card);
                        error(password2Text,password2Card);
                        errorText.setText("Что-то пошло не так...");
                    }
                    hideLoading();
                    reg.setEnabled(true);
                    hideLoading();
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

        InnerGlowMaterialCardView[] cards = {loginCard, password1Card, password2Card};

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