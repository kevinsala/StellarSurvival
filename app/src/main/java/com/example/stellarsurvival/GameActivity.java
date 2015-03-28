package com.example.stellarsurvival;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class GameActivity extends Activity implements OnTouchListener, OnKeyListener {
	
	/* Control constants */
	private static int EX_JOYSTICK_SIZE = 130;
	private static int PADDING_CONTROL = 40;
	private static int TURBO_BUTTON_SIZE= 100;
	private static int IN_JOYSTICK_SIZE = 80;
	
	/* Game constants */
	public static int NUM_ASTEROIDS_START;
	public static final int EXPLOSION_SIZE = 80;
	public static int HEART_SIZE = 30;
	public static int SCORE_TEXT_SIZE;
	public static final int LEFT_BORDER = 0;
	public static final int RIGHT_BORDER = 1;
	public static final int UP_BORDER = 2;
	public static final int DOWN_BORDER = 3;
	
	/* Game object constants */
	public static int SPACECRAFT_SIZE;
	public static int ASTEROID_SIZE;
	public static int LASER_WIDTH;
	public static int LASER_HEIGHT;
	public static int SPACECRAFT_SPEED_RATE;
	public static int ASTEROID_SPEED_RATE;
	public static int LASER_SPEED_RATE;
	
	/* Game Mode constants */
	public static final String MODE = "mode";
	public static final int MODE_ONE = 0;
	public static final int MODE_TWO = 1;
	public static final int MODE_THREE = 2;
	
	/* Other constants */
	public static final String WINNER_MSG = "Congratulations";
	public static final String LOSER_MSG = "Game Over";
	public static int GENERAL_PADDING = 10;
	
	
	public Bitmap asteroidBitmapBig;
	public Bitmap asteroidBitmapMed;
	public Bitmap asteroidBitmapSmall;
	public Bitmap laserBitmap;
	public Bitmap backgroundBitmap;
	public Bitmap explosionBitmap;
	public Bitmap heartBitmap;
	
	private boolean movement;
	private boolean playing;
	
	public int screenWidth;
	public int screenHeight;
	
	private AlertDialog pauseDialog;
	private AlertDialog finishDialog;
	
	public static Typeface tf;
	
	private FrameLayout mFrame;
	private ImageButton turboButton;
	private GameView gameView;
	
	/* Joystick attributes */
	private ImageView externalJoystickView;
	private ImageView internalJoystickView;
	private int joystickDefaultX;
	private int joystickDefaultY;
	private int joystickPointerID;
	
	private MatchLog matchLog;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game);
		
		initConstants();
		
		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);
		screenWidth = point.x;
		screenHeight = point.y;
		
		/* LOAD BITMAPS */
		int bitmapSize = ASTEROID_SIZE;
		Bitmap asteroidBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
		asteroidBitmapBig = Bitmap.createScaledBitmap(asteroidBitmap, bitmapSize, ASTEROID_SIZE, false);
		asteroidBitmapMed = Bitmap.createScaledBitmap(asteroidBitmap, ASTEROID_SIZE/Asteroid.MED_ASTEROID, 
				ASTEROID_SIZE/Asteroid.MED_ASTEROID, false);
		asteroidBitmapSmall = Bitmap.createScaledBitmap(asteroidBitmap, ASTEROID_SIZE/Asteroid.SMALL_ASTEROID, 
				ASTEROID_SIZE/Asteroid.SMALL_ASTEROID, false);
		laserBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
		laserBitmap = Bitmap.createScaledBitmap(laserBitmap, LASER_WIDTH, LASER_HEIGHT, false);
		explosionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
		explosionBitmap = Bitmap.createScaledBitmap(explosionBitmap, EXPLOSION_SIZE, EXPLOSION_SIZE, false);
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.space_background);
		backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, false);
		heartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
		heartBitmap = Bitmap.createScaledBitmap(heartBitmap, HEART_SIZE, HEART_SIZE, false);
		
		playing = false;
		movement = false;
		joystickPointerID = -1;
		
		tf = Typeface.createFromAsset(getAssets(), "fonts/Digital_tech.otf");
		
		matchLog = new MatchLog(this);
		
		mFrame = (FrameLayout) findViewById(R.id.frame);
		mFrame.setOnTouchListener(this);
		findViewById(R.id.settingsImageView).setOnTouchListener(this);
		
		int mode = this.getIntent().getIntExtra(MODE, MODE_ONE);
		
		initControls();
		initGame(mode);
		startGame();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (playing) pauseGame();
	}
	
	private void initConstants() {
		SPACECRAFT_SPEED_RATE = getResources().getInteger(R.integer.spacecraft_velocity);
		ASTEROID_SPEED_RATE = getResources().getInteger(R.integer.asteroid_velocity);
		LASER_SPEED_RATE = getResources().getInteger(R.integer.laser_velocity);
		SPACECRAFT_SIZE = getResources().getInteger(R.integer.spacecraft);
		ASTEROID_SIZE = getResources().getInteger(R.integer.asteroid);
		LASER_WIDTH = getResources().getInteger(R.integer.laser_width);
		LASER_HEIGHT = getResources().getInteger(R.integer.laser_height);
		HEART_SIZE = getResources().getInteger(R.integer.heart);
		SCORE_TEXT_SIZE = getResources().getInteger(R.integer.score);
		TURBO_BUTTON_SIZE = getResources().getInteger(R.integer.turbo);
		EX_JOYSTICK_SIZE = getResources().getInteger(R.integer.external_joystick);
		IN_JOYSTICK_SIZE = getResources().getInteger(R.integer.internal_joystick);
		PADDING_CONTROL = getResources().getInteger(R.integer.padding_control);
		GENERAL_PADDING = getResources().getInteger(R.integer.padding_general);
		NUM_ASTEROIDS_START = getResources().getInteger(R.integer.num_asteroids);
	}
	
	private void initControls() {
		/* Joystick configuration */
		externalJoystickView = (ImageView) findViewById(R.id.externalJoystickView);
		externalJoystickView.setLayoutParams(new RelativeLayout.LayoutParams(EX_JOYSTICK_SIZE, EX_JOYSTICK_SIZE));
		externalJoystickView.setX(PADDING_CONTROL);
		externalJoystickView.setY(screenHeight - EX_JOYSTICK_SIZE - PADDING_CONTROL);
		
		joystickDefaultX = PADDING_CONTROL + EX_JOYSTICK_SIZE/2 - IN_JOYSTICK_SIZE/2;
		joystickDefaultY = screenHeight - EX_JOYSTICK_SIZE/2 - IN_JOYSTICK_SIZE/2 - PADDING_CONTROL;
		
		internalJoystickView = (ImageView) findViewById(R.id.internalJoystickView);
		internalJoystickView.setLayoutParams(new RelativeLayout.LayoutParams(IN_JOYSTICK_SIZE, IN_JOYSTICK_SIZE));
		internalJoystickView.setX(joystickDefaultX);
		internalJoystickView.setY(joystickDefaultY);
		
		turboButton = (ImageButton) findViewById(R.id.turbo_button);
		turboButton.setOnTouchListener(this);
		turboButton.setLayoutParams(new RelativeLayout.LayoutParams(TURBO_BUTTON_SIZE, TURBO_BUTTON_SIZE));
		turboButton.setX(screenWidth - TURBO_BUTTON_SIZE - PADDING_CONTROL);
		turboButton.setY(screenHeight - TURBO_BUTTON_SIZE - PADDING_CONTROL);
		//turboButton.setTypeface(tf);
	}
	
	public void initGame(int mode) {
		gameView = new GameView(this, mode);
		mFrame.addView(gameView);
	}
	
	public void startGame() {
		playing = true;
	}
	
	public void pauseGame() {
		playing = false;
		displayPauseDialog();
	}
	
	public void resetGame() {
		playing = false;
		gameView.resetGame();
		playing = true;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	private boolean isInsideJoystick(float x, float y) {
		float xCenter = joystickDefaultX + IN_JOYSTICK_SIZE/2, yCenter = joystickDefaultY + IN_JOYSTICK_SIZE/2;
		return (Math.sqrt(Math.pow(Math.abs(xCenter - x),2) + Math.pow(Math.abs(yCenter - y),2)) <= EX_JOYSTICK_SIZE/2);
	}
	/*
	private boolean isInsideTurbo(float x, float y) {
		float xCenter = screenWidth - PADDING_CONTROL - TURBO_BUTTON_SIZE/2, yCenter = screenHeight - PADDING_CONTROL - TURBO_BUTTON_SIZE/2;
		return (Math.sqrt(Math.pow(Math.abs(xCenter - x),2) + Math.pow(Math.abs(yCenter - y),2)) <= TURBO_BUTTON_WIDTH/2);
	}*/
	
	private double getDegree(float px, float py) {
		float radius = EX_JOYSTICK_SIZE/2;
		float xCentre = PADDING_CONTROL + radius;
		float yCentre = screenHeight - PADDING_CONTROL - radius;
		float dx = Math.abs(xCentre - px);
		float dy = Math.abs(yCentre - py);
		
		double degree = Math.atan(dy/dx);
		if (px < xCentre && py <= yCentre) return Math.PI - degree;
		else if (px < xCentre && py > yCentre) return Math.PI + degree;
		else if (px >= xCentre && py > yCentre) return 2*Math.PI - degree;
		else return degree;
	}
	
	private PointF getIntersectionPoint(float px, float py) {
		float radius = EX_JOYSTICK_SIZE/2;
		float xCentre = PADDING_CONTROL + radius;
		float yCentre = screenHeight - PADDING_CONTROL - radius;
		
		PointF point = new PointF();
		double degree = getDegree(px, py);
		point.x = xCentre + ((float) Math.cos(degree) * radius);
		point.y = yCentre - ((float) Math.sin(degree) * radius);
		return point;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getActionMasked();
		int viewID = v.getId();
		int index = event.getActionIndex();
		int indexJoystick = event.findPointerIndex(joystickPointerID);
		boolean isInside = isInsideJoystick(event.getX(index),event.getY(index));
		boolean isInside2 = isInsideJoystick(event.getX(),event.getY());
		//boolean turboSelected = isInsideTurbo(event.getX(index),event.getY(index));
		Spacecraft spacecraft = gameView.getSpacecraft();
		
		if (action == MotionEvent.ACTION_DOWN) {
			if (isInside2) {
				// 1
				joystickPointerID = event.getPointerId(0);
				internalJoystickView.setX(event.getX(index) - IN_JOYSTICK_SIZE/2);
				internalJoystickView.setY(event.getY(index) - IN_JOYSTICK_SIZE/2);
				spacecraft.updateRotation((float)Math.toDegrees(getDegree(event.getX(index), event.getY(index))));
				movement = true;
			}
			else if (viewID == R.id.turbo_button) {
				// 2
				float degree = (float) Math.toRadians((double)spacecraft.getRotation());
				spacecraft.setVelocity((float)Math.cos(degree)*4, (float)-Math.sin(degree)*4);
				if (!spacecraft.isTurboEnabled()) spacecraft.changeTurbo();
			}
			else if (viewID == R.id.settingsImageView) {
				// 3
				if (playing) pauseGame();
			}
			else {
				// 4
				gameView.newLaser();
			}
		}
		else if (action == MotionEvent.ACTION_POINTER_DOWN) {
			if (isInside) {
				// 1
				joystickPointerID = event.getPointerId(index);
				internalJoystickView.setX(event.getX(index) - IN_JOYSTICK_SIZE/2);
				internalJoystickView.setY(event.getY(index) - IN_JOYSTICK_SIZE/2);
				spacecraft.updateRotation((float)Math.toDegrees(getDegree(event.getX(index), event.getY(index))));
				movement = true;
			}
			else if (viewID == R.id.turbo_button) {
				// 2
				float degree = (float) Math.toRadians((double)spacecraft.getRotation());
				spacecraft.setVelocity((float)Math.cos(degree)*4, (float)-Math.sin(degree)*4);
				if (!spacecraft.isTurboEnabled()) spacecraft.changeTurbo();
			}
			else if (viewID == R.id.settingsImageView) {
				// 3
				if (playing) pauseGame();
			}
			else {
				// 4
				gameView.newLaser();
			}
		}
		else if (action == MotionEvent.ACTION_UP && event.getPointerId(index) == joystickPointerID) {
			// 5
			internalJoystickView.setX(joystickDefaultX);				
			internalJoystickView.setY(joystickDefaultY);
			movement = false;
			joystickPointerID = -1;
		}
		else if (action == MotionEvent.ACTION_POINTER_UP && event.getPointerId(index) == joystickPointerID) {
			internalJoystickView.setX(joystickDefaultX);				
			internalJoystickView.setY(joystickDefaultY);
			movement = false;
			joystickPointerID = -1;
		}
		else if (action == MotionEvent.ACTION_MOVE && movement && indexJoystick != -1) {
			if (isInside) {
				internalJoystickView.setX(event.getX(indexJoystick) - IN_JOYSTICK_SIZE/2);
				internalJoystickView.setY(event.getY(indexJoystick) - IN_JOYSTICK_SIZE/2);
				spacecraft.updateRotation((float)Math.toDegrees(getDegree(event.getX(indexJoystick), event.getY(indexJoystick))));
			}
			else {
				PointF pointF = getIntersectionPoint(event.getX(indexJoystick), event.getY(indexJoystick));
				internalJoystickView.setX(pointF.x - IN_JOYSTICK_SIZE/2);
				internalJoystickView.setY(pointF.y - IN_JOYSTICK_SIZE/2);
				spacecraft.updateRotation((float)Math.toDegrees(getDegree(pointF.x, pointF.y)));
			}
		}
		return true;
	}
	
	void finishWinGame(int score) {
		playing = false;
		displayFinishWinDialog(score);
	}
	
	void finishFailGame() {
		playing = false;
		displayFinishFailedDialog();
	}
	
	private void displayPauseDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_pause, null);
		
        builder.setView(layout);
        pauseDialog = builder.create();
        pauseDialog.setCancelable(false);
        
        ImageButton resume = (ImageButton) layout.findViewById(R.id.resumeButton);
        resume.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	startGame();
            	pauseDialog.dismiss();
            	pauseDialog = null;
            }
        });

        ImageButton menu = (ImageButton) layout.findViewById(R.id.menuButton);
        menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	pauseDialog.cancel();
            	pauseDialog = null;
                finish();
            }
        });
        
        ImageButton restart = (ImageButton) layout.findViewById(R.id.restartButton);
        restart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	pauseDialog.cancel();
            	pauseDialog = null;
            	resetGame();	
            }
        });

        pauseDialog.setOnKeyListener(this);

        ((TextView) layout.findViewById(R.id.pausedDialogTitle)).setTypeface(tf);

        pauseDialog.show();
	}
	
	private void displayFinishWinDialog(final int score) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_finished_game, null);
		
        builder.setView(layout);
        finishDialog = builder.create();
        finishDialog.setCancelable(false);
        
        final String timestamp = (DateFormat.format("dd-MM-yyyy kk:mm:ss", new java.util.Date()).toString());
        
        final EditText nameEdit = (EditText) layout.findViewById(R.id.nameMatchEdit);
        TextView titleView = (TextView) layout.findViewById(R.id.titleFinishedDialog);
        TextView scoreView = (TextView) layout.findViewById(R.id.scoreFinishedDialog);
        titleView.setText(WINNER_MSG);
        scoreView.setText("SCORE " + score);
        nameEdit.setTypeface(tf);
        titleView.setTypeface(tf);
        scoreView.setTypeface(tf);
        
        Button newGame = (Button) layout.findViewById(R.id.playNewFinished);
        newGame.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finishDialog.cancel();
            	finishDialog = null;
            	resetGame();	
            }
        });

        Button menu = (Button) layout.findViewById(R.id.menuFinished);
        menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finishDialog.cancel();
            	finishDialog = null;
                finish();
            }
        });
        
        final ImageButton save = (ImageButton) layout.findViewById(R.id.saveMatchButton);
        save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (toast != null) toast.cancel();
            	String name = nameEdit.getText().toString().trim();
	            if (!name.equals("")) {
	            	if (matchLog.addMatch(name, score, timestamp)) {
	            		save.setEnabled(false);
	            		nameEdit.setEnabled(false);
	            		save.setImageResource(R.drawable.icon_save_disable);
	            		toast = Toast.makeText(getApplicationContext(), "Match saved", Toast.LENGTH_SHORT);
	            	}
	            	else toast = Toast.makeText(getApplicationContext(), "This name already exists", Toast.LENGTH_SHORT);
	            }
	            else toast = Toast.makeText(getApplicationContext(), "The name field can't be empty", Toast.LENGTH_SHORT);
	            toast.show();
            }
        });
        
        finishDialog.setOnKeyListener(this);
        
        menu.setTypeface(tf);
        newGame.setTypeface(tf);

        finishDialog.show();
	}
	
	private void displayFinishFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_finished_game_fail, null);
		
        builder.setView(layout);
        finishDialog = builder.create();
        finishDialog.setCancelable(false);

        TextView titleView = (TextView) layout.findViewById(R.id.titleFinishedFailDialog);
        titleView.setText(LOSER_MSG);
        titleView.setTypeface(tf);
        
        Button newGame = (Button) layout.findViewById(R.id.playNewFinishedFail);
        newGame.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finishDialog.cancel();
            	finishDialog = null;
            	resetGame();	
            }
        });

        Button menu = (Button) layout.findViewById(R.id.menuFinishedFail);
        menu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finishDialog.cancel();
            	finishDialog = null;
                finish();
            }
        });
        
        finishDialog.setOnKeyListener(this);
        
        menu.setTypeface(tf);
        newGame.setTypeface(tf);

        finishDialog.show();
	}

	
	@Override
	public void onBackPressed() {
		pauseGame();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
			
		case R.id.action_match_log:
			startActivity(new Intent(this, RankingActivity.class));
			break;
		}
			
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (finishDialog != null) finishDialog.cancel();
        	else if (pauseDialog != null) pauseDialog.cancel();
            finish();
            return true;
        }
		return false;
	}
}