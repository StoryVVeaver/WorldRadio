package by.roman.worldradio0.business_logic.data.models;

public class History {
    private int user_id;
    private String uuid;

    public History(int user_id, String uuid) {
        this.user_id = user_id;
        this.uuid = uuid;
    }
    public int getUser_id() {
        return user_id;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
