package com.example.stellarsurvival;

import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

public class RankingActivity extends Activity {
	
	final static public int ORDER_BY_DATE = 0;
	final static public int ORDER_BY_SCORE = 1;
	final static public int ORDER_BY_NAME = 2;
	
	private ListView listView;
	private Spinner spinner;
	private int mOrderBy;
	private MatchLog matchLog;
	
	String[] objects = { "Order By Date", "Order By Score", "Order By Name"};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_ranking);
		
		matchLog = new MatchLog(this);
		
		listView = (ListView) findViewById(R.id.list_matches);
		spinner = (Spinner) findViewById(R.id.spinner_order_by);
		spinner.setAdapter(new CustomAdapter(this, R.layout.custom_spinner,
				objects));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mOrderBy = position;
				getMatchesFromDB();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		listView.setAdapter(new MatchLogAdapter(this));
		mOrderBy = ORDER_BY_SCORE;
		getMatchesFromDB();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getMatchesFromDB();
	}
	
	public void getMatchesFromDB() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<MatchLogItem> list = matchLog.getMatchLog(mOrderBy);
				((MatchLogAdapter)listView.getAdapter()).clear();
				((MatchLogAdapter)listView.getAdapter()).setItems(list);
			}
		}).run();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_match, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
