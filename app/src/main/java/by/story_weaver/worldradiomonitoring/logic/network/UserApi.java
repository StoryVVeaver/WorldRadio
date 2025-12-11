package by.story_weaver.worldradiomonitoring.logic.network;

import java.util.List;

import by.story_weaver.worldradiomonitoring.logic.models.FilterStation;
import by.story_weaver.worldradiomonitoring.logic.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {
    @GET("get/filter")
    Call<List<FilterStation>> getStationFilter();

    @PUT("put/filter")
    Call<List<FilterStation>> putStationFilters(@Body List<FilterStation> list);
}
