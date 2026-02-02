package by.story_weaver.worldradiomonitoring.logic.view_models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import by.story_weaver.worldradiomonitoring.logic.UiState;
import by.story_weaver.worldradiomonitoring.logic.models.*;
import by.story_weaver.worldradiomonitoring.logic.network.*;
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ViewModel extends androidx.lifecycle.ViewModel {
    private final RadioApi radioApi;
    private final UserApi userApi;

    private final MutableLiveData<UiState<List<Station>>> stations = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<Station>>> station = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<CodesModel>>> codes = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<LangModel>>> lang = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<BrokeModel>>> broke = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<CodecModel>>> tag = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<ClickModel>>> click = new MutableLiveData<>();

    private final MutableLiveData<UiState<List<FilterStation>>> filter = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<FilterStation>>> setFilter = new MutableLiveData<>();
    private final MutableLiveData<UiState<Long>> countUsers = new MutableLiveData<>();

    @Inject
    public ViewModel(RadioApi radioApi, UserApi userApi){
        this.radioApi = radioApi;
        this.userApi = userApi;
    }

    public LiveData<UiState<List<Station>>> getTopClick(){
        return stations;
    }
    public LiveData<UiState<List<Station>>> getStationByUUID(){
        return station;
    }
    public LiveData<UiState<List<CodesModel>>> getCountryCodes(){
        return codes;
    }
    public LiveData<UiState<List<LangModel>>> getLang(){
        return lang;
    }
    public LiveData<UiState<List<BrokeModel>>> getBroke(){
        return broke;
    }
    public LiveData<UiState<List<CodecModel>>> getTag(){
        return tag;
    }
    public LiveData<UiState<List<ClickModel>>> getClick(){
        return click;
    }

    public LiveData<UiState<List<FilterStation>>> getStationFilter(){
        return filter;
    }
    public LiveData<UiState<List<FilterStation>>> setStationFilter(){
        return setFilter;
    }
    public LiveData<UiState<Long>> getCountUsers(){
        return countUsers;
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
    public void loadLanguages(){
        lang.postValue(UiState.loading());
        radioApi.getLanguages().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<LangModel>> call, @NonNull Response<List<LangModel>> response) {
                if(response.isSuccessful()){
                    lang.postValue(UiState.success(response.body()));
                } else {
                    lang.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LangModel>> call, @NonNull Throwable t) {
                lang.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadBroken(){
        broke.postValue(UiState.loading());
        radioApi.getBroken().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<BrokeModel>> call, @NonNull Response<List<BrokeModel>> response) {
                if(response.isSuccessful()){
                    broke.postValue(UiState.success(response.body()));
                } else {
                    broke.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BrokeModel>> call, @NonNull Throwable t) {
                broke.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadTags(){
        tag.postValue(UiState.loading());
        radioApi.getCodecs().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CodecModel>> call, @NonNull Response<List<CodecModel>> response) {
                if(response.isSuccessful()){
                    tag.postValue(UiState.success(response.body()));
                } else {
                    tag.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CodecModel>> call, @NonNull Throwable t) {
                tag.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadClicks(){
        click.postValue(UiState.loading());
        radioApi.getClickHistory().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ClickModel>> call, @NonNull Response<List<ClickModel>> response) {
                if(response.isSuccessful()){
                    click.postValue(UiState.success(response.body()));
                } else {
                    click.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ClickModel>> call, @NonNull Throwable t) {
                click.postValue(UiState.error(t.getMessage()));
            }
        });
    }
    public void loadStation(String uuid){
        station.postValue(UiState.loading());
        radioApi.getStationByUUID(uuid).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Station>> call, @NonNull Response<List<Station>> response) {
                if(response.isSuccessful()){
                    station.postValue(UiState.success(response.body()));
                } else {
                    station.postValue(UiState.error(response.code() + " "));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Station>> call, @NonNull Throwable t) {
                station.postValue(UiState.error(t.getMessage()));
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
    public void loadCountUsers(){
        countUsers.postValue(UiState.loading());
        userApi.getCountUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if(response.isSuccessful()){
                    countUsers.postValue(UiState.success(response.body()));
                } else {
                    countUsers.postValue((UiState.error(response.code() + " ")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                countUsers.postValue(UiState.error(t.getMessage()));
            }
        });
    }
}
