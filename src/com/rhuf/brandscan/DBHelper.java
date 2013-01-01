package com.rhuf.brandscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rhuf.brandscan.TenSearches;

public class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context) {
		super(context,TenSearches.DATABASE_NAME,null,TenSearches.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " +TenSearches.DATABASE_TABLE +" ("+
				TenSearches.KEY_ROWID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				TenSearches.KEY_UPC +" TEXT NOT NULL, " +
				TenSearches.KEY_TITLE + " TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TenSearches.DATABASE_TABLE);
		onCreate(db);
	}

}
