package com.mill.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.http.BaseRequestCall;
import com.mill.utils.ContextUtils;
import com.mill.utils.LogUtils;
import com.http.HttpClientImp;
import com.http.callback.StringCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextUtils.init(getApplicationContext());
        LogUtils.init(getApplicationContext());

        HttpClientImp.getInstance().getAsnyc(this, "http://www.baidu.com", null, new StringCallback() {
            @Override
            public void onError(BaseRequestCall call, Exception e) {
                Log.i("HttpClientImp", "onError " + e);

            }

            @Override
            public void onResponse(String response) {
                Log.i("HttpClientImp", "onResponse " + response);
            }
        });
    }
}
