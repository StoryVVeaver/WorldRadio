package by.roman.worldradio0.business_logic.data.dto;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.network.radioapi.Model;

public class RadioStationDTO {
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
    private String languageCode;
    private int votes;
    private String codec;
    private int bitrate;
    private int hls;
    private double geoLat;
    private double geoLong;
    public RadioStation toModel(){
        return new RadioStation(stationUuid,name,url,urlResolved,homepage,favicon,tags,
                country,countryCode, state,language,languageCode,votes,codec,bitrate,hls,geoLat,geoLong);
    }
    public RadioStationDTO fromModel(@NonNull Model radioStation){
        RadioStationDTO dto = new RadioStationDTO();
        dto.stationUuid = radioStation.getStationUuid();
        dto.name = radioStation.getName();
        dto.url = radioStation.getUrl();
        dto.urlResolved = radioStation.getUrlResolved();
        dto.homepage = radioStation.getHomepage();
        dto.favicon = radioStation.getFavicon();
        dto.tags = radioStation.getTags();
        dto.country = radioStation.getCountry();
        dto.countryCode = radioStation.getCountryCode();
        dto.state = radioStation.getState();
        dto.language = radioStation.getLanguage();
        dto.languageCode = radioStation.getLanguageCode();
        dto.votes = radioStation.getVotes();
        dto.codec = radioStation.getCodec();
        dto.bitrate = radioStation.getBitrate();
        dto.hls = radioStation.getHls();
        dto.geoLat = radioStation.getGeoLat();
        dto.geoLong = radioStation.getGeoLong();
        return dto;
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
        return languageCode;
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
