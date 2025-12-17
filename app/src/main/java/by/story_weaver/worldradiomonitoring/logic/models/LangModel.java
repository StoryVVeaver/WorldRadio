package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class LangModel {
    @SerializedName("name")
    private String lang;
    @SerializedName("iso_639")
    private String iso_639;
    @SerializedName("stationcount")
    private int stationcount;

    public String getLang(){
        return lang;
    }
    public String getISO(){
        return iso_639;
    }
    public int getCount(){
        return stationcount;
    }
}
