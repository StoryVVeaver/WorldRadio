package by.story_weaver.worldradiomonitoring.logic.models;

import com.google.gson.annotations.SerializedName;

public class Station {
    @SerializedName("changeuuid")
    private String changeUuid;

    @SerializedName("stationuuid")
    private String stationUuid;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("url_resolved")
    private String urlResolved;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("favicon")
    private String favicon;

    @SerializedName("tags")
    private String tags;

    @SerializedName("country")
    private String country;

    @SerializedName("countrycode")
    private String countryCode;

    @SerializedName("iso_3166_2")
    private String iso31662;

    @SerializedName("state")
    private String state;

    @SerializedName("language")
    private String language;

    @SerializedName("languagecodes")
    private String languageCodes;

    @SerializedName("votes")
    private int votes;

    @SerializedName("lastchangetime")
    private String lastChangeTime;

    @SerializedName("lastchangetime_iso8601")
    private String lastChangeTimeIso8601;

    @SerializedName("codec")
    private String codec;

    @SerializedName("bitrate")
    private int bitrate;

    @SerializedName("hls")
    private int hls;

    @SerializedName("lastcheckok")
    private int lastCheckOk;

    @SerializedName("lastchecktime")
    private String lastCheckTime;

    @SerializedName("lastchecktime_iso8601")
    private String lastCheckTimeIso8601;

    @SerializedName("lastcheckoktime")
    private String lastCheckOkTime;

    @SerializedName("lastcheckoktime_iso8601")
    private String lastCheckOkTimeIso8601;

    @SerializedName("lastlocalchecktime")
    private String lastLocalCheckTime;

    @SerializedName("lastlocalchecktime_iso8601")
    private String lastLocalCheckTimeIso8601;

    @SerializedName("clicktimestamp")
    private String clickTimestamp;

    @SerializedName("clicktimestamp_iso8601")
    private String clickTimestampIso8601;

    @SerializedName("clickcount")
    private int clickCount;

    @SerializedName("clicktrend")
    private int clickTrend;

    @SerializedName("ssl_error")
    private int sslError;

    @SerializedName("geo_lat")
    private double geoLat;

    @SerializedName("geo_long")
    private double geoLong;

    @SerializedName("geo_distance")
    private double geoDistance;

    @SerializedName("has_extended_info")
    private boolean hasExtendedInfo;

    // Getters
    public String getStationUuid() { return stationUuid; }
    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getUrlResolved() { return urlResolved; }
    public String getHomepage() {return homepage; }
    public String getFavicon() { return favicon; }
    public String getTags() { return tags; }
    public String getCountry() { return country; }
    public String getCountryCode() { return countryCode; }
    public String getState() {return state; }
    public String getLanguage() { return language; }
    public String getLanguageCode() { return languageCodes; }
    public int getVotes() { return votes; }
    public String getCodec() { return codec; }
    public int getBitrate() { return bitrate; }
    public int getHls() { return hls; }
    public double getGeoLat() { return geoLat; }
    public double getGeoLong() { return geoLong; }
    public int getClickCount(){
        return clickCount;
    }
}
