package com.rhuf.brandscan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPrefs {
	public static String settings = "settings";
     private SharedPreferences appSharedPrefs;
     private Editor prefsEditor;
      
    private static String FirstLaunch = "First Launch";			//Boolean
 	private static String PromptFacebook = "Prompt Facebook";	//Boolean
 	private static String FBLogin = "Facebook Logged in";		//Boolean
 	private static String Username = "Name";					//String
 	private static String Userage = "Age";						//String
 	private static String Useremail = "Email";					//String
 	private static String Usergender = "Gender";				//String
 	private static String countforprompt = "Count for Prompt";  //Integer
 	private static String Latitude = "latitude";  				//Integer
 	private static String Longitude = "longitude";  			//Integer
 	private static String CurrentAddress = "Current Address";	//String
 	private static String ShowTutorial = "Show Tutorial";	    //String
 	
 	

     public AppPrefs(Context context)
     {
         this.appSharedPrefs = context.getSharedPreferences(settings, Activity.MODE_PRIVATE);
         this.prefsEditor = appSharedPrefs.edit();
     }
     
     public Boolean getShowTutorial() {
         return appSharedPrefs.getBoolean(ShowTutorial, false);
     }

     public void setShowTutorial(Boolean val) {
         prefsEditor.putBoolean(ShowTutorial, val).commit();
     }
     

     public Boolean getFirstLaunch() {
         return appSharedPrefs.getBoolean(FirstLaunch, true);
     }

     public void setFirstLaunch(boolean val) {
         prefsEditor.putBoolean(FirstLaunch, val).commit();
     }
     
     public Boolean getPromptFacebook() {
         return appSharedPrefs.getBoolean(PromptFacebook, true);
     }

     public void setPromptFacebook(boolean val) {
         prefsEditor.putBoolean(PromptFacebook, val).commit();
     }

     public Boolean getFBLogin() {
         return appSharedPrefs.getBoolean(FBLogin, false);
     }

     public void setFBLogin(boolean val) {
         prefsEditor.putBoolean(FBLogin, val).commit();
     }
     
     public String getUsername() {
         return appSharedPrefs.getString(Username, "not found");
     }

     public void setUsername(String val) {
         prefsEditor.putString(Username, val).commit();
     }
     
     public String getUserage() {
         return appSharedPrefs.getString(Userage, "not found");
     }

     public void setUserage(String val) {
         prefsEditor.putString(Userage, val).commit();
     }
     
     public String getUseremail() {
         return appSharedPrefs.getString(Useremail, "anonymous@anonymous.com");
     }

     public void setUseremail(String val) {
         prefsEditor.putString(Useremail, val).commit();
     }
     
     public String getUsergender() {
         return appSharedPrefs.getString(Usergender, "not found");
     }

     public void setUsergender(String val) {
         prefsEditor.putString(Usergender, val).commit();
     }
     
     
     public int getCountforPrompt() {
         return appSharedPrefs.getInt(countforprompt, -1);
     }

     public void setCountforPrompt(int val) {
         prefsEditor.putInt(countforprompt, val).commit();
     }
     
     public void resetCountforPrompt()
     {
    	 prefsEditor.putInt(countforprompt, 0).commit();
     }
     
     public void incCountforPrompt()
     {
    	 int val = appSharedPrefs.getInt(countforprompt, -1);
    	 prefsEditor.putInt(countforprompt, val+1).commit();
     }
     
     public int getLatitude() {
         return appSharedPrefs.getInt(Latitude, -1);
     }

     public void setLatitude(long val) {
         prefsEditor.putLong(Latitude, val).commit();
     }
     
     public int getLongitude() {
         return appSharedPrefs.getInt(Longitude, -1);
     }

     public void setLongitude(long val) {
         prefsEditor.putLong(Longitude, val).commit();
     }
     
     public String getCurrentAddress() {
         return appSharedPrefs.getString(CurrentAddress, "not found");
     }

     public void setCurrentAddress(String val) {
         prefsEditor.putString(CurrentAddress, val).commit();
     }
}