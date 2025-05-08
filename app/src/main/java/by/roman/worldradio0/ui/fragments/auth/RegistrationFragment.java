package by.roman.worldradio0.ui.fragments.auth;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.view_models.AccountViewModel;
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
        buttons();
        observeResult();
        Log.v("RegistrationFragment","Performance - onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    private void findAll(@NonNull View view){
        loginText = view.findViewById(R.id.loginInput_Registration);
        password1Text = view.findViewById(R.id.password1Input_Registration);
        password2Text = view.findViewById(R.id.password2Input_Registration);
        loginCard = view.findViewById(R.id.loginCardView_Registration);
        password1Card = view.findViewById(R.id.password1CardView_Registration);
        password2Card = view.findViewById(R.id.password2CardView_Registration);
        pass1Butt = view.findViewById(R.id.pass1Status_Registration);
        pass2Butt = view.findViewById(R.id.pass2Status_Registration);
        reg = view.findViewById(R.id.regButton);
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }
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
            String login = loginText.getText().toString();
            String password1 = password1Text.getText().toString();
            String password2 = password2Text.getText().toString();
            if(password1.equals(password2)) {
                if(!login.isEmpty()){
                    if(!password1.isEmpty()) {
                        startRegistration(login,password1);
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
            } else {
                startError();
                Log.e("1","pass");
                //TODO несовпадение паролей
            }
        });
    }
    private void startRegistration(String login, String password1){
        viewModel.reg(new UserRequest(login, password1));
    }
    private void startError(){
        reg.setEnabled(true);
        error();
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
                    startError();
                    break;
            }
        });
    }
    private void showLoading(){
        //TODO
    }
    private void error() {
        int errorColor = ContextCompat.getColor(requireContext(), R.color.red);
        int lightErrorColor = ContextCompat.getColor(requireContext(), R.color.lightRed);

        loginText.setText("");
        password1Text.setText("");
        password2Text.setText("");
        Toast.makeText(requireContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();

        float elevationPx = dpToPx(30);
        int strokePx     = (int) dpToPx(2);
        loginCard.setInnerGlowEnabled(true);
        password1Card.setInnerGlowEnabled(true);
        password2Card.setInnerGlowEnabled(true);
        applyErrorStyle(loginCard, elevationPx, strokePx, errorColor,lightErrorColor);
        applyErrorStyle(password1Card, elevationPx, strokePx, errorColor,lightErrorColor);
        applyErrorStyle(password2Card, elevationPx, strokePx, errorColor,lightErrorColor);
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