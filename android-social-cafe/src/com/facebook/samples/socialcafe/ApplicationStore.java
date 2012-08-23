/*
 * Copyright 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.socialcafe;

import com.facebook.android.Facebook;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationStore {
    
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String USER_NAME = "user_name";
    private static final String USER_PIC_FILENAME = "user_pic_filename";
    private static final String KEY = "socialcafe_data";
    
    /*
     * Save the access token and expiry date so you don't have to fetch it each time
	 */
    
    public static boolean saveSession(Facebook session, Context context) {
        Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, session.getAccessToken());
        editor.putLong(EXPIRES, session.getAccessExpires());
        return editor.commit();
    }

    /*
     * Restore the access token and the expiry date from the shared preferences.
     */
    public static boolean restoreSession(Facebook session, Context context) {
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        session.setAccessToken(savedSession.getString(TOKEN, null));
        session.setAccessExpires(savedSession.getLong(EXPIRES, 0));
        return session.isSessionValid();
    }
    
    public static boolean saveUserData(String name, String picFilename, Context context) {
    	Editor editor =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(USER_NAME, name);
        editor.putString(USER_PIC_FILENAME, picFilename);
        return editor.commit();
    }
    
    public static String restoreUserName(Context context) {
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return savedSession.getString(USER_NAME, null);
    }
    
    public static String restoreUserPicFileName(Context context) {
        SharedPreferences savedSession =
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return savedSession.getString(USER_PIC_FILENAME, null);
    }

    public static void clear(Context context) {
        Editor editor = 
            context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
    
}
