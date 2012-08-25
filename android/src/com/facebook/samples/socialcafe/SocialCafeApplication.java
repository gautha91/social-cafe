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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;


public class SocialCafeApplication extends Application {


	private static final String TAG = "SocialCafeApplication";
	private static final String APP_ID = "138483919580948";
	
	public Facebook facebook;
	public AsyncFacebookRunner asyncRunner;
	
	public ArrayList<Drink> drinks;
	public String userName;
	public Bitmap userPic;
	
	@Override
	public void onCreate() {
		super.onCreate();
		facebook = new Facebook(APP_ID);
		asyncRunner = new AsyncFacebookRunner(facebook);
		
		//restore session if one exists
        ApplicationStore.restoreSession(facebook, this);
        
        /* Create a dummy drinks list.
         * Using dummy data for drink info.
         */
        drinks = new ArrayList<Drink>();
        drinks.add(new Drink("Cafe Latte", "2 others enjoyed this", R.drawable.latte, "Small", "http://social-cafe.herokuapp.com/latte.php"));
        drinks.add(new Drink("Iced Mocha", "6 others enjoyed this", R.drawable.icedmocha, "Large", "http://social-cafe.herokuapp.com/icedmocha.php"));
        drinks.add(new Drink("Earl Grey Tea", "3 others enjoyed this", R.drawable.earlgrey, "Medium", "http://social-cafe.herokuapp.com/earlgrey.php"));
        
        //load user data (name and bitmap)
        loadUserData();
	}
	
	public void saveSession() {
        ApplicationStore.saveSession(facebook, this);
	}
	
	public void clearSession() {
        ApplicationStore.clear(this);
	}
	
	public void saveUserData(String name, Bitmap bmp) {
		userName = name;
		userPic = bmp;
		String picFilename = UUID.randomUUID().toString() + ".jpg";
		ApplicationStore.saveUserData(name, picFilename, this);
		OutputStream out = null;
		try {
			out = openFileOutput(picFilename, Context.MODE_PRIVATE);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
		   e.printStackTrace();
		}
		finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.e (TAG, "Error closing the OutputStream: ", e);
				}
			}
		}
	}
	
	public void loadUserData() {
		userName = ApplicationStore.restoreUserName(this);
		String bitmapFile = ApplicationStore.restoreUserPicFileName(this);
		if (bitmapFile != null) {
			String bitmapPath = getFileStreamPath(bitmapFile).getAbsolutePath();
			userPic = BitmapFactory.decodeFile(bitmapPath);
		}
	}
}
