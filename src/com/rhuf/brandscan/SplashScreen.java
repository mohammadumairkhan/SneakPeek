/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rhuf.brandscan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashScreen extends FragmentActivity {
    private TextView mLatLng;
    private TextView mAddress;
    private Button mFineProviderButton;
    private Button mBothProviderButton;
    private LocationManager mLocationManager;
    private Handler mHandler;
    private boolean mGeocoderAvailable;
    private boolean mUseFine;
    private boolean mUseBoth;

    // Keys for maintaining UI states after rotation.
    private static final String KEY_FINE = "use_fine";
    private static final String KEY_BOTH = "use_both";
    // UI handler codes.
    private static final int UPDATE_ADDRESS = 1;
    private static final int UPDATE_LATLNG = 2;

    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    
    /**
     * Brandscan Variables Start
     */
    
	AlertDialog.Builder dlgAlert;
	AlertDialog.Builder fbAlert;
	AlertDialog fbDialog;
    final SplashScreen sPlashScreen = this; 
    ProgressBar progressBar;
    protected AppPrefs appPrefs;
    LoadViewTask loadviewtask;
    
    /**
     * Brandscan Variables End
     */
    
    
    

    /**
     * This sample demonstrates how to incorporate location based services in your app and
     * process location updates.  The app also shows how to convert lat/long coordinates to
     * human-readable addresses.
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        // Restore apps state (if exists) after rotation.
        if (savedInstanceState != null) {
            mUseFine = savedInstanceState.getBoolean(KEY_FINE);
            mUseBoth = savedInstanceState.getBoolean(KEY_BOTH);
        } else {
            mUseFine = false;
            mUseBoth = false;
        }
        mLatLng = (TextView) findViewById(R.id.latlng);
        mAddress = (TextView) findViewById(R.id.address);
        // Receive location updates from the fine location provider (gps) only.
        mFineProviderButton = (Button) findViewById(R.id.provider_fine);
        // Receive location updates from both the fine (gps) and coarse (network) location
        // providers.
        mBothProviderButton = (Button) findViewById(R.id.provider_both);

        // The isPresent() helper method is only available on Gingerbread or above.
        mGeocoderAvailable =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();

        // Handler for updating text fields on the UI like the lat/long and address.
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_ADDRESS:
                        mAddress.setText((String) msg.obj);
                    	appPrefs.setCurrentAddress((String) msg.obj);
                        break;
                    case UPDATE_LATLNG:
                        mLatLng.setText((String) msg.obj);
                        break;
                }
            }
        };
        // Get a reference to the LocationManager object.
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        
        appPrefs = new AppPrefs(getApplicationContext());
          progressBar = (ProgressBar)findViewById(R.id.progressBarOne);
	    loadviewtask  = new LoadViewTask();
	    mBothProviderButton.performClick();
	    facebookDialog();
	    fbDialog = fbAlert.create();
	    
	    isConnectedDialog();
	    
	    if(appPrefs.getFirstLaunch())
	    	createSettingsOnFirstLaunch();
        	    
        	    
	    if(!isConnected())
        	dlgAlert.create().show();
        else
        {
        	if(appPrefs.getFBLogin())
        	{
        		loadviewtask.execute();
        	}
        	else 
        	{
        		appPrefs.incCountforPrompt();
        		if(appPrefs.getCountforPrompt()==5)
        		{
        			appPrefs.resetCountforPrompt();
        		}
        		
        		if(appPrefs.getCountforPrompt()==1)
        	    	fbDialog.show();
        		else
        		{
        			
        			loadviewtask.execute();
        		}
        	}
        		
        }      
    }

    // Restores UI states after rotation.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FINE, mUseFine);
        outState.putBoolean(KEY_BOTH, mUseBoth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setup();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the GPS setting is currently enabled on the device.
        // This verification should be done during onStart() because the system calls this method
        // when the user returns to the activity, which ensures the desired location provider is
        // enabled each time the activity resumes from the stopped state.
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
            //new EnableGpsDialogFragment().show(getSupportFragmentManager(), "enableGpsDialog");
        }
    }

    // Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    // Stop receiving location updates whenever the Activity becomes invisible.
    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(listener);
    }

    // Set up fine and/or coarse location providers depending on whether the fine provider or
    // both providers button is pressed.
    private void setup() {
        Location gpsLocation = null;
        Location networkLocation = null;
        mLocationManager.removeUpdates(listener);
        mLatLng.setText(R.string.unknown);
        mAddress.setText(R.string.unknown);
        // Get fine location updates only.
        if (mUseFine) {
//            mFineProviderButton.setBackgroundResource(R.drawable.button_active);
//            mBothProviderButton.setBackgroundResource(R.drawable.button_inactive);
            // Request updates from just the fine (gps) provider.
            gpsLocation = requestUpdatesFromProvider(
                    LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            // Update the UI immediately if a location is obtained.
            if (gpsLocation != null) updateUILocation(gpsLocation);
        } else if (mUseBoth) {
            // Get coarse and fine location updates.
//            mFineProviderButton.setBackgroundResource(R.drawable.button_inactive);
//            mBothProviderButton.setBackgroundResource(R.drawable.button_active);
            // Request updates from both fine (gps) and coarse (network) providers.
            gpsLocation = requestUpdatesFromProvider(
                    LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            networkLocation = requestUpdatesFromProvider(
                    LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

            // If both providers return last known locations, compare the two and use the better
            // one to update the UI.  If only one provider returns a location, use it.
            if (gpsLocation != null && networkLocation != null) {
                updateUILocation(getBetterLocation(gpsLocation, networkLocation));
            } else if (gpsLocation != null) {
                updateUILocation(gpsLocation);
            } else if (networkLocation != null) {
                updateUILocation(networkLocation);
            }
        }
    }

    /**
     * Method to register location updates with a desired location provider.  If the requested
     * provider is not available on the device, the app displays a Toast with a message referenced
     * by a resource id.
     *
     * @param provider Name of the requested provider.
     * @param errorResId Resource id for the string message to be displayed if the provider does
     *                   not exist on the device.
     * @return A previously returned {@link android.location.Location} from the requested provider,
     *         if exists.
     */
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, listener);
            location = mLocationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
        }
        return location;
    }

    // Callback method for the "fine provider" button.
    public void useFineProvider(View v) {
        mUseFine = true;
        mUseBoth = false;
        setup();
    }

    // Callback method for the "both providers" button.
    public void useCoarseFineProviders(View v) {
        mUseFine = false;
        mUseBoth = true;
        setup();
    }

    private void doReverseGeocoding(Location location) {
        // Since the geocoding API is synchronous and may take a while.  You don't want to lock
        // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
        (new ReverseGeocodingTask(this)).execute(new Location[] {location});
    }

    private void updateUILocation(Location location) {
        // We're sending the update to a handler which then updates the UI with the new
        // location.
        Message.obtain(mHandler,
                UPDATE_LATLNG,
                location.getLatitude() + ", " + location.getLongitude()).sendToTarget();

        // Bypass reverse-geocoding only if the Geocoder service is available on the device.
        if (mGeocoderAvailable) doReverseGeocoding(location);
    }

    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            updateUILocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /** Determines whether one Location reading is better than the current Location fix.
      * Code taken from
      * http://developer.android.com/guide/topics/location/obtaining-user-location.html
      *
      * @param newLocation  The new Location that you want to evaluate
      * @param currentBestLocation  The current Location fix, to which you want to compare the new
      *        one
      * @return The better Location object based on recency and accuracy.
      */
    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    // AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
    // we do not want to invoke it from the UI thread.
    private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected Void doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            Location loc = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                // Update address field with the exception.
                Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Format the first line of address (if available), city, and country name.
                String addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                // Update address field on UI.
                Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
            }
            return null;
        }
    }

    /**
     * Dialog to prompt users to enable GPS on the device.
     */
    private class EnableGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enableLocationSettings();
                        }
                    })
                    .create();
        }
    }
        
    
    private boolean isConnected() {
        
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
}


private void isConnectedDialog()
{
	  dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Please Connect your device to the Internet and try again");
        dlgAlert.setTitle("Brand Scan");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("Ok",
        	    new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	          dialog.dismiss();
        	          finish();
        	        }
        	    });
}

private void createSettingsOnFirstLaunch() {
	// TODO Auto-generated method stub
	appPrefs.setShowTutorial(true);
	appPrefs.setFirstLaunch(false);
	appPrefs.setFBLogin(false);
	appPrefs.setPromptFacebook(true);
	appPrefs.setUsername("anonymous");
	appPrefs.setUserage("anonymous");
	appPrefs.setUseremail("anonymous@anonymous.com");
	appPrefs.setCountforPrompt(0);
	Log.i(Variables.tag,"Create Data Committed");
}

private void facebookDialog()
{
	fbAlert  = new AlertDialog.Builder(this);
	fbAlert.setMessage("Have a Facebook profile? Tap Yes to login through Facebook...");
	fbAlert.setTitle("Brand Scan");
	fbAlert.setPositiveButton("OK", null);
	fbAlert.setCancelable(false);
	fbAlert.setPositiveButton("Yes",
    	    new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) {
    	        	appPrefs.setPromptFacebook(true);
    	            loadviewtask.execute();
    	        }
    	    });
    
	fbAlert.setNegativeButton("No, Later",
    	    new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) {
   	        	appPrefs.setPromptFacebook(false);
    	        	loadviewtask.execute();
    	        }
    	    });
}


//To use the AsyncTask, it must be subclassed
private class LoadViewTask extends AsyncTask<Void, Integer, Void>
{
	//Before running code in the separate thread
	@Override
	protected void onPreExecute() 
	{
		progressBar.setVisibility(View.VISIBLE);

	}
	
	//The code to be executed in a background thread.
	@Override
	protected Void doInBackground(Void... params) 
	{
		/* This is just a code that delays the thread execution 4 times, 
		 * during 850 milliseconds and updates the current progress. This 
		 * is where the code that is going to be executed on a background
		 * thread must be placed. 
		 */
		try 
		{
			//Get the current thread's token
			synchronized (this) 
			{
				Log.d("GPS","currentAddress: " + appPrefs.getCurrentAddress());
				this.wait(1500);
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	//Update the progress
	@Override
	protected void onProgressUpdate(Integer... values) 
	{
		//none
	}

	//after executing the code in the thread
	@Override
	protected void onPostExecute(Void result) 
	{
		
		finish();
		Intent i =  new Intent(getApplicationContext(),SneakPeekActivity.class);
		startActivity(i);

	} 	
}
}
