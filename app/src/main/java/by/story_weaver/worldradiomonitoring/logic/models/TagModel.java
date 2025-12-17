package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class TagModel {
    @SerializedName("name")
    private String name;
    @SerializedName("stationcount")
    private int stationcount;

    public String getTag(){
        return name;
    }
    public int getCount(){
        return stationcount;
    }
}
