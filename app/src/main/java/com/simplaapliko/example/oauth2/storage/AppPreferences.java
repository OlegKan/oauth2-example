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

package com.simplaapliko.example.oauth2.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.simplaapliko.example.oauth2.App;

public class AppPreferences {

    private static final String PREF_TOKEN = "token";
    private static final String PREF_AUTHORIZATION_ID = "authorization_id";

    private AppPreferences() {}

    public static String getToken() {
        return getPreferences()
                .getString(PREF_TOKEN, "");
    }

    public static void setToken(String token) {
        getPreferences().edit()
                .putString(PREF_TOKEN, token)
                .apply();
    }

    private static SharedPreferences getPreferences() {
        Context context = App.getAppContext();
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getAuthorizationId() {
        return getPreferences()
                .getInt(PREF_AUTHORIZATION_ID, -1);
    }

    public static void setAuthorizationId(int authorizationId) {
        getPreferences().edit()
                .putInt(PREF_AUTHORIZATION_ID, authorizationId)
                .apply();
    }
}
