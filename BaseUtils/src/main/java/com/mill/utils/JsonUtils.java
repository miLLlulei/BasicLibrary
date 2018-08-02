package com.mill.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static String listToJsonArray(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        if (list != null) {
            for (String val : list) {
                jsonArray.put(val);
            }
        }
        return jsonArray.toString();
    }

    public static List<String> jsonArrayToList(String jsonArrayStr) {
        List<String> list = null;
        if (jsonArrayStr != null) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(jsonArrayStr);
            } catch (JSONException e) {
                if (LogUtils.isDebug()) {
                    LogUtils.e(JsonUtils.class.getSimpleName(), "jsonArrayToList", e);
                }
            }
            if (jsonArray != null) {
                list = jsonArrayToList(jsonArray);
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    if (LogUtils.isDebug()) {
                        LogUtils.e(JsonUtils.class.getSimpleName(), "jsonArrayToList JSONArray", e);
                    }
                }
            }
        }
        return list;
    }

    public static List<String> jsonArrayFileToList(String jsonArrayFile) {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonArrayFile) && FileUtils.pathFileExist(jsonArrayFile)) {
            String content = FileUtils.readFileToString(new File(jsonArrayFile));
            list = jsonArrayToList(content);
        }
        return list;
    }

    public static void jsonArrayToFile(List<String> list, String file) {
        FileUtils.writeStringToFile(new File(file), listToJsonArray(list));
    }
}
