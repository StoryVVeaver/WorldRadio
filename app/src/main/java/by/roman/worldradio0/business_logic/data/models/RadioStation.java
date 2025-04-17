package by.roman.worldradio0.business_logic.data.models;

public class RadioStation {
    private final String stationUuid;
    private final String name;
    private final String url;
    private final String urlResolved;
    private final String homepage;
    private final String favicon;
    private final String tags;
    private final String country;
    private final String countryCode;
    private final String state;
    private final String language;
    private final String languageCodes;
    private final int votes;
    private final String codec;
    private final int bitrate;
    private final int hls;
    private final double geoLat;
    private final double geoLong;
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
    // Getters
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