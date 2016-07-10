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

import com.simplaapliko.example.oauth2.network.request.AuthorizationRequest;
import com.simplaapliko.example.oauth2.network.response.Authorization;
import com.simplaapliko.example.oauth2.network.response.User;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface GithubApiService {

    @POST("authorizations")
    Observable<Result<Authorization>> createAuthorization(
            @Body AuthorizationRequest authorizationRequest);

    @DELETE("authorizations/{authorizationId}")
    Observable<Result<Object>> deleteAuthorization(
            @Path("authorizationId") String authorizationId);

    @GET("user")
    Observable<Result<User>> getUser();
}
