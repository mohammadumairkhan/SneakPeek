package com.rhuf.brandscan;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class TenSearches {
	public static final String KEY_ROWID="ID";
	public static final String KEY_UPC="upccode";
	public static final String KEY_TITLE="title";
	
	public static final String DATABASE_NAME="BrandscanDB";
	public static final String DATABASE_TABLE="ScanTable";
	public static final int DATABASE_VERSION=2;
	
	private DBHelper helper;
	private final Context context;
	private SQLiteDatabase BrandScanDB;
	
	public TenSearches(Context c)
	{
		context = c;
	}
	
	public TenSearches open() throws SQLException
	{
		helper = new DBHelper(context);
		BrandScanDB = helper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		helper.close();
	}
	
	public long createEntry(String upc, String title)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_UPC, upc);
		cv.put(KEY_TITLE, title);
		Log.e("Database Entry",title+" "+ upc);
		return BrandScanDB.insert(DATABASE_TABLE, null, cv);
	}
	
	public ArrayList<Product> getDataUPC()
	{
		String columns[]=new String[]{KEY_ROWID,KEY_UPC,KEY_TITLE};
		Cursor c = BrandScanDB.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ROWID);
		String result;
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iUPC = c.getColumnIndex(KEY_UPC);
		int iTitle = c.getColumnIndex(KEY_TITLE);

		//Make a listArray or Object Array from the Database entries
		//Product temp[]=new Product[10];
		ArrayList<Product> temp = new ArrayList<Product>();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
			{
				try{
				Log.e("Database Fetch",c.getString(iTitle)+" "+ c.getString(iUPC));
				temp.add(new Product(c.getString(iTitle),c.getString(iUPC)));
				}catch(Exception e)
				{
					Log.e("Database Fetch",e.toString());
				}
				
			}
			
		return temp;
	}
}
