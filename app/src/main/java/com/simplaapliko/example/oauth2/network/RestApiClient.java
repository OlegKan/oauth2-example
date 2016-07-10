/*
 * Copyright (C) 2016 Oleg Kan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplaapliko.example.oauth2.network;

import android.text.TextUtils;

import com.simplaapliko.example.oauth2.storage.AppPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Func1;

public class RestApiClient {

    public static final Func1<Result<?>, Boolean> RESULT_SUCCESS =
            result -> !result.isError() && result.response().isSuccessful();

    private static final String BASE_URL = "https://api.github.com/";

    private RestApiClient() {}

    public static GithubApiService githubApiService(String basicAuthorization) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getCredentialClient(basicAuthorization))
                .build();

        return retrofit.create(GithubApiService.class);
    }

    public static GithubApiService githubApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getTokenClient())
                .build();

        return retrofit.create(GithubApiService.class);
    }

    private static OkHttpClient getCredentialClient(String basicAuthorization) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(logging);
        okHttpClient.addNetworkInterceptor(chain -> {
            Request.Builder builder = chain.request().newBuilder()
                    .addHeader("Authorization", basicAuthorization);

            Request request = builder.build();
            return chain.proceed(request);
        });

        return okHttpClient.build();
    }

    private static OkHttpClient getTokenClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(logging);
        okHttpClient.addNetworkInterceptor(chain -> {
            Request.Builder builder = chain.request().newBuilder();

            String accessToken = AppPreferences.getToken();
            if (!TextUtils.isEmpty(accessToken)) {
                String headerValue = String.format("token %1$s", accessToken);
                builder.addHeader("Authorization", headerValue);
            }

            Request request = builder.build();
            return chain.proceed(request);
        });

        return okHttpClient.build();
    }
}
