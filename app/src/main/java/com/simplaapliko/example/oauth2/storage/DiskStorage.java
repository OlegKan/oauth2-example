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

import com.google.gson.Gson;
import com.simplaapliko.example.oauth2.network.response.User;

public class DiskStorage {

    private static final String USER_FILE = "User.json";

    private DiskStorage() {}

    public static User readUser() {
        String fileData = FileManager.readFile(USER_FILE);
        Gson gson = new Gson();
        User user = gson.fromJson(fileData, User.class);
        return user;
    }

    public static void saveUser(User user) {
        Gson gson = new Gson();
        String dataToWrite = gson.toJson(user);
        FileManager.writeToFile(USER_FILE, dataToWrite);
    }

    public static void deleteUser() {
        FileManager.deleteFile(USER_FILE);
    }
}
