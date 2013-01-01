/*
 * Copyright 2010 Facebook, Inc.
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

package com.rhuf.brandscan.Facebook;
import com.rhuf.brandscan.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.rhuf.brandscan.Facebook.SessionEvents.AuthListener;
import com.rhuf.brandscan.Facebook.SessionEvents.LogoutListener;

public class LoginButton extends ImageButton {
    
    private Facebook mFb;
    private SessionListener mSessionListener = new SessionListener();
    private String[] mPermissions;
    private Activity mActivity;
    
    public LoginButton(Context context) {
        super(context);
    }
    
    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void init(final Activity activity, final Facebook fb) {
    	init(activity, fb, new String[] { "email", "user_birthday"});
    }
    
    public void init(final Activity activity, final Facebook fb,
                     final String[] permissions) {
        mActivity = activity;
        mFb = fb;
        mPermissions = permissions;

        
        setBackgroundColor(Color.TRANSPARENT);
        setAdjustViewBounds(true);
        setImageResource(fb.isSessionValid() ?
                         R.drawable.logout_button : 
                         R.drawable.login_button);
        drawableStateChanged();
        
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
        setOnClickListener(new ButtonOnClickListener());
        performClick();
    }
    
    private final class ButtonOnClickListener implements OnClickListener {
        
        public void onClick(View arg0) {
            if (!mFb.isSessionValid()) {
            	mFb.authorize(mActivity, mPermissions,
                        new LoginDialogListener());
      
            } else {
            	
            	Toast.makeText(mActivity, "Already Logged in",Toast.LENGTH_LONG);
                }
        }
    }

    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
    
    
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
          //  setImageResource(R.drawable.logout_button);
            SessionStore.save(mFb, getContext());
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(getContext());
            setImageResource(R.drawable.login_button);
        }
    }
    
}
