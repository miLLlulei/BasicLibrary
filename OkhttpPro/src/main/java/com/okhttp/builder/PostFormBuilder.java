package com.okhttp.builder;

import com.okhttp.request.PostFormRequest;
import com.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder>{
    private List<FileInput> files = new ArrayList<>();

    @Override
    protected boolean appendParamsToUrl() {
        return false;
    }

    @Override
    public RequestCall createRequest() {
        return new PostFormRequest(url, tag, params, headers, files,id).build();
    }

    public PostFormBuilder files(String key, Map<String, File> files) {
        for (String filename : files.keySet()) {
            this.files.add(new FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
