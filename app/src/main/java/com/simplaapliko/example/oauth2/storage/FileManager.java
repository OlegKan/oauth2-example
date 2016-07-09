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

import com.simplaapliko.example.oauth2.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileManager {

    private FileManager() {}

    public static void writeToFile(String fileName, String fileContent) {
        Context context = App.getAppContext();
        writeToFile(fileName, fileContent, context);
    }

    public static void writeToFile(String fileName, String fileContent, Context context) {
        try {
            OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(out);
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        Context context = App.getAppContext();
        return readFile(fileName, context);
    }

    public static String readFile(String fileName, Context context) {
        StringBuilder fileContentBuilder = new StringBuilder();

        File file = context.getFileStreamPath(fileName);
        if (file.exists()) {
            try {
                String fileLine;
                InputStream inputStream = context.openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                while ((fileLine = reader.readLine()) != null) {
                    fileContentBuilder.append(fileLine);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fileContentBuilder.toString();
    }

    public static void deleteFile(String fileName) {
        Context context = App.getAppContext();
        deleteFile(fileName, context);
    }

    public static void deleteFile(String fileName, Context context) {
        try {
            context.deleteFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(String fileName, Context context) {
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }
}
