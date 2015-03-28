package com.example.stellarsurvival;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button playButton, rankingButton, helpButton;
	private Typeface tf;
	
	String[] objects = { "VERTICAL MODE", "CENTRAL MODE", "RANDOM MODE", "WAVE MODE"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		playButton = (Button) findViewById(R.id.play_button);
		rankingButton = (Button) findViewById(R.id.ranking_button);
		helpButton = (Button) findViewById(R.id.help_button);
		
		tf = Typeface.createFromAsset(getAssets(), "fonts/Digital_tech.otf");
		playButton.setTypeface(tf);
		rankingButton.setTypeface(tf);
		helpButton.setTypeface(tf);
		
		playButton.setOnClickListener(this);
		rankingButton.setOnClickListener(this);
		helpButton.setOnClickListener(this);
	}
	
	@SuppressLint("InflateParams")
	private void displayModeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_mode, null);
		
        builder.setView(layout);
        final AlertDialog modeDialog = builder.create();
        modeDialog.setCancelable(true);
        modeDialog.setCanceledOnTouchOutside(true);
        
        ((TextView) layout.findViewById(R.id.modeDialogTitle)).setTypeface(tf);
        
        final Spinner modeSpinner = (Spinner) layout.findViewById(R.id.modeDialogSpinner);
        modeSpinner.setAdapter(new CustomAdapter(this, R.layout.custom_spinner,
				objects));
        
        ImageButton resume = (ImageButton) layout.findViewById(R.id.playMode);
        resume.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            	int selected = modeSpinner.getSelectedItemPosition();
            	intent.putExtra(GameActivity.MODE, selected);
            	startActivity(intent);
            	modeDialog.dismiss();
            }
        });

        ImageButton menu = (ImageButton) layout.findViewById(R.id.menuMode);
        menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	modeDialog.dismiss();
            }
        });

        modeDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		if (v.getId() == R.id.play_button) {
			//displayModeDialog();
			intent = new Intent(getApplicationContext(), ModeActivity.class);
			intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        	startActivity(intent);
		}
		else if (v.getId() == R.id.ranking_button){
			intent = new Intent(this, RankingActivity.class);
			startActivity(intent);
		}
		else {
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
		}
	}
}
