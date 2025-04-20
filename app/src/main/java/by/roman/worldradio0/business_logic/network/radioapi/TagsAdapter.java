package by.roman.worldradio0.business_logic.network.radioapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TagsAdapter implements JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            return context.deserialize(json, String[].class);
        } else if (json.isJsonPrimitive()) {
            return json.getAsString();
        }
        return null;
    }
}
