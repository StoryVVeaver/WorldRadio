package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class StateModel {
    @SerializedName("name")
    private String state;
    @SerializedName("iso_639")
    private String country;
    @SerializedName("stationcount")
    private int stationcount;

    public String getState(){
        return state;
    }
    public String getCountry(){
        return country;
    }
    public int getCount(){
        return stationcount;
    }
}
