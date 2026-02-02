package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class CodecModel {
    @SerializedName("name")
    private String name;
    @SerializedName("stationcount")
    private int stationcount;

    public String getCodec(){
        return name;
    }
    public int getCount(){
        return stationcount;
    }
}
