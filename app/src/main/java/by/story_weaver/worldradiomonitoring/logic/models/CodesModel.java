package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class CodesModel {
    @SerializedName("name")
    private String countryCode;
    @SerializedName("stationcount")
    private int count;

    public String getCountryCode(){
        return countryCode;
    }
    public int getCount(){
        return count;
    }
}
