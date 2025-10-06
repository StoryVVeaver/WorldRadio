package by.roman.worldradio0.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.view_models.AccountViewModel;
import by.roman.worldradio0.ui.fragments.auth.EntranceFragment;
import by.roman.worldradio0.ui.fragments.auth.RegistrationFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountActivity extends AppCompatActivity {

    private ConstraintLayout entrance;
    private ConstraintLayout registration;
    private TextView entranceText;
    private TextView registrationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountViewModel viewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        if(viewModel.isUserHere() != -1){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findAllId();
        entrance.setOnClickListener(v1 -> mode(new EntranceFragment(),0));
        registration.setOnClickListener(v1 -> mode(new RegistrationFragment(),1));
    }

    private void findAllId(){
        entrance = findViewById(R.id.entranceMode);
        entranceText = findViewById(R.id.entranceMode_Text);
        registration = findViewById(R.id.registrationMode);
        registrationText = findViewById(R.id.registrationMode_Text);
    }
    private void mode(Fragment f, int num){
        change(f);
        switch (num){
            case 0:
                entrance.setBackgroundColor(getColor(R.color.selectedMode));
                entranceText.setTextColor(getColor(R.color.white));
                registration.setBackgroundColor(getColor(R.color.unselectedMode));
                registrationText.setTextColor(getColor(R.color.unselectedText));
                break;
            case 1:
                entrance.setBackgroundColor(getColor(R.color.unselectedMode));
                entranceText.setTextColor(getColor(R.color.unselectedText));
                registration.setBackgroundColor(getColor(R.color.selectedMode));
                registrationText.setTextColor(getColor(R.color.white));
                break;
        }
    }
    private void change(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        ft.replace(R.id.accountEntranceView,f);
        ft.commit();
    }
}