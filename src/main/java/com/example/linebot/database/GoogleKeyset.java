package com.example.linebot.database;

public class GoogleKeyset {
    private String lineId;
    private String apiKey;
    private String engineKey;
    private int roomType;

    public String getLineId() {
        return lineId;
    }
    public GoogleKeyset setLineId(String lineId) {
        this.lineId = lineId;
        return this;
    }
    public String getApiKey() {
        return apiKey;
    }
    public GoogleKeyset setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }
    public String getEngineKey() {
        return engineKey;
    }
    public GoogleKeyset setEngineKey(String engineKey) {
        this.engineKey = engineKey;
        return this;
    }
    public int getRoomType() {
        return roomType;
    }
    public GoogleKeyset setRoomType(int roomType) {
        this.roomType = roomType;
        return this;
    }
    
}
