package com.example.stellarsurvival;

public class MatchLogItem {
	private String mName;
	private Integer mScore;
	private String mDate;
	
	public MatchLogItem() {
		
	}
	
	public MatchLogItem(String name, Integer score, String date) {
		mName = name;
		mScore = score;
		mDate = date; 
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public Integer getScore() {
		return mScore;
	}
	
	public void setScore(Integer score) {
		mScore = score;
	}
	
	public String getDate() {
		return mDate.substring(0, mDate.length()-3);
	}
	
	public void setDate(String date) {
		mDate = date;
	}
}