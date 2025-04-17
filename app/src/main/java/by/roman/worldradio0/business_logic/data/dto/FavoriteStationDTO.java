package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;

public class FavoriteStationDTO {
    private int userId;
    private String stationUUID;
    public FavoriteStation toModel(){
        return new FavoriteStation(userId,stationUUID);
    }
    public FavoriteStationDTO fromModel(FavoriteStation favoriteStation){
        FavoriteStationDTO dto = new FavoriteStationDTO();
        dto.userId = favoriteStation.getUserId();
        dto.stationUUID = favoriteStation.getStationUUID();
        return dto;
    }
    public int getUserId() {
        return userId;
    }
    public String getStationUUID() {
        return stationUUID;
    }
}
