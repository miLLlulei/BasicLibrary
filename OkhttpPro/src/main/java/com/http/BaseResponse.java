package com.http;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseResponse {
    public abstract boolean isSuccessful();
    public abstract InputStream byteStream();
    public abstract String string() throws IOException;
    public abstract long contentLength();
    public abstract void close();
}
