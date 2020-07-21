package com.example.physiclab.services;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    OkHttpClient okHttpClient = new OkHttpClient();

    public String run(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        try(Response response = okHttpClient.newCall(request).execute()){
            return response.body().string();
        }
    }
}
