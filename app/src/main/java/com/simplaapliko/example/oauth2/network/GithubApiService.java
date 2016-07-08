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

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface GithubApiService {

    @POST("authorizations")
    Observable<Result<Authorization>> createAuthorization(
            @Header("Authorization") String authorization,
            @Body AuthorizationRequest authorizationRequest);
}
