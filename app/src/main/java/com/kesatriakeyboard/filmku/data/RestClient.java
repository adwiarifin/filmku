package com.kesatriakeyboard.filmku.data;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sasuke on 02-Sep-17.
 */

public class RestClient {

    private static OkHttpClient.Builder getClientWithIntercept() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("api_key","3a4e86161038248056d298db969a24f6").build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        return client;
    }

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.themoviedb.org/3/")
            .client(getClientWithIntercept().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
