package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.User;

public class UserDao {
    protected static final String TABLE_USER = "user";
    protected static final String COLUMN_ID_USER = "id";
    protected static final String COLUMN_LOGIN_USER = "login";
    protected static final String COLUMN_PASSWORD_USER = "password";
    protected static final String COLUMN_IN_SYSTEM_USER = "in_system";
    protected static final String COLUMN_UUID_PLAYING_STATION = "uuid";
    protected static final String COLUMN_AVATAR_USER = "avatar";

    protected static final String CREATE_TABLE_USER = "CREATE TABLE "+ TABLE_USER + " ("+
            COLUMN_ID_USER +               " INTEGER, "+
            COLUMN_LOGIN_USER +            " TEXT, "+
            COLUMN_PASSWORD_USER +         " TEXT, "+
            COLUMN_UUID_PLAYING_STATION +  " TEXT, "+
            COLUMN_AVATAR_USER +           " TEXT, "+
            COLUMN_IN_SYSTEM_USER +        " INTEGER);";
    private final SQLiteDatabase db;
    public UserDao(SQLiteDatabase db){
        this.db = db;
    }
    public int getIdUserInSystem(){
        int id = -1;
        String query = "SELECT " + COLUMN_ID_USER + " FROM " + TABLE_USER +
                " WHERE " + COLUMN_IN_SYSTEM_USER + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        try (cursor){
            if(cursor != null && cursor.moveToNext()){
                id = cursor.getInt(0);
            }
        }
        return id;
    }
    public void setUserInSystem(int id){
        ContentValues values = new ContentValues();
        values.put(COLUMN_IN_SYSTEM_USER, 1);
        String selection = COLUMN_ID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_USER, values, selection, selectionArgs);
    }

    public boolean setUserAvatar(int id, String avatar){
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_AVATAR_USER, avatar);
            String selection = COLUMN_ID_USER + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            int rowsAffected = db.update(TABLE_USER, values, selection, selectionArgs);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("UserDao", e.getMessage() + " ");
            return false;
        }
    }
    public void addUser(@NonNull UserDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_USER, dto.getId());
        values.put(COLUMN_LOGIN_USER, dto.getLogin());
        values.put(COLUMN_PASSWORD_USER, dto.getPassword());
        values.put(COLUMN_UUID_PLAYING_STATION, dto.getPlaying());
        values.put(COLUMN_AVATAR_USER, dto.getAvatar());
        values.put(COLUMN_IN_SYSTEM_USER, dto.getInSystem());
        db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeUser(int id){
        String selection = COLUMN_ID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(TABLE_USER, selection, selectionArgs);
    }
    public boolean exit(){
        String checkQuery = "SELECT * FROM " + TABLE_USER +
                " WHERE " + COLUMN_IN_SYSTEM_USER + " = 1";
        try (Cursor cursor = db.rawQuery(checkQuery, null)) {
            if (cursor.moveToFirst()) {
                ContentValues resetValues = new ContentValues();
                resetValues.put(COLUMN_IN_SYSTEM_USER, 0);
                db.update(TABLE_USER, resetValues, null, null);
                return true;
            }
            return false;
        }
    }
    public User getUserData(int id){
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COLUMN_ID_USER + " = ?", new  String[]{String.valueOf(id)});
        if (cursor.moveToNext()) {
            try (cursor) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID_USER);
                int loginIndex = cursor.getColumnIndex(COLUMN_LOGIN_USER);
                int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD_USER);
                int stationIndex = cursor.getColumnIndex(COLUMN_UUID_PLAYING_STATION);
                int avatarIndex = cursor.getColumnIndex(COLUMN_AVATAR_USER);
                int inSystemIndex = cursor.getColumnIndex(COLUMN_IN_SYSTEM_USER);
                if(idIndex != -1 && loginIndex != -1 && passwordIndex != -1 && stationIndex != -1 && inSystemIndex != -1 && avatarIndex != -1){
                    return new User(cursor.getInt(idIndex),
                            cursor.getString(loginIndex),
                            cursor.getString(passwordIndex),
                            cursor.getString(stationIndex),
                            cursor.getInt(inSystemIndex),
                            cursor.getString(avatarIndex)
                    );
                }
            }
        }
        return null;
    }
    public boolean isTableEmpty() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER, null);
        try (cursor){
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count == 0;
            }
            return true;
        }
    }
    public String getColumnPlayingUUID(int id){
        String uuid = null;
        String Query = "SELECT " + COLUMN_UUID_PLAYING_STATION + " FROM " + TABLE_USER +
                " WHERE " + COLUMN_ID_USER + " =?";
        Cursor cursor = db.rawQuery(Query,new String[]{String.valueOf(id)});
        if(cursor != null && cursor.moveToNext()){
            try (cursor){
                int uuidIndex = cursor.getColumnIndex(COLUMN_UUID_PLAYING_STATION);
                if (uuidIndex != -1) {
                    uuid = cursor.getString(uuidIndex);
                }
            }
        }
        return uuid;
    }
    public void setColumnPlayingUUID(int id, String UUID){
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID_PLAYING_STATION, UUID);
        String selection = COLUMN_ID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_USER, values, selection, selectionArgs);
    }
}
