package com.rhuf.brandscan;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.rhuf.brandscan.Facebook.BaseRequestListener;
import com.rhuf.brandscan.Facebook.LoginButton;
import com.rhuf.brandscan.Facebook.SessionEvents;
import com.rhuf.brandscan.Facebook.SessionEvents.AuthListener;
import com.rhuf.brandscan.Facebook.SessionEvents.LogoutListener;
import com.rhuf.brandscan.Facebook.SessionStore;
import com.rhuf.brandscan.webview.WebViewActivity;

import com.google.android.youtube.player.YouTubeIntents;


public class SneakPeekActivity extends Activity {
	
	
    /** Called when the activity is first created. */
	 AlertDialog.Builder alert ;
	 AlertDialog alertDialog;
	 AlertDialog.Builder dlgAlert;
	 
	 ProgressDialog pdialog;
	 ProgressDialog ldialog;
	 
	 Product Pscan;
	 DateFormat dateFormat;
	 Calendar cal;
	
	 String upcCode;
//	 String CurrentAddress;
	 
//	 ArrayList<String> StoreNames;
	 
	 protected AppPrefs appPrefs;
	 
	 Context context;
	 TenSearches DB;
	 
//	 GPS gps;
	 
//	 double lat=0;
//	 double lon=0;
	 
	 //FACEBOOK Variables
	 	private LoginButton mLoginButton;
	    private TextView mText;
	    private Button mRequestButton;
	    private Facebook mFacebook;
	    private AsyncFacebookRunner mAsyncRunner; 
	 //FACEBOOK Variables
	 

	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainmenu);
        context = this;
        appPrefs = new AppPrefs(context);
        
        LoadVariables();
        
//        gps = new GPS();
        
        if(!isConnected())
        	dlgAlert.create().show();
   
        
        isConnectedDialog();
        
        enterCode();
        
        
//        GetPlaces();
        mFacebook = new Facebook(Variables.APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
    	mLoginButton.setVisibility(View.INVISIBLE);
    	mText.setVisibility(View.INVISIBLE);
       	
        
        if(!appPrefs.getFBLogin() && appPrefs.getPromptFacebook())
        {
        	LoginFacebook();
        }
        alertDialog = alert.create();
        //gps=null;
        
        if(appPrefs.getShowTutorial())
        {
        	appPrefs.setShowTutorial(false);  
        	try{
        		Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
        		intent.putExtra("URL","help");
				startActivity(intent);  
           }
            catch(Exception e)
            {
          	  System.out.println(e.toString());
            }
        }
        	
        
    }

	private void LoginFacebook() {
		// TODO Auto-generated method stub
		
		//System.out.print("I AM HERE YOU ARE NOT");
		SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(this, mFacebook);
        //set login prefs 
        
        mRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mAsyncRunner.request("me", new SampleRequestListener());
            }
        });
        //mRequestButton.performClick();        
	}


	public final Button.OnClickListener scanAnything = new Button.OnClickListener() {
        public void onClick(View v) {
          Intent intent = new Intent("com.rhuf.barcodescanner.SCAN");
          intent.putExtra("SCAN_MODE", "PRODUCT_MODE,QR_CODE_MODE");
          //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
          try{startActivityForResult(intent, 5);}
          catch(Exception e)
          {
        	  System.out.println(e.toString());
          }
        }
      };

      
    public final Button.OnClickListener historyListener = new Button.OnClickListener() {
          public void onClick(View v) {
            Intent intent = new Intent(SneakPeekActivity.this,History.class);
            try{
            	startActivity(intent);
            }
            catch(Exception e)
            {
          	  System.out.println(e.toString());
            }
          }
        };
        
    public final Button.OnClickListener home = new Button.OnClickListener() {
            public void onClick(View v) {
              try{
            	  Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
                  intent.putExtra("URL", "http://www.brandscan.org/");
                  startActivity(intent);
              }
              catch(Exception e)
              {
            	  System.out.println(e.toString());
              }
            }
          };
      
      
    public final Button.OnClickListener enterCode = new Button.OnClickListener() {
          public void onClick(View v) {
        	  try{
        		  alertDialog.show();
        		  
           }
            catch(Exception e)
            {
          	  System.out.println(e.toString());
            }
          }
        };
        
    public final Button.OnClickListener help = new Button.OnClickListener() {
            public void onClick(View v) {
          	  try{
          		Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
          		intent.putExtra("URL","help");
				startActivity(intent);  
             }
              catch(Exception e)
              {
            	  System.out.println(e.toString());
              }
            }
          };
      
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	   if (requestCode == 5) {
    	      if (resultCode == RESULT_OK) {
    	         String contents = intent.getStringExtra("SCAN_RESULT");
    	         upcCode = contents;
    	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
    	         // Handle successful scan
    	         Log.e("Format", format);
    	         Log.e("Contents", contents);
	    	         if(TextUtils.equals(format, "QR_CODE")/* && contents.startsWith("http://www.youtube.com/")*/)
	    	         {
	    	        	 /*startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
	    	        			 (contents)));*/
	    	        	 ActionOnQRScan(contents);
	    	         }
	    	        /* else if(TextUtils.equals(format, "QR_CODE") && !contents.startsWith("http://www.youtube.com/"))
	    	        	 Toast.makeText(getApplicationContext(), "Unknown Content", Toast.LENGTH_SHORT).show();*/
	    	         else
	    	        	 new ScanTask().execute();
    	        } 
    	      else if (resultCode == RESULT_CANCELED) {
    	    	  //do nothing
    	      }
 	      }
    	   else 
 	    	  mFacebook.authorizeCallback(requestCode, resultCode, intent);	   
    	}

	//FACEBOOK Methods

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
*/
    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
           // mText.setText("You have logged in! ");
        	appPrefs.setFBLogin(true);
        	mRequestButton.performClick();
               
        }

        public void onAuthFail(String error) {
           // mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
           // mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            //mText.setText("You have logged out! ");
           // mRequestButton.setVisibility(View.INVISIBLE);
 
        }
    }

    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
            	           	
            	// process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                //set prefs for user information
               /* final String name = json.getString("name");
                final String email = json.getString("email");
                final String gender = json.getString("gender");
                final String DoB = json.getString("birthday");
                final String age = Integer.toString(getAge(json.getString("birthday")));*/

                appPrefs.setUsername(json.getString("name"));
                appPrefs.setUseremail(json.getString("email"));
                appPrefs.setUserage(Integer.toString(getAge(json.getString("birthday"))));
                appPrefs.setUsergender(json.getString("gender"));
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                SneakPeekActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        new CreateUserTask().execute();
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
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
    
    //FACEBOOK Methods
    
    @Override
    public void onPause()
    {
    	super.onPause();
    }
   
    @Override
    public void onStop()
    {
    	super.onStop();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	if(!isConnected())
        	dlgAlert.create().show();
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	if(!isConnected())
        	dlgAlert.create().show();
    }
    
    private void LoadVariables()
    {
    	findViewById(R.id.ScanCode).setOnClickListener(scanAnything);
        findViewById(R.id.EnterCode).setOnClickListener(enterCode);
        findViewById(R.id.Howto).setOnClickListener(help);
        findViewById(R.id.History).setOnClickListener(historyListener);
        findViewById(R.id.Home).setOnClickListener(home);
        
        //FACEBOOK Buttons
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mRequestButton = (Button) findViewById(R.id.requestButton);
        mLoginButton.setVisibility(View.VISIBLE);
        mText = (TextView) findViewById(R.id.txt);
        //FACEBOOK Buttons
        
        DB = new TenSearches(this);
        
        pdialog=new ProgressDialog(this);
		pdialog.setCancelable(false);
		pdialog.setMessage("Searching ....");
		
		ldialog=new ProgressDialog(this);
		ldialog.setCancelable(false);
		ldialog.setMessage("Locating....");
        
        
    }
    
    private void isConnectedDialog() {
		// TODO Auto-generated method stub
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

    private int getAge(String DoB) 
    {
    	Date dateOfBirth = null;
    	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
			dateOfBirth = formatter.parse(DoB);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();

        dob.setTime(dateOfBirth);

        if (dob.after(now)) 
        {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) 
        {
            age--;
        }

        return age;
    }
  
    private void GetPlaces()
    {
    	
    		//Execute Places Async Task
    		/*new PlaceTask().execute();*/
    }
   
    private void enterCode(){
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   //get current date time with Calendar()
 	    cal = Calendar.getInstance();  
        alert  = new AlertDialog.Builder(this);
    	alert.setTitle("Enter Barcode");
        alert.setMessage("Type the Barcode below ...");

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          Editable value = input.getText();
          // Do something with value!
          if(TextUtils.isEmpty(input.getText().toString()))
          {
        	  Toast.makeText(getApplicationContext(), "Please enter a valid UPC Code", Toast.LENGTH_LONG);
          }else{
		          upcCode = value.toString();
		          new ScanTask().execute();
          }

          }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
        	  dialog.dismiss();
        	  
          }
        });
    }
   

	//Create User Task
    private class CreateUserTask extends AsyncTask<Void, Integer, Void>
    {
    	//Before running code in the separate thread
		@Override
		protected void onPreExecute() 
		{
			
			//show some dialog if needed
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

					//create user HTTP Post Request
					 HttpClient httpclient = new DefaultHttpClient();
					 HttpPost httppost = 
					 new HttpPost(Variables.SITE+"/registeruser/");

					 try {
					        // Add your data
					        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					        nameValuePairs.add(new BasicNameValuePair("email",appPrefs.getUseremail()));
					        nameValuePairs.add(new BasicNameValuePair("name",appPrefs.getUsername()));
					        nameValuePairs.add(new BasicNameValuePair("age",appPrefs.getUserage()));
					        nameValuePairs.add(new BasicNameValuePair("gender",appPrefs.getUsergender()));
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        Log.d("Register User String",nameValuePairs.toString());
					        // Execute HTTP Post Request
					        HttpResponse response = httpclient.execute(httppost);
					        Log.i("Response Register User",response.toString());
					    } catch (ClientProtocolException e) {
					        // TODO Auto-generated catch block
					    } catch (IOException e) {
					        // TODO Auto-generated catch block
					    }
					
				}
			} 
			catch (Exception e) 
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
			//none
		} 	
   }

    private class ScanTask extends AsyncTask<Void, Integer, Void>
    {
    	String status;
		String message;
    	//Before running code in the separate thread
		@Override
		protected void onPreExecute() 
		{
			pdialog.show();
			try{
//				new AddressTask().execute();
	    		Log.d("AddressTask",appPrefs.getCurrentAddress());
			}
			catch(Exception e)
			{
				Log.e("GETADDRESS", e.toString());
			}
		
			//show some dialog if needed
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

					//create user HTTP Post Request
					 HttpClient httpclient = new DefaultHttpClient();
					 HttpPost httppost = 
					 new HttpPost(Variables.SITE+"/scan/");
					 httppost.addHeader("Content-Type", "application/x-www-form-urlencoded"); 
					 
					 

					 try {
					        // Add your data
					        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					        nameValuePairs.add(new BasicNameValuePair("upccode",upcCode));
					        nameValuePairs.add(new BasicNameValuePair("email",appPrefs.getUseremail()));
					        nameValuePairs.add(new BasicNameValuePair("location",appPrefs.getCurrentAddress()));
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        Log.d("ScanTask", "Payload " + nameValuePairs.toString());

					        // Execute HTTP Post Request
					        ResponseHandler<String> responseHandler=new BasicResponseHandler();
					        String responseBody = httpclient.execute(httppost, responseHandler);
					        try {
								JSONObject response = new JSONObject(responseBody);
								status = response.get("status").toString();
								message = response.getString("message").toString();
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SneakPeekActivity.this,"JSON Parse Error: Report error on http://www.brandscan.org", Toast.LENGTH_LONG).show();
							}
					        Log.d("ScanTask", "Response: "+responseBody);
					        
					    } catch (ClientProtocolException e) {
					        // TODO Auto-generated catch block
					    } catch (IOException e) {
					        // TODO Auto-generated catch block
					    }
					
				}
			} 
			catch (Exception e) 
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
			pdialog.dismiss();
			if (status==("true")){
					try{
						DB.open();
						DB.createEntry(upcCode, message);
						DB.close();
						/*Intent i = new Intent(getBaseContext(), WebViewActivity.class);
		    	        i.putExtra("URL", "http://rhuftech.com/ci/index.php/site/product_page/"+upcCode+"/email/"+appPrefs.getUseremail());
		    	        startActivity(i);*/
						
						Uri uri = Uri.parse("http://rhuftech.com/ci/index.php/site/product_page/"+upcCode+"/email/"+appPrefs.getUseremail());
					    Intent i = new Intent(Intent.ACTION_VIEW, uri);
					    startActivity(i);
						
					}
					catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					}
			}
			else
			{
				Toast.makeText(getApplicationContext(),"Status: "+status+" Message: "+ message, Toast.LENGTH_LONG).show();
				Log.d("ScanTask", "Response at Machine: "+"Status: "+status+" Message: "+ message);
				
			}
		}

    }
/*
    private class PlaceTask extends AsyncTask<Void, Integer, Void>
    {
    	
		//Before running code in the separate thread
    	
		@Override
		protected void onPreExecute() 
		{
			
			ldialog.show();
			//show some dialog if needed
 		}
		
		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) 
		{
			
			 This is just a code that delays the thread execution 4 times, 
			 * during 850 milliseconds and updates the current progress. This 
			 * is where the code that is going to be executed on a background
			 * thread must be placed. 
			 
			//Get HTTP Request for Lat Long
/////////////////////////////////////////////////FIX THIS
			// TODO Auto-generated method stub
			//create user HTTP Post Request
			HttpClient httpclient = new DefaultHttpClient();
			String url = "https://maps.googleapis.com/maps/api/place/search/json?";
			try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("location",appPrefs.getLatitude()+","+appPrefs.getLongitude()));
	        nameValuePairs.add(new BasicNameValuePair("radius","50000"));
	        nameValuePairs.add(new BasicNameValuePair("types","grocery_or_supermarket|store|establishment"));
	        nameValuePairs.add(new BasicNameValuePair("name","market"));
	        nameValuePairs.add(new BasicNameValuePair("sensor","false"));
	        nameValuePairs.add(new BasicNameValuePair("key","AIzaSyCm4GBLfOMXnE5CTQfEcKgEP9F2RBJu0P4"));
	        //httpget.setParams((HttpParams) new UrlEncodedFormEntity(nameValuePairs));
	        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
	        Log.i("HELLO HELLO", nameValuePairs.toString());

	        HttpGet httpget = 
					 new HttpGet(url+paramString);
	        // Execute HTTP Post Request
	        ResponseHandler<String> responseHandler=new BasicResponseHandler();
	        String responseBody = httpclient.execute(httpget, responseHandler);
	        try {
				JSONObject response = new JSONObject(responseBody);
				JSONArray results = response.getJSONArray("results");
				
				Log.i("Response",response.toString());
				int count =results.length()-1;
				
				while (count >=0)
				{
					JSONObject jso = results.getJSONObject(count);
					if(StoreNames.contains(jso.getString("name")))
						continue; 
					else
						StoreNames.add(jso.getString("name"));
					count--;
				}
				Log.e("StoreNames", StoreNames.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//Toast.makeText( getApplicationContext(),"1"+e.toString(),Toast.LENGTH_SHORT).show();
			}
	        Log.e("Response Message", responseBody);
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	//Toast.makeText( getApplicationContext(),"2"+e.toString(),Toast.LENGTH_SHORT).show();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	//Toast.makeText( getApplicationContext(),"3"+e.toString(),Toast.LENGTH_SHORT).show();
	    }
	catch (Exception e) 
	{
	e.printStackTrace();
	//Toast.makeText( getApplicationContext(),"4"+e.toString(),Toast.LENGTH_SHORT).show();
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
			ldialog.dismiss();
			AlertDialog.Builder adb = new AlertDialog.Builder(SneakPeekActivity.this);
			CharSequence[] items = StoreNames.toArray(new CharSequence[StoreNames.size()]);
			items=(String[])items;
			adb.setItems(items,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), arg0.toString(),Toast.LENGTH_SHORT).show();
				}
			});
			adb.setTitle("Which one?");
			adb.show();
		}

    }
*/
//    private class GPS
//    {
//    	
//    	GPS()
//    	{
//    		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);	
//    		LocationListener mlocListener = new MyLocationListener();
//    		mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
//    		
//    	}
//    	
//    	public String getAddress() {
//    		/*Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//    		List<Address> list = null;
//			try {
//				list = geoCoder.getFromLocation(lat, lon, 1);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	        if (list != null & list.size() > 0) {
//    	            Address address = list.get(0);
//    	            String result = address.getLocality();
//    	            result+= address.getSubLocality();
//    	            result+=address.getSubAdminArea();
//    	            result+=address.getThoroughfare();
//    	            appPrefs.setCurrentAddress(result);
//    	            return result;
//    	        }
//    	        else
//    	        	return "";*/
//    		new AddressTask().execute();
//    		Log.d("AddressTask",appPrefs.getCurrentAddress());
//    		return appPrefs.getCurrentAddress();
//    	}
//    	
// 	
//    	
//    	public class MyLocationListener implements LocationListener
//    	{
//    		@Override
//    		public void onLocationChanged(Location loc)
//    		{
//    			lat=loc.getLatitude();
//    			lon=loc.getLongitude();
//    			appPrefs.setLatitude((long) lat);
//    			appPrefs.setLongitude((long) lon);
//    			//String Text = "My current location is (BrandScan): " +
//    			//"Latitud = " + loc.getLatitude() +
//    			//"Longitud = " + loc.getLongitude();
//    			//Toast.makeText(getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
//    		
//    		}
//    	
//    		@Override
//    		public void onProviderDisabled(String provider)
//    		{
//    			Toast.makeText( getApplicationContext(),"Gps Disabled Brandscan",Toast.LENGTH_SHORT ).show();
//    		}
//    	
//    	
//    		@Override
//    		public void onProviderEnabled(String provider)
//    		{
//    			Toast.makeText( getApplicationContext(),"Gps Enabled Brandscan",Toast.LENGTH_SHORT).show();
//    		}
//    	
//    		@Override
//    		public void onStatusChanged(String provider, int status, Bundle extras)
//    		{
//    		}
//
//    	}/* End of Class MyLocationListener */
//    }
//    
//
//    
//    
//    private class AddressTask extends AsyncTask<Void, Integer, Void>
//    {
//    	
//    	//Before running code in the separate thread
//		@Override
//		protected void onPreExecute() 
//		{
//			pdialog.show();
//			//show some dialog if needed
//			Log.d("AddressTask","Started");
// 		}
//		
//		//The code to be executed in a background thread.
//		@Override
//		protected Void doInBackground(Void... params) 
//		{
//			/* This is just a code that delays the thread execution 4 times, 
//			 * during 850 milliseconds and updates the current progress. This 
//			 * is where the code that is going to be executed on a background
//			 * thread must be placed. 
//			 */
//			try 
//			{
//				//Get the current thread's token
//				synchronized (this) 
//				{
//
//					Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//		    		List<Address> list = null;
//					try {
//						list = geoCoder.getFromLocation(lat, lon, 1);
//						
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		    	        if (list != null & list.size() > 0) {
//		    	            Address address = list.get(0);
//		    	            String result = address.getLocality();
//		    	            result+= address.getSubLocality();
//		    	            result+=address.getSubAdminArea();
//		    	            result+=address.getThoroughfare();
//		    	            appPrefs.setCurrentAddress(result);
//		    	        }
//					
//				}
//			} 
//			catch (Exception e) 
//			{
//				
//			}
//			return null;
//		}
//
//		//Update the progress
//		@Override
//		protected void onProgressUpdate(Integer... values) 
//		{
//			//none
//		}
//
//		//after executing the code in the thread
//		@Override
//		protected void onPostExecute(Void result) 
//		{
//			pdialog.dismiss();
//			Log.d("AddressTask","Ended");
//		}
//
//    }
    
    private void ActionOnQRScan(String path)
    {
    	Uri uri = Uri.parse(path);  
    		Intent i = new Intent(Intent.ACTION_VIEW,uri);
    		startActivity(i);
  	
//    	int index = path.lastIndexOf("=");
//    	String VidID = path.substring(index+1, path.length());
//    	YouTubeIntents.canResolvePlayVideoIntent(this.context);
//   	 	Intent i = 
//   	 			YouTubeIntents.createPlayVideoIntentWithOptions(this.context, VidID, true, true);
//   	 Log.d("YouTubeAPI","VidID: " + VidID);
    	  
    	  //i.setAction(Intent.ACTION_VIEW);
          
          //i.setDataAndType(uri, "url/*");
          try {
              startActivity(i);
          } catch (ActivityNotFoundException e) {
              // Handle exception
        	  
          } catch (Exception e) {
             //
          }  
    	
    }
}
    
    
