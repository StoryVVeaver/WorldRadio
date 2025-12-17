package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ClickModel {
    @SerializedName("stationuuid")
    private String stationuuid;
    @SerializedName("clickuuid")
    private String clickuuid;
    @SerializedName("clicktimestamp_iso8601")
    private String clicktimestamp_iso8601;
    @SerializedName("clicktimestamp")
    private String clicktimestamp;

    public String getUUID(){
        return stationuuid;
    }
    public String getClickUUID(){
        return clickuuid;
    }
    public String getTimeISO(){
        return clicktimestamp_iso8601;
    }
    public String getTime(){
        return clicktimestamp;
    }
    public long getTimeMillis() {
        try {
            if (clicktimestamp_iso8601 != null && !clicktimestamp_iso8601.isEmpty()) {
                return Instant.parse(clicktimestamp_iso8601).toEpochMilli();
            } else if (clicktimestamp != null && !clicktimestamp.isEmpty()) {
                String isoString = clicktimestamp.replace(" ", "T") + "Z";
                return Instant.parse(isoString).toEpochMilli();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
