package com.example.stellarsurvival;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MatchLogDBOpenHelper extends SQLiteOpenHelper {

	final private static String CREATE_SENTENCE =

	"CREATE TABLE IF NOT EXISTS " + Contract.TABLE_NAME + " (" + Contract._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Contract.MATCH_NAME + " TEXT NOT NULL UNIQUE, " 
			+ Contract.MATCH_SCORE + " INTEGER NOT NULL, "
			+ Contract.MATCH_DATE + " TEXT NOT NULL)";

	final private static String NAME = "matchlog_db";
	final private static Integer VERSION = 1;
	final private Context mContext;

	public MatchLogDBOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SENTENCE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public boolean deleteDatabase() {
		return mContext.deleteDatabase(NAME);
	}
}
