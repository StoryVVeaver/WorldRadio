package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;

public class FilterDao {

    protected static final String TABLE_FILTER = "filter";
    protected static final String COLUMN_USER_ID_FILTER = "user_id_f";
    private static final String COLUMN_NAME_FILTER = "name_f";
    private static final String COLUMN_CODEC_FILTER = "codec_f";
    public static final String COLUMN_TAGS_FILTER = "style_f";
    public static final String COLUMN_COUNTRY_FILTER = "country_f";
    public static final String COLUMN_LANG_FILTER = "lang_f";
    public static final String COLUMN_SORT_FILTER = "sort_f";
    protected static final String CREATE_TABLE_FILTER = "CREATE TABLE " + TABLE_FILTER + " (" +
            COLUMN_USER_ID_FILTER + " INTEGER, "+
            COLUMN_NAME_FILTER +    " TEXT, "+
            COLUMN_CODEC_FILTER +   " TEXT, "+
            COLUMN_COUNTRY_FILTER + " TEXT, "+
            COLUMN_LANG_FILTER +    " TEXT, "+
            COLUMN_TAGS_FILTER +    " TEXT, "+
            COLUMN_SORT_FILTER +    " INTEGER, "+
            "FOREIGN KEY (" + COLUMN_USER_ID_FILTER + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE"
            + ");";
    private final SQLiteDatabase db;
    public FilterDao(SQLiteDatabase db) {
        this.db = db;
    }
    public void addFilter(@NonNull FilterDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FILTER, dto.getId());
        values.put(COLUMN_COUNTRY_FILTER, dto.getCountry());
        values.put(COLUMN_TAGS_FILTER, dto.getTags());
        values.put(COLUMN_LANG_FILTER, dto.getLang());
        values.put(COLUMN_SORT_FILTER, dto.getSort());
        db.insertWithOnConflict(TABLE_FILTER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Filter getFilters(int id) {
        Cursor cursor = db.query(TABLE_FILTER,
                new String[]{COLUMN_USER_ID_FILTER,COLUMN_NAME_FILTER,COLUMN_CODEC_FILTER, COLUMN_COUNTRY_FILTER,
                        COLUMN_LANG_FILTER, COLUMN_TAGS_FILTER, COLUMN_SORT_FILTER},
                COLUMN_USER_ID_FILTER + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if(cursor != null && cursor.moveToNext()){
            try  (cursor) {
                int idIndex = cursor.getColumnIndex(COLUMN_USER_ID_FILTER);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME_FILTER);
                int codecIndex = cursor.getColumnIndex(COLUMN_CODEC_FILTER);
                int countryIndex = cursor.getColumnIndex(COLUMN_COUNTRY_FILTER);
                int langIndex = cursor.getColumnIndex(COLUMN_LANG_FILTER);
                int tagsIndex = cursor.getColumnIndex(COLUMN_TAGS_FILTER);
                int sortIndex = cursor.getColumnIndex(COLUMN_SORT_FILTER);
                if(idIndex != -1 && nameIndex != -1 && codecIndex != -1 && countryIndex != -1 && langIndex != -1 && tagsIndex != -1 && sortIndex != -1){
                    return new Filter(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(codecIndex),
                            cursor.getString(countryIndex),
                            cursor.getString(langIndex),
                            cursor.getString(tagsIndex),
                            cursor.getInt(sortIndex)
                    );
                }
            }
        }
        return null;
    }
    public void setFilters(@NonNull FilterDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNTRY_FILTER,dto.getCountry());
        values.put(COLUMN_NAME_FILTER,dto.getName());
        values.put(COLUMN_CODEC_FILTER,dto.getCodec());
        values.put(COLUMN_LANG_FILTER,dto.getLang());
        values.put(COLUMN_TAGS_FILTER,dto.getTags());
        values.put(COLUMN_SORT_FILTER,dto.getSort());
        String selection = COLUMN_USER_ID_FILTER + " = ?";
        String[] selectionArgs = {String.valueOf(dto.getId())};
        db.update(TABLE_FILTER, values, selection, selectionArgs);
    }
    public void removeFilters(int id){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_FILTER,"null");
        values.put(COLUMN_CODEC_FILTER,"null");
        values.put(COLUMN_COUNTRY_FILTER,"null");
        values.put(COLUMN_LANG_FILTER,"null");
        values.put(COLUMN_TAGS_FILTER,"null");
        values.put(COLUMN_SORT_FILTER,0);
        String selection = COLUMN_USER_ID_FILTER + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_FILTER, values, selection, selectionArgs);
    }
}
