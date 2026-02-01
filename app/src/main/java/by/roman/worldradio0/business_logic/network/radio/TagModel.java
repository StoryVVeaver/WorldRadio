package by.roman.worldradio0.business_logic.network.radio;

public class TagModel {
    private String name;
    private int stationcount;

    public TagModel(String name, int stationcount) {
        this.name = name;
        this.stationcount = stationcount;
    }

    public String getName() {
        return name;
    }

    public int getStationcount() {
        return stationcount;
    }
}
