package fr.insapp.insapp.http;

import java.lang.reflect.Type;

import fr.insapp.insapp.models.deserializer.Deserializer;

/**
 * Created by thomas on 12/07/2017.
 */

public class TypeAdapter {

    private Deserializer deserializer;
    private Type type;

    public TypeAdapter(String key, Type type) {
        this.deserializer = new Deserializer<>(key);
        this.type = type;
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
