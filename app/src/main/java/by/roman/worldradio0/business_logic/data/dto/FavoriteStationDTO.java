package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;

public class FavoriteStationDTO {
    private int id;
    private int userId;
    private String stationUUID;
    public FavoriteStation toModel(){
        return new FavoriteStation(id,userId,stationUUID);
    }
    public FavoriteStationDTO fromModel(FavoriteStation favoriteStation){
        FavoriteStationDTO dto = new FavoriteStationDTO();
        dto.id = favoriteStation.getId();
        dto.userId = favoriteStation.getUserId();
        dto.stationUUID = favoriteStation.getStationUUID();
        return dto;
    }
    public int getId() {
        return id;
    }
    public int getUserId() {
        return userId;
    }
    public String getStationUUID() {
        return stationUUID;
    }
}
