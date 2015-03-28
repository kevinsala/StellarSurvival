package com.example.stellarsurvival;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ModeActivity extends Activity implements OnClickListener, OnItemClickListener {
	
	private ListView modeList;
	private Button playButton;
	private ArrayAdapter<String> adapter;
	private Typeface tf;
	private int mode = -1;
	
	String [] objects = { "VERTICAL MODE", "CENTRAL MODE", "RANDOM MODE"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_mode);
		
		playButton = (Button) findViewById(R.id.modePlayButton);
		modeList = (ListView) findViewById(R.id.modeListView);
		adapter = new ArrayAdapter<String>(this,
	              R.layout.custom_spinner, R.id.text_main_seen, objects);
		modeList.setAdapter(adapter);
		tf = Typeface.createFromAsset(getAssets(), "fonts/Digital_tech.otf");
		playButton.setTypeface(tf);
		playButton.setOnClickListener(this);
		playButton.setEnabled(false);
		playButton.setTextColor(getResources().getColor(R.color.white_trans));
		modeList.setOnItemClickListener(this);
		modeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		((TextView) findViewById(R.id.modeTextView)).setTypeface(tf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mode, menu);
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

	@Override
	public void onClick(View v) {
		if (mode != -1 && v.getId() == R.id.modePlayButton) {
			Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        	intent.putExtra(GameActivity.MODE, mode);
        	startActivity(intent);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mode == -1) {
			playButton.setEnabled(true);
			playButton.setTextColor(getResources().getColor(R.color.orange));
		}
		mode = position;
	}
}
