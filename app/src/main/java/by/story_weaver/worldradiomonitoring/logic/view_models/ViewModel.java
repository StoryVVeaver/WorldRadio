package by.story_weaver.worldradiomonitoring.logic.view_models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.worldradiomonitoring.logic.UiState;
import by.story_weaver.worldradiomonitoring.logic.models.CodesModel;
import by.story_weaver.worldradiomonitoring.logic.models.FilterStation;
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
    private final MutableLiveData<UiState<List<CodesModel>>> codes = new MutableLiveData<>();

    private final MutableLiveData<UiState<List<FilterStation>>> filter = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<FilterStation>>> setFilter = new MutableLiveData<>();

    @Inject
    public ViewModel(RadioApi radioApi, UserApi userApi){
        this.radioApi = radioApi;
        this.userApi = userApi;
    }

    public LiveData<UiState<List<Station>>> getTopClick(){
        return stations;
    }
    public LiveData<UiState<List<CodesModel>>> getCountryCodes(){
        return codes;
    }

    public LiveData<UiState<List<FilterStation>>> getStationFilter(){
        return filter;
    }
    public LiveData<UiState<List<FilterStation>>> setStationFilter(){
        return setFilter;
    }

    //RadioBrowser
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
    public void loadCountryCodes(){
        codes.postValue(UiState.loading());
        radioApi.getCountryCodes().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CodesModel>> call, @NonNull Response<List<CodesModel>> response) {
                if(response.isSuccessful()){
                    codes.postValue(UiState.success(response.body()));
                } else {
                    codes.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CodesModel>> call, @NonNull Throwable t) {
                codes.postValue(UiState.error(t.getMessage()));
            }
        });
    }

    //Backend
    public void loadStationFilter(){
        filter.postValue(UiState.loading());

        userApi.getStationFilter().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FilterStation>> call, @NonNull Response<List<FilterStation>> response) {
                if(response.isSuccessful()){
                    filter.postValue(UiState.success(response.body()));
                } else {
                    filter.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FilterStation>> call, @NonNull Throwable t) {
                Log.e("ViewModel", call.request().url().toString());
                filter.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void saveStationFilter(List<FilterStation> list){
        setFilter.postValue(UiState.loading());
        userApi.putStationFilters(list).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FilterStation>> call, @NonNull Response<List<FilterStation>> response) {
                if(response.isSuccessful()){
                    setFilter.postValue(UiState.success(response.body()));
                } else {
                    setFilter.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FilterStation>> call, @NonNull Throwable t) {
                setFilter.postValue(UiState.error(t.getMessage()));
            }
        });
    }
}
