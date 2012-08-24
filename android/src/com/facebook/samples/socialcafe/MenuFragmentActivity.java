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

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragmentActivity extends Fragment {
	
	ImageView userPicView;
	TextView userNameView;

    private Handler mHandler;
    
    private String userName;
    private Bitmap userPic; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		mHandler = new Handler();
		userName = ((SocialCafeApplication)getActivity().getApplication()).userName;
		userPic = ((SocialCafeApplication)getActivity().getApplication()).userPic;
		if (userName == null || userPic == null) {
			requestUserData();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main_menu, parent, false);
		getActivity().setTitle(R.string.menu);
		
		userNameView = (TextView)v.findViewById(R.id.user_name);
		userPicView = (ImageView)v.findViewById(R.id.user_pic);
		if (userName != null) {
			userNameView.setText(userName);
		}
		if (userPic != null) { 
			userPicView.setImageBitmap(userPic);
		}
		ListView drinkList = (ListView)v.findViewById(R.id.menu_list);
		drinkList.setAdapter(new DrinkListAdapter());
		
		drinkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Intent orderIntent = new Intent(getActivity(), OrderDrinkActivity.class);
				orderIntent.putExtra("DRINK_INDEX", position);
				startActivity(orderIntent);
			}
			
		});
	
		return v;
	}
	
	@Override
	public void onDestroyView () {
		super.onDestroyView();
		userNameView = null;
		userPicView = null;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.options_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_item_logout:
				((SocialCafeApplication)getActivity().getApplication()).asyncRunner.logout(getActivity(), new LogoutRequestListener());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
    /*
     * Request user name, and picture to show on the main screen.
     */
    public void requestUserData() {
    	Bundle params = new Bundle();
    	int screenLayoutSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    	if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_LARGE || 
    			screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
    		params.putString("fields", "name, picture.type(large)");
    	} else {
    		params.putString("fields", "name, picture.type(normal)");
    	}
   		params.putString("fields", "name, picture.type(large)");
		((SocialCafeApplication)getActivity().getApplication()).asyncRunner.request("me", params, new UserDataRequestListener());
    }
	
    
    /*
     * Callback for fetching current user's name, picture, uid.
     */
    public class UserDataRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);
				
	        	final String picURL = jsonObject.getString("picture");
	        	userName = jsonObject.getString("name");
	        	
	        	/* At this point we are in the facebook sdk asyncrunner thread,
	        	 * the UI must however be updated in the UI thread.
	        	 */
	        	mHandler.post(new Runnable() {
	                public void run() {
	                	userNameView.setText(userName);
	                	new DownloadPictureTask().execute(picURL, null, null);
	                }
	            });
	        	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    private class DownloadPictureTask extends AsyncTask<String, Void, Bitmap> {
    	
    	protected Bitmap doInBackground(String... urls) {
        	return Utility.getBitmap(urls[0]);
    	}
    	
    	protected void onProgressUpdate(Void... progress) {
    	}

        protected void onPostExecute(Bitmap result) {
        	userPic = result;
        	userPicView.setImageBitmap(userPic);
        	((SocialCafeApplication)getActivity().getApplication()).saveUserData(userName, userPic);
        }
    }
    
    private class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
        	((SocialCafeApplication)getActivity().getApplication()).clearSession();
            /*
             * callback should be run in the original thread, 
             * not the background thread
             */
            mHandler.post(new Runnable() {
                public void run() {
                	startActivity(new Intent().setClass(getActivity(), LoginActivity.class));  
                }
            });
        }
    }
    
    /**
     * Definition of the list adapter
     */
	public class DrinkListAdapter extends BaseAdapter {
		
		ArrayList<Drink> drinks;
		
		public DrinkListAdapter() {
			drinks = ((SocialCafeApplication)getActivity().getApplication()).drinks;
		}
		
		@Override
		public int getCount() {
			return drinks.size();
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.drink_item, null);
			}
			ImageView image = (ImageView)convertView.findViewById(R.id.drink_image);
			image.setImageResource(drinks.get(position).getImageID());
			
			TextView name = (TextView) convertView.findViewById(R.id.drink_title);
			name.setText(drinks.get(position).getTitle());
			
			TextView info = (TextView) convertView.findViewById(R.id.drink_other_info);
			info.setText(drinks.get(position).getInfo());
			
			return convertView;
		}	
		
	}
	
	
	class ViewHolder {
		ImageView image;
		TextView name;
		TextView info;
	}
    
}
