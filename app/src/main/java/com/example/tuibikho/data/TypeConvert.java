package com.example.tuibikho.data;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TypeConvert {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toStringList(String list) {
        if (list == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(list, listType);
    }

    @TypeConverter
    public static String fromNutritionInfoList(List<NutritionInfo> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<NutritionInfo> toNutritionInfoList(String list) {
        if (list == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<NutritionInfo>>() {}.getType();
        return gson.fromJson(list, listType);
    }



}
