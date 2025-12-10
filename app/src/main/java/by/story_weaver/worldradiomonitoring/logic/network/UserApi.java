package by.story_weaver.worldradiomonitoring.logic.network;

import by.story_weaver.worldradiomonitoring.logic.models.User;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {
    @GET()
    Call<User> getAllUsers();
}
