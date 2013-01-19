package com.rhuf.brandscan;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rhuf.brandscan.webview.WebViewActivity;


public class History extends ListActivity {
	 

		String [] Names;
		String [] UPC;
		int iterator = 0;
		int DBiterator = 0;
		TenSearches DB;
		AppPrefs appPrefs;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			appPrefs = new AppPrefs(getApplicationContext());
			
			DB = new TenSearches(this);
			//Product [] temp;
			ArrayList<Product> temp;
			DB.open();
			temp = DB.getDataUPC();
			DB.close();
			//Product[] history = new Product[temp.length];
			DBiterator = temp.size()-1;
			Log.i("INFORMATION",Integer.toString(DBiterator));
			if(temp.size()>=10){
				Names = new String[10];
				UPC = new String[10];
				}
			else
			{
				Names = new String[temp.size()];
				UPC = new String[temp.size()];
			}

			
			while(iterator < 10 && DBiterator >=0)
			{
				try{
				Names[iterator]=temp.get(DBiterator).getProductName();
				UPC[iterator]=temp.get(DBiterator).getProductCode();
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
				iterator++;
				DBiterator--;
			}
			
			setListAdapter(new ArrayAdapter<String>(this, R.layout.history,R.id.ProductName,Names));
	 
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);
	 
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked, show a toast with the TextView text
					Intent i = new Intent(getBaseContext(), WebViewActivity.class);
					i.putExtra("URL", Variables.PROD_URL + UPC[position]+"/email/"+appPrefs.getUseremail());
	    	        startActivity(i);
				    //launch a webview for the clicked product
				}
			});
	 
		}
}