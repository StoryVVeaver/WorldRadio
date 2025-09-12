package by.roman.worldradio0.business_logic.data.dto;


import by.roman.worldradio0.business_logic.data.models.History;

public class HistoryDTO {
    private int user_id;
    private String uuid;

    public History toModel(){
        return new History(user_id,uuid);
    }
    public HistoryDTO fromModel(History history){
        HistoryDTO dto = new HistoryDTO();
        dto.user_id = history.getUser_id();
        dto.uuid = history.getUuid();
        return dto;
    }

    public int getUser_id() {
        return user_id;
    }
    public String getUuid() {
        return uuid;
    }

}

