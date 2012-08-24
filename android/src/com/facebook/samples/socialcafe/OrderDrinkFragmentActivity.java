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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderDrinkFragmentActivity extends Fragment {
	private int index;
	private static final int ORDER_STATE = 1;
	ProgressDialog dialog;
	
	ArrayList<Drink> drinks;

	@TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		drinks = ((SocialCafeApplication)getActivity().getApplication()).drinks;
		index = getActivity().getIntent().getIntExtra("DRINK_INDEX", -1);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.order_drink, parent, false);
		getActivity().setTitle(R.string.place_order);
		
		TextView userName = (TextView)v.findViewById(R.id.story_order_message);
		userName.setText(getString(R.string.og_message, 
				((SocialCafeApplication)getActivity().getApplication()).userName, drinks.get(index).getTitle()));
		
		ImageView userPic = (ImageView)v.findViewById(R.id.user_order_pic);
		userPic.setImageBitmap(((SocialCafeApplication)getActivity().getApplication()).userPic);
		
		TextView drinkTitle = (TextView)v.findViewById(R.id.drink_order_title);
		drinkTitle.setText(drinks.get(index).getTitle());

		TextView drinkInfo = (TextView)v.findViewById(R.id.drink_order_info);
		drinkInfo.setText(drinks.get(index).getInfo());
		
		final TextView userMessage = (TextView)v.findViewById(R.id.user_message);
		
		ImageView drinkImage = (ImageView)v.findViewById(R.id.drink_order_image);
		drinkImage.setImageResource(drinks.get(index).getImageID());
		
		ImageButton orderButton = (ImageButton)v.findViewById(R.id.order_drink_button);
		orderButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true, true);
				Bundle params = new Bundle();
		   		params.putString("beverage", drinks.get(index).getURL());
		   		if(userMessage.getText() != null && userMessage.getText().length() > 0) {
		   			params.putString("message", userMessage.getText().toString());
		   		}
				((SocialCafeApplication)getActivity().getApplication()).asyncRunner.request("me/social-cafe:order", params, 
						"POST", new OrderDrinkPostListener(), ORDER_STATE);
				
			}
		});
		
		ImageButton tagFriendButton = (ImageButton)v.findViewById(R.id.tag_friends);
		tagFriendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
    	        .setMessage(R.string.tag_friends_not_supported)
    	        .setPositiveButton(R.string.ok, null)
    	        .show();
			}
		});
		
		ImageButton tagLocationButton = (ImageButton)v.findViewById(R.id.tag_location);
		tagLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
    	        .setMessage(R.string.tag_location_not_supported)
    	        .setPositiveButton(R.string.ok, null)
    	        .show();
			}
		});
		
		ImageButton addPhotoButton = (ImageButton)v.findViewById(R.id.add_photo);
		addPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
    	        .setMessage(R.string.add_photo_not_supported)
    	        .setPositiveButton(R.string.ok, null)
    	        .show();
			}
		});
		
		return v;
	}
	
	
	@Override
	public void onDestroyView () {
		super.onDestroyView();
		dialog = null;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(getActivity(), MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    /*
     * Callback for fetching current user's name, picture, uid.
     */
    public class OrderDrinkPostListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	dialog.dismiss();
        	Intent confirmIntent = new Intent (getActivity(), ConfirmationActivity.class);
        	confirmIntent.putExtra("DRINK_INDEX", index);
        	startActivity(confirmIntent);
        }
    }
}
