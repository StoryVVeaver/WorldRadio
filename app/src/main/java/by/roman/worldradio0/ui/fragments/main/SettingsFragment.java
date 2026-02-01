package by.roman.worldradio0.ui.fragments.main;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.settings.SettingsList;
import by.roman.worldradio0.business_logic.adapters.SettingsAdapter;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.settings.SettingsChangeListener;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.ui.activities.AccountActivity;
import by.roman.worldradio0.ui.fragments.history.HistoryFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageButton history_button;
    private ImageButton log_out_button;
    private ImageButton edit_user_button;
    private RecyclerView recyclerView;
    private TextView textView;
    private TextView text_status;
    private ProgressBar loading;
    private SettingsViewModel viewModel;
    private StateViewModel stateViewModel;
    private Handler handler;
    private Runnable runnable;
    private ImageView avatar;
    private String currentAvatarBase64;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        long startTime = System.nanoTime();
        Log.v("SettingsFragment: performance", "onViewCreated started");
        findAllId(view);
        initAll();
        observeStatus();
        avatar.setOnClickListener(v -> checkPermissionsAndPickImage());
        log_out_button.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getResources().getString(R.string.sign_out))
                    .setMessage(getResources().getString(R.string.sing_out_text))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        viewModel.logOut();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), null)
                    .show();
        });
        edit_user_button.setOnClickListener(v -> {

        });
        history_button.setOnClickListener(v -> {
            stateViewModel.openFullscreen(new HistoryFragment());
        });
        Log.v("SettingsFragment: performance", "onViewCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }

    private void findAllId(@NonNull View view){
        recyclerView = view.findViewById(R.id.recyclerView_Settings);
        textView = view.findViewById(R.id.nameAccountView);
        loading = view.findViewById(R.id.progressBar_Settings);
        text_status = view.findViewById(R.id.text_loading_Settings);
        edit_user_button = view.findViewById(R.id.edit_user_settings);
        log_out_button = view.findViewById(R.id.log_out_settings);
        history_button = view.findViewById(R.id.history_button);
        avatar = view.findViewById(R.id.avatar_settings);
    }

    private void checkPermissionsAndPickImage() {
        if (!isAdded() || getContext() == null) return;
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(requireActivity(), getResources().getString(R.string.photo_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        if (!isAdded()) return;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(getContext(), "No photo app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                processSelectedImage(imageUri);
            }
        }
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            String base64 = convertImageToBase64(imageUri);
            if (base64 != null) {
                currentAvatarBase64 = base64;

                displayBase64Image(base64);

                viewModel.addAvator(base64);
            }
        } catch (Exception e) {
            Toast.makeText(requireActivity(), getResources().getString(R.string.err_photo), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            Bitmap compressedBitmap = compressBitmap(bitmap, 800, 800);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            if (compressedBitmap != bitmap) bitmap.recycle();
            compressedBitmap.recycle();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap compressBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > maxWidth || height > maxHeight) {
            float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
            width = Math.round(width * ratio);
            height = Math.round(height * ratio);

            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        return bitmap;
    }

    private void displayBase64Image(String base64) {
        try {
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            avatar.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initAll(){
        if (!isAdded()) return;
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        if (viewModel.getSettingsModel() == null) return;
        SettingsAdapter adapter = new SettingsAdapter(SettingsList.getSettingsList(viewModel.getSettingsModel(), requireActivity()), new SettingsChangeListener() {
            @Override
            public void onToggleChanged(@NonNull String key, boolean isChecked) {
                viewModel.toggleChange(key,isChecked);
            }

            @Override
            public void onSwitchChanged(@NonNull String key, int pos){
                viewModel.switchChange(key,pos);
            }

            @Override
            public void onClickChanged(@NonNull String key) {
                viewModel.clickChange(key);
            }

            @Override
            public void onSliderChanged(@NonNull String key, int value) {
                viewModel.sliderChange(key,value);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            if (isAdded() && text_status != null) {
                text_status.setVisibility(INVISIBLE);
                text_status.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            }
        };
        User user = viewModel.getUserData();
        if (user != null && user.getLogin() != null) {
            textView.setText(getString(R.string.hello) + " " + user.getLogin());
        }

        loadCurrentAvatar();
    }

    private void loadCurrentAvatar() {
        User user = viewModel.getUserData();
        if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            currentAvatarBase64 = user.getAvatar();
            displayBase64Image(user.getAvatar());
        }
    }

    @SuppressLint("SetTextI18n")
    private void observeStatus(){
        viewModel.getStationsCount().observe(getViewLifecycleOwner(),count -> {
            if (count == null || !isAdded()) return;
            switch (count.status){
                case LOADING:
                    if(count.data == 0){
                        text_status.setVisibility(VISIBLE);
                        loading.setVisibility(VISIBLE);
                        text_status.setText(getResources().getString(R.string.loading));
                        loading.setIndeterminate(true);
                        handler.removeCallbacks(runnable);
                    } else {
                        text_status.setText(getResources().getString(R.string.storing));
                        loading.setIndeterminate(false);
                        text_status.setText(count.data + "%");
                        loading.setProgress(count.data);
                    }
                    break;

                case SUCCESS:
                    loading.setVisibility(INVISIBLE);
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.green));
                    text_status.setText(getResources().getString(R.string.stored));
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    loading.setVisibility(INVISIBLE);
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(),R.color.red));
                    text_status.setText(getResources().getString(R.string.err_update));
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getGettingStatus().observe(getViewLifecycleOwner(), i -> {
            switch (i.status){
                case LOADING:
                    handler.removeCallbacks(runnable);
                    loading.setVisibility(INVISIBLE);
                    text_status.setVisibility(VISIBLE);
                    text_status.setText(getResources().getString(R.string.loading));
                    break;

                case SUCCESS:
                    text_status.setText(getResources().getString(R.string.stored));
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.green));
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    text_status.setText(getResources().getString(R.string.err_loading));
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.red));
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getSendingStatus().observe(getViewLifecycleOwner(), i -> {
            switch (i.status){
                case LOADING:
                    loading.setVisibility(INVISIBLE);
                    text_status.setVisibility(VISIBLE);
                    text_status.setText(getResources().getString(R.string.sending));
                    handler.removeCallbacks(runnable);
                    break;

                case SUCCESS:
                    text_status.setText(getResources().getString(R.string.sent));
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.green));
                    handler.postDelayed(runnable,5000);
                    break;

                case ERROR:
                    text_status.setText(getResources().getString(R.string.err_send));
                    text_status.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.red));
                    handler.postDelayed(runnable,5000);
                    break;
            }
        });
        viewModel.getTimeToLeave().observe(getViewLifecycleOwner(), timeToLeave -> {
            if (getActivity() == null) return;
            requireActivity().startActivity(new Intent(requireContext(), AccountActivity.class));
            requireActivity().finish();
        });

        viewModel.getAddAvatar().observe(getViewLifecycleOwner(), avatarState -> {
            if (avatarState != null && isAdded()) {
                switch (avatarState.status) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), getResources().getString(R.string.avatar_saved), Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), getResources().getString(R.string.err_avatar) + " " + avatarState.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}