package com.lyes.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtils {
    private static final Gson gson = new Gson();

    // Transformer un objet Java en String JSON (pour l'envoyer)
    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    // Transformer un String JSON en objet Java (à la réception)
    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    // Transformer un String JSON en objet Java (pour les types génériques comme List<T>)
    public static <T> T deserialize(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
