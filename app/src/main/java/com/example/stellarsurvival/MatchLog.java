package com.example.stellarsurvival;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MatchLog {
	
	private Context mContext;
	private MatchLogDBOpenHelper mDBHelper;
	
	public MatchLog(Context context) {
		mContext = context;
		mDBHelper = new MatchLogDBOpenHelper(mContext);
	}
	
	public boolean addMatch(String name, Integer score, String timestamp) {
		ContentValues values = new ContentValues();

		values.put(Contract.MATCH_NAME, name);
		values.put(Contract.MATCH_SCORE, score);
		values.put(Contract.MATCH_DATE, timestamp);
		
		long insert = mDBHelper.getWritableDatabase().insert(Contract.TABLE_NAME, 
				null, values);
		
		if (insert == -1) {
			values.clear();
			values.put(Contract.MATCH_SCORE, score);
			values.put(Contract.MATCH_DATE, timestamp);
			String [] args = {name, score.toString()};
			String where = Contract.MATCH_NAME + " = ? AND " + Contract.MATCH_SCORE + " < ?";
			mDBHelper.getWritableDatabase().update(Contract.TABLE_NAME, values, where, args);
		}

		
		return true;
	}
	
	public boolean updateMatchName(String oldName, String newName) {
		ContentValues data = new ContentValues();                          
		data.put(Contract.MATCH_NAME, newName);
		String where = Contract.MATCH_NAME + "=?";
		String[] whereArgs = new String[] {oldName};
		return mDBHelper.getWritableDatabase().update(Contract.TABLE_NAME, 
				data, where, whereArgs) == 1;
	}
	
	public List<MatchLogItem> getMatchLog(Integer orderBy) {
		String order;
		if (orderBy == RankingActivity.ORDER_BY_NAME) order = Contract.MATCH_NAME + " ASC";
		else if (orderBy == RankingActivity.ORDER_BY_SCORE) order = Contract.MATCH_SCORE + " DESC";
		else order = Contract.MATCH_DATE + " DESC";
					
		Cursor c = mDBHelper.getReadableDatabase().query(Contract.TABLE_NAME,
				Contract.COLUMNS, null, new String[] {}, null, null, order);
		
		List<MatchLogItem> list = new ArrayList<MatchLogItem>();
		while (c.moveToNext()) {
			MatchLogItem item = new MatchLogItem();
		    item.setName(c.getString(c.getColumnIndex(Contract.MATCH_NAME)));
		    item.setScore(c.getInt(c.getColumnIndex(Contract.MATCH_SCORE)));
		    item.setDate(c.getString(c.getColumnIndex(Contract.MATCH_DATE)));
		    list.add(item);
		}
		return list;
	}
	
	public void deleteMatchLog() {
		mDBHelper.getWritableDatabase().delete(Contract.TABLE_NAME,
				null, null);
	}
}
