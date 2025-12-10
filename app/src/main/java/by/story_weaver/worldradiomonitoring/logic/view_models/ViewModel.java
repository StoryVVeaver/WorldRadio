package by.story_weaver.worldradiomonitoring.logic.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.worldradiomonitoring.logic.UiState;
import by.story_weaver.worldradiomonitoring.logic.models.Station;
import by.story_weaver.worldradiomonitoring.logic.network.RadioApi;
import by.story_weaver.worldradiomonitoring.logic.network.UserApi;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ViewModel extends androidx.lifecycle.ViewModel {
    private final RadioApi radioApi;
    private final UserApi userApi;

    private final MutableLiveData<UiState<List<Station>>> stations = new MutableLiveData<>();

    @Inject
    public ViewModel(RadioApi radioApi, UserApi userApi){
        this.radioApi = radioApi;
        this.userApi = userApi;
    }

    public LiveData<UiState<List<Station>>> getTopClick(){
        return stations;
    }

    public void loadTopClick(int count){
        stations.postValue(UiState.loading());
        radioApi.getTopClicked(count).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Station>> call, @NonNull Response<List<Station>> response) {
                if(response.isSuccessful()){
                    stations.postValue(UiState.success(response.body()));
                } else {
                    stations.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Station>> call, @NonNull Throwable t) {
                stations.postValue(UiState.error(t.getMessage()));
            }
        });
    }
}
