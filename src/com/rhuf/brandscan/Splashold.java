package com.rhuf.brandscan;
/*package com.rhuf.brandscan;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;


public class SplashOLD extends Activity {

		AlertDialog.Builder dlgAlert;
		AlertDialog.Builder fbAlert;
		AlertDialog fbDialog;
	    final SplashScreen sPlashScreen = this; 
	    ProgressBar progressBar;
	    protected AppPrefs appPrefs;
	    LoadViewTask loadviewtask;
	    
	    

	*//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.splash);
	    
	    appPrefs = new AppPrefs(getApplicationContext());
	    progressBar = (ProgressBar)findViewById(R.id.progressBarOne);
	    loadviewtask  = new LoadViewTask();
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
			 This is just a code that delays the thread execution 4 times, 
			 * during 850 milliseconds and updates the current progress. This 
			 * is where the code that is going to be executed on a background
			 * thread must be placed. 
			 
			try 
			{
				//Get the current thread's token
				synchronized (this) 
				{
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
*/