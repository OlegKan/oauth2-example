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

package com.simplaapliko.example.oauth2.util;

import android.util.Base64;

public final class EncryptionUtils {

    private EncryptionUtils() {}

    public static String toBase64(String input) {
        String encoded;
        encoded = Base64.encodeToString(input.getBytes(), Base64.NO_WRAP);
        return encoded;
    }

    public static String fromBase64(String base64) {
        String decoded;
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        decoded = new String(data);
        return decoded;
    }

    public static String generateBasicAuthorization(String username, String password) {
        String authorization;
        String credentials = String.format("%1$s:%2$s", username, password);
        authorization = String.format("Basic %1$s", toBase64(credentials)).trim();
        return authorization;
    }
}
