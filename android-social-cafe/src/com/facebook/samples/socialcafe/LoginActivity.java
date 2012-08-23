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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginActivity extends Activity {
	
	protected final int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	
	Button loginButton;
	String[] permissions = {"publish_actions"};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_with_facebook);
		loginButton = (Button)findViewById(R.id.login_with_facebook);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// This will launch Single Sign On if Facebook app is installed
				// else go to webview for web outh.
				((SocialCafeApplication)getApplication()).facebook.authorize(LoginActivity.this, permissions, AUTHORIZE_ACTIVITY_RESULT_CODE, new LoginDialogListener());	
			}
		});
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
	    	case AUTHORIZE_ACTIVITY_RESULT_CODE: {
	    		((SocialCafeApplication)getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
	    		break;
	    	}
    	}
    }
    
    private final class LoginDialogListener implements DialogListener {
    	
        public void onComplete(Bundle values) {
        	finish();
        	//Save the session and launch the Menu activity
        	((SocialCafeApplication)getApplication()).saveSession();
        	startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        }

        public void onFacebookError(FacebookError error) {
    		Toast.makeText(LoginActivity.this, "Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
        
        public void onError(DialogError error) {
    		Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
    		Toast.makeText(LoginActivity.this, "Action Cancelled", Toast.LENGTH_LONG).show();
        }
    }
    

}
