package by.roman.worldradio0.business_logic.network.radio;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;

public class DataFromRadio {
    private final radio radio;

    public DataFromRadio() {
        this.radio = new radio();
    }
    public void getStations(RadioCallback<List<RadioStation>> callback, Filter filter, int offset, int limit) {
        radio.fetchStations(callback, filter, offset, limit);
    }
    public void getStationsByUUID(RadioCallback<List<RadioStation>> callback, List<String> list) {
        radio.fetchStationsByUUID(callback, list);
    }
    public void getCountries(RadioCallback<List<CountryModel>> callback){
        radio.getCountries(callback);
    }
    public void getLang(RadioCallback<List<LangModel>> callback){
        radio.getLang(callback);
    }
    public void getTags(RadioCallback<List<TagModel>> callback, String filter){
        radio.getTags(callback, filter);
    }
    public void getCodec(RadioCallback<List<CodecModel>> callback){
        radio.getCodec(callback);
    }
    public void getNames(RadioCallback<List<RadioStation>> callback, String filter){
        radio.getNames(callback, filter);
    }
    public void click(String uuid, RadioCallback<ClickModel> callback){
        radio.click(uuid, callback);
    }
    public void vote(String uuid, RadioCallback<VoteModel> callback){
        radio.vote(uuid, callback);
    }
    public void getStationsByLocation(int limit, double lat, double lon, double distance, RadioCallback<List<RadioStation>> callback){
        radio.getStationsByLocation(callback, lat, lon, distance, limit);
    }
}

