package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    protected static final String CREATE_TABLE_USER = "CREATE TABLE "+ TABLE_USER + " ("+
            COLUMN_ID_USER +               " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_LOGIN_USER +            " TEXT, "+
            COLUMN_PASSWORD_USER +         " TEXT, "+
            COLUMN_UUID_PLAYING_STATION +  " TEXT, "+
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
    public void addUser(@NonNull UserDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_USER, dto.getId());
        values.put(COLUMN_LOGIN_USER, dto.getLogin());
        values.put(COLUMN_PASSWORD_USER, dto.getPassword());
        values.put(COLUMN_UUID_PLAYING_STATION, dto.getPlaying());
        values.put(COLUMN_IN_SYSTEM_USER, dto.getInSystem());
        db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeUser(int id){
        String selection = COLUMN_ID_USER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(TABLE_USER, selection, selectionArgs);
    }
    public void exit(){
        String checkQuery = "SELECT * FROM " + TABLE_USER +
                " WHERE " + COLUMN_IN_SYSTEM_USER + " = 1";
        Cursor cursor = db.rawQuery(checkQuery, null);
        try (cursor) {
            if (cursor.moveToFirst()) {
                ContentValues resetValues = new ContentValues();
                resetValues.put(COLUMN_IN_SYSTEM_USER, 0);
                db.update(TABLE_USER, resetValues, null, null);
            }
        }
    }
    public User getUserData(int id){
        return null; //TODO
    }
    //todo
}
