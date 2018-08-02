package com.mill.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mill.utils.LogUtils;

import java.lang.reflect.Type;

/**
 * Created by lulei-ms on 2018/2/28.
 */
public class GsonHelper {
    private static Gson gson;

    public static Gson getGsonInstance() {
        if (gson == null) {
            synchronized (GsonHelper.class) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    Expose expose = f.getAnnotation(Expose.class);
                                    if (expose != null && expose.serialize() == false) {
                                        return true; //按注解排除
                                    }
                                    return false;
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> aClass) {
                                    return false;
                                }
                            })
                            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    Expose expose = f.getAnnotation(Expose.class);
                                    if (expose != null && expose.deserialize() == false) {
                                        return true; //按注解排除
                                    }
                                    return false;
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> aClass) {
                                    return false;
                                }
                            })
                            .create();
                }
            }
        }
        return gson;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return getGsonInstance().fromJson(json, classOfT);
        } catch (Exception e) {
            if (LogUtils.isDebug()) {
                throw e;
            }
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return getGsonInstance().fromJson(json, typeOfT);
        } catch (Exception e) {
            if (LogUtils.isDebug()) {
                throw e;
            }
            e.printStackTrace();
        }
        return null;
    }
}
