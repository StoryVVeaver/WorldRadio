package by.story_weaver.worldradiomonitoring.logic.network;

import java.util.List;

import by.story_weaver.worldradiomonitoring.logic.models.CodesModel;
import by.story_weaver.worldradiomonitoring.logic.models.Station;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RadioApi {
    @GET("json/stations/topclick/{count}")
    Call<List<Station>> getTopClicked(@Path("count") int count);

    @GET("json/countrycodes")
    Call<List<CodesModel>> getCountryCodes();
}
