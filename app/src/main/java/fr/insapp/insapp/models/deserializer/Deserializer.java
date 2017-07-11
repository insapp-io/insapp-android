package fr.insapp.insapp.models.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by thomas on 11/07/2017.
 */

public class Deserializer<T> implements JsonDeserializer<T> {

    private String key;

    public Deserializer(String key) {
        this.key = key;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement content = json.getAsJsonObject().get(key);
        return new Gson().fromJson(content, typeOfT);
    }
}
