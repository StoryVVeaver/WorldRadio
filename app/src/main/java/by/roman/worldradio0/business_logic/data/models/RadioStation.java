package by.roman.worldradio0.business_logic.data.models;

public class RadioStation {
    private String stationUuid;
    private String name;
    private String url;
    private String urlResolved;
    private String homepage;
    private String favicon;
    private String tags;
    private String country;
    private String countryCode;
    private String state;
    private String language;
    private String languageCodes;
    private int votes;
    private String codec;
    private int bitrate;
    private int hls;
    private double geoLat;
    private double geoLong;
    public RadioStation(String stationUuid, String name, String url, String urlResolved, String homepage,
                        String favicon, String tags, String country, String countryCode, String state,
                        String language, String languageCodes, int votes, String codec, int bitrate,
                        int hls, double geoLat, double geoLong) {
        this.stationUuid = stationUuid;
        this.name = name;
        this.url = url;
        this.urlResolved = urlResolved;
        this.homepage = homepage;
        this.favicon = favicon;
        this.tags = tags;
        this.country = country;
        this.countryCode = countryCode;
        this.state = state;
        this.language = language;
        this.languageCodes = languageCodes;
        this.votes = votes;
        this.codec = codec;
        this.bitrate = bitrate;
        this.hls = hls;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
    }
    public RadioStation(String uuid, String name, String url, String favicon, String homepage){
        this.stationUuid = uuid;
        this.name = name;
        this.url = url;
        this.favicon = favicon;
        this.homepage = homepage;
    }

    public String getStationUuid() {
        return stationUuid;
    }
    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
    public String getUrlResolved() {
        return urlResolved;
    }
    public String getHomepage() {
        return homepage;
    }
    public String getFavicon() {
        return favicon;
    }
    public String getTags() {
        return tags;
    }
    public String getCountry() {
        return country;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getState() {
        return state;
    }
    public String getLanguage() {
        return language;
    }
    public String getLanguageCode() {
        return languageCodes;
    }
    public int getVotes() {
        return votes;
    }
    public String getCodec() {
        return codec;
    }
    public int getBitrate() {
        return bitrate;
    }
    public int getHls() {
        return hls;
    }
    public double getGeoLat() {
        return geoLat;
    }
    public double getGeoLong() {
        return geoLong;
    }
}