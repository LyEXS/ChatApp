package com.lyes.utils;

import com.google.*;
import com.google.gson.Gson;

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
}