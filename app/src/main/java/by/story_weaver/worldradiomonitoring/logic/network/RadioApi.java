package by.story_weaver.worldradiomonitoring.logic.network;

import java.util.List;

import by.story_weaver.worldradiomonitoring.logic.models.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RadioApi {
    @GET("json/stations/topclick/{count}")
    Call<List<Station>> getTopClicked(@Path("count") int count);

    @GET("json/countrycodes")
    Call<List<CodesModel>> getCountryCodes();

    @GET("json/languages")
    Call<List<LangModel>> getLanguages();

    @GET("json/states")
    Call<List<StateModel>> getStates();

    @GET("json/tags ")
    Call<List<TagModel>> getTags();

    @GET("json/clicks?seconds=600")
    Call<List<ClickModel>> getClickHistory();

    @GET("json/stations/byuuid/{uuid}")
    Call<List<Station>> getStationByUUID(@Path("uuid") String uuid);
}
