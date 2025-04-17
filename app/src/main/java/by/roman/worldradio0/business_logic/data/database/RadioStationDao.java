package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public class RadioStationDao {
    protected static final String TABLE_RADIO_STATION = "radiostation";
    protected static final String COLUMN_UUID_STATION= "stationUUID";
    protected static final String COLUMN_NAME_STATION = "name";
    protected static final String COLUMN_URL_STATION = "url";
    protected static final String COLUMN_URL_RESOLVED_STATION = "url_resolved";
    protected static final String COLUMN_HOMEPAGE_STATION = "homepage";
    protected static final String COLUMN_FAVICON_STATION = "favicon";
    protected static final String COLUMN_TAGS_STATION = "tags";
    protected static final String COLUMN_COUNTRY_STATION = "country";
    protected static final String COLUMN_COUNTRY_CODE_STATION = "countryCode";
    protected static final String COLUMN_STATE_STATION = "state";
    protected static final String COLUMN_LANGUAGE_STATION = "language";
    protected static final String COLUMN_LANGUAGE_CODE_STATION = "languageCode";
    protected static final String COLUMN_VOTES_STATION = "votes";
    protected static final String COLUMN_CODEC_STATION = "codec";
    protected static final String COLUMN_BITRATE_STATION = "bitrate";
    protected static final String COLUMN_HLS_STATION = "hls";
    protected static final String COLUMN_GEO_LATITUDE_STATION = "geo_lat";
    protected static final String COLUMN_GEO_LONGITUDE_STATION = "geo_long";
    protected static final String CREATE_TABLE_RADIO_STATION = "CREATE TABLE "+ TABLE_RADIO_STATION + " ("+
            COLUMN_UUID_STATION +                          " TEXT,"+
            COLUMN_NAME_STATION +                          " TEXT, "+
            COLUMN_URL_STATION +                           " TEXT, "+
            COLUMN_URL_RESOLVED_STATION +                  " TEXT, "+
            COLUMN_HOMEPAGE_STATION +                      " TEXT, "+
            COLUMN_FAVICON_STATION +                       " TEXT, "+
            COLUMN_TAGS_STATION +                          " TEXT, "+
            COLUMN_COUNTRY_STATION +                       " TEXT, "+
            COLUMN_COUNTRY_CODE_STATION +                  " TEXT, "+
            COLUMN_STATE_STATION +                         " TEXT, "+
            COLUMN_LANGUAGE_STATION +                      " TEXT, "+
            COLUMN_LANGUAGE_CODE_STATION +                 " TEXT, "+
            COLUMN_VOTES_STATION +                         " INTEGER, "+
            COLUMN_CODEC_STATION +                         " TEXT, "+
            COLUMN_BITRATE_STATION +                       " INTEGER, "+
            COLUMN_HLS_STATION +                           " INTEGER, "+
            COLUMN_GEO_LATITUDE_STATION +                  " REAL, "+
            COLUMN_GEO_LONGITUDE_STATION +                 " REAL);";
    private final SQLiteDatabase db;
    public RadioStationDao(SQLiteDatabase db) {
        this.db = db;
    }
    public List<String> getCountryList() {
        List<String> countries = new ArrayList<>();
        Cursor cursor = db.query(true,
                TABLE_RADIO_STATION,
                new String[]{COLUMN_COUNTRY_STATION},
                null,null,null,null,null,null);
        if (cursor != null) {
            try (cursor) {
                int countryIndex = cursor.getColumnIndex(COLUMN_COUNTRY_STATION);
                if (countryIndex != -1) {
                    while (cursor.moveToNext()) {
                        String country = cursor.getString(countryIndex);
                        if (country != null && !country.trim().isEmpty()) {
                            countries.add(country.trim());
                        }
                    }
                }
            }
        }
        return countries;
    }
    public List<String> getLanguageList() {
        List<String> languages = new ArrayList<>();
        Cursor cursor = db.query(true,
                TABLE_RADIO_STATION,
                new String[]{COLUMN_LANGUAGE_STATION},
                null,null,null,null,null,null);
        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    int langIndex = cursor.getColumnIndex(COLUMN_LANGUAGE_STATION);
                    if (langIndex != -1) {

                        String lang = cursor.getString(langIndex);
                        if (lang != null && !lang.trim().isEmpty()) {
                            languages.add(lang.trim());
                        }
                    }
                }
            }
        }
        return languages;
    }
    public List<String> getTagsList() {
        Set<String> uniqueTags = new HashSet<>();
        Cursor cursor = db.query(TABLE_RADIO_STATION,
                new String[]{COLUMN_TAGS_STATION},
                null,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            try (cursor) {
                int tagsIndex = cursor.getColumnIndex(COLUMN_TAGS_STATION);
                if (tagsIndex != -1) {
                    while (cursor.moveToNext()) {
                        String tags = cursor.getString(tagsIndex);
                        if (tags != null && !tags.isEmpty()) {
                            String[] splitTags = tags.split(",");
                            for (String tag : splitTags) {
                                String trimmedTag = tag.trim();
                                if (!trimmedTag.isEmpty()) {
                                    uniqueTags.add(trimmedTag);
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(uniqueTags);
    }
    public void addRadioStation(@NonNull RadioStationDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_UUID_STATION,dto.getStationUuid());
        values.put(COLUMN_NAME_STATION,dto.getName());
        values.put(COLUMN_URL_STATION,dto.getUrl());
        values.put(COLUMN_URL_RESOLVED_STATION,dto.getUrlResolved());
        values.put(COLUMN_HOMEPAGE_STATION,dto.getHomepage());
        values.put(COLUMN_FAVICON_STATION,dto.getFavicon());
        values.put(COLUMN_TAGS_STATION,dto.getTags());
        values.put(COLUMN_COUNTRY_STATION,dto.getCountry());
        values.put(COLUMN_COUNTRY_CODE_STATION,dto.getCountryCode());
        values.put(COLUMN_STATE_STATION,dto.getState());
        values.put(COLUMN_LANGUAGE_STATION,dto.getLanguage());
        values.put(COLUMN_LANGUAGE_CODE_STATION,dto.getLanguageCode());
        values.put(COLUMN_VOTES_STATION,dto.getVotes());
        values.put(COLUMN_CODEC_STATION,dto.getCodec());
        values.put(COLUMN_BITRATE_STATION,dto.getBitrate());
        values.put(COLUMN_HLS_STATION,dto.getBitrate());
        values.put(COLUMN_GEO_LATITUDE_STATION,dto.getGeoLat());
        values.put(COLUMN_GEO_LONGITUDE_STATION,dto.getGeoLong());
        db.insertWithOnConflict(TABLE_RADIO_STATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public RadioStation getStationById(String uuid){
        Cursor cursor = db.query(TABLE_RADIO_STATION,
                null,
                COLUMN_UUID_STATION + " = ?",
                new String[]{uuid},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try (cursor) {
                return createStationFromCursor(cursor);
            }
        }
        return null;
    }
    public List<RadioStation> getAllStations() {
        List<RadioStation> stations = new ArrayList<>();

        Cursor cursor = db.query(TABLE_RADIO_STATION,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    stations.add(createStationFromCursor(cursor));
                }
            }
        }
        return stations;
    }
    public List<RadioStation> getFilteredStations(Filter filter) {
        List<RadioStation> stationList = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = getSelectionArgs(filter, selection);

        String orderBy = null;
        switch (filter.getSort()) {
            case 1: orderBy = COLUMN_NAME_STATION + " ASC"; break;
            case 2: orderBy = COLUMN_VOTES_STATION + " DESC"; break;
            case 3: orderBy = COLUMN_BITRATE_STATION + " DESC"; break;
        }

        Cursor cursor = db.query(TABLE_RADIO_STATION,
                null,
                selection.length() > 0 ? selection.toString() : null,
                selectionArgs.toArray(new String[0]),
                null,
                null,
                orderBy);

        if (cursor != null) {
            try (cursor){
                while (cursor.moveToNext()) {
                    RadioStation station = createStationFromCursor(cursor);
                    if (station != null) {
                        stationList.add(station);
                    }
                }
            }
        }
        return stationList;
    }
    public int getCountFilteredStations(Filter filter){
        List<RadioStation> stationList = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = getSelectionArgs(filter, selection);

        Cursor cursor = db.query(TABLE_RADIO_STATION,
                new String[]{"COUNT(*)"},
                selection.length() > 0 ? selection.toString() : null,
                selectionArgs.toArray(new String[0]),
                null,
                null,
                null);
        int count = 0;
        try (cursor) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }
        @NonNull
        private static List<String> getSelectionArgs(Filter filter, StringBuilder selection) {
        List<String> selectionArgs = new ArrayList<>();

        if (filter.getCountry() != null) {
            selection.append(COLUMN_COUNTRY_STATION).append(" = ?");
            selectionArgs.add(filter.getCountry());
        }

        if (filter.getTag() != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append("(")
                    .append(COLUMN_TAGS_STATION).append(" = ? OR ")
                    .append(COLUMN_TAGS_STATION).append(" LIKE ? OR ")
                    .append(COLUMN_TAGS_STATION).append(" LIKE ? OR ")
                    .append(COLUMN_TAGS_STATION).append(" LIKE ?")
                    .append(")");
            selectionArgs.add(filter.getTag());
            selectionArgs.add(filter.getTag() + ",%");
            selectionArgs.add("%," + filter.getTag());
            selectionArgs.add("%," + filter.getTag() + ",%");
        }

        if (filter.getLang() != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(COLUMN_LANGUAGE_STATION).append(" = ?");
            selectionArgs.add(filter.getLang());
        }
        return selectionArgs;
    }
        private RadioStation createStationFromCursor(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(COLUMN_UUID_STATION);
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME_STATION);
        int streamUrlIndex = cursor.getColumnIndex(COLUMN_URL_STATION);
        int urlResolvedIndex = cursor.getColumnIndex(COLUMN_URL_RESOLVED_STATION);
        int homepageIndex = cursor.getColumnIndex(COLUMN_HOMEPAGE_STATION);
        int logoUrlIndex = cursor.getColumnIndex(COLUMN_FAVICON_STATION);
        int tagsIndex = cursor.getColumnIndex(COLUMN_TAGS_STATION);
        int countryIndex = cursor.getColumnIndex(COLUMN_COUNTRY_STATION);
        int countryCodeIndex = cursor.getColumnIndex(COLUMN_COUNTRY_CODE_STATION);
        int stateIndex = cursor.getColumnIndex(COLUMN_STATE_STATION);
        int langIndex = cursor.getColumnIndex(COLUMN_LANGUAGE_STATION);
        int langCodeIndex = cursor.getColumnIndex(COLUMN_LANGUAGE_CODE_STATION);
        int votesIndex = cursor.getColumnIndex(COLUMN_VOTES_STATION);
        int codecIndex = cursor.getColumnIndex(COLUMN_CODEC_STATION);
        int bitrateIndex = cursor.getColumnIndex(COLUMN_BITRATE_STATION);
        int hlsIndex = cursor.getColumnIndex(COLUMN_HLS_STATION);
        int latIndex = cursor.getColumnIndex(COLUMN_GEO_LATITUDE_STATION);
        int longIndex = cursor.getColumnIndex(COLUMN_GEO_LONGITUDE_STATION);

        if (idIndex != -1 && nameIndex != -1 && streamUrlIndex != -1 &&
                urlResolvedIndex != -1 && homepageIndex != -1 && logoUrlIndex != -1 &&
                tagsIndex != -1 && countryIndex != -1 && countryCodeIndex != -1 &&
                stateIndex != -1 && langIndex != -1 && langCodeIndex != -1 &&
                votesIndex != -1 && codecIndex != -1 && bitrateIndex != -1 &&
                hlsIndex != -1 && latIndex != -1 && longIndex != -1) {

            return new RadioStation(
                    cursor.getString(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(streamUrlIndex),
                    cursor.getString(urlResolvedIndex),
                    cursor.getString(homepageIndex),
                    cursor.getString(logoUrlIndex),
                    cursor.getString(tagsIndex),
                    cursor.getString(countryIndex),
                    cursor.getString(countryCodeIndex),
                    cursor.getString(stateIndex),
                    cursor.getString(langIndex),
                    cursor.getString(langCodeIndex),
                    cursor.getInt(votesIndex),
                    cursor.getString(codecIndex),
                    cursor.getInt(bitrateIndex),
                    cursor.getInt(hlsIndex),
                    cursor.getDouble(latIndex),
                    cursor.getDouble(longIndex)
            );
        } else {
            throw new IllegalStateException("Ошибка при чтении RadioStation из базы: не найдены нужные поля.");
        }
    }
}
