package com.example.stellarsurvival;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements
			SurfaceHolder.Callback, Runnable {

	public GameActivity activity;
	public static int drawDelay;
	
	private final SurfaceHolder surfaceHolder;
	private final Paint painter = new Paint();
	private Thread drawingThread;
	
	private Spacecraft spacecraft;
	private ArrayList<Asteroid> asteroidArray;
	private List<Laser> laserArray;
	
	//private int explosionCount;
	private int lifes;
	private int score;
	private int mode;
	
	private Random random = new Random();

	public GameView(Context context, int mode) {
		super(context);
		surfaceHolder = getHolder();
		initView(context, mode);
	}
	
	public GameView(Context context, AttributeSet attrs, int mode) {
		super(context, attrs);
		surfaceHolder = getHolder();
		initView(context, mode);
	}
		 
	public GameView(Context context, AttributeSet attrs, int defStyle, int mode) {
		super(context, attrs, defStyle);
		surfaceHolder = getHolder();
		initView(context, mode);
	}
	
	private void initView(Context context, int mode) {
		activity = (GameActivity) context;
		painter.setAntiAlias(true);
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
		painter.setTypeface(GameActivity.tf);
		painter.setTextSize(GameActivity.SCORE_TEXT_SIZE);
		int myColor =
			    context.getResources().getColor(R.color.orange);
		painter.setColor(myColor);
		drawDelay = getContext().getResources().getInteger(R.integer.draw_delay);
		Log.d("StellarSurvival", "drawdelay = " + drawDelay);
		this.mode = mode;
		initGame();
		//explosionCount = 0;
		lifes = 3;
		score = 0;
	}
	
	public void resetGame() {
		lifes = 3;
		score = 0;
		initGame();
	}
	
	private void initGame() {
		spacecraft = new Spacecraft(this, activity.getScreenWidth()/2, activity.getScreenHeight()/2);
		asteroidArray = new ArrayList<Asteroid>();
		newAsteroids(GameActivity.NUM_ASTEROIDS_START);
		laserArray = Collections.synchronizedList(new ArrayList<Laser>());
	}
	
	public GameActivity getGameActivity() {
		return activity;
	}
	
	private void refreshGame() {
		synchronized(laserArray) {
			boolean deleted;
			for (int i = 0; i < laserArray.size(); ++i) {
				if (laserArray.get(i).nextMovement()) {
					deleted = false;
					for (int j = 0; !deleted && j < asteroidArray.size(); ++j) {
						if (laserArray.get(i).intersects(asteroidArray.get(j).getPosition().x,
								asteroidArray.get(j).getPosition().y, GameActivity.ASTEROID_SIZE/asteroidArray.get(j).getSize())) {
							score += 10;
							laserArray.remove(i);
							divideAsteroid(j);
							asteroidArray.remove(j);
							deleted = true;
						}
					}
				}
				else laserArray.remove(i);
			}
		}
		
		if (asteroidArray.size() < GameActivity.NUM_ASTEROIDS_START) {
			newAsteroids(GameActivity.NUM_ASTEROIDS_START - asteroidArray.size());
		}
		
		for (Asteroid asteroid : asteroidArray)
			asteroid.nextMovement();
		
		if (!spacecraft.nextMovement()) {
			--lifes;
			
			if (lifes == -1) {
				if (score == 0) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.finishFailGame();
						}});
				} else {
					final int finalScore = score;
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.finishWinGame(finalScore);
						}});
				}
			} else initGame();
			
		}
		/*else {
			if (explosionCount != 0) explosionCount = 10;
			spacecraft.draw(canvas); // RESET GAME
			drawExplosion(canvas, spacecraft.getPosition().x, spacecraft.getPosition().y);
		}*/
		
	}

	private void drawGame(Canvas canvas) {
		
		//canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		canvas.drawBitmap(activity.backgroundBitmap, 0, 0, painter);
		
		
		synchronized(laserArray) {
			for (int i = 0; i < laserArray.size(); ++i) {
				laserArray.get(i).draw(canvas);
			}
		}
		
		for (Asteroid asteroid : asteroidArray) {
			asteroid.draw(canvas);
		}
		
		spacecraft.draw(canvas);
		
		drawHUB(canvas);
	}
	
	private void drawHUB(Canvas canvas) {
		int x = GameActivity.GENERAL_PADDING;
		for (int i = 0; i < lifes; i++) {
			canvas.drawBitmap(activity.heartBitmap, x, GameActivity.GENERAL_PADDING, painter);
			x += GameActivity.GENERAL_PADDING + GameActivity.HEART_SIZE;
		}
		canvas.drawText("SCORE " + score, activity.screenWidth/2 - GameActivity.GENERAL_PADDING*5, GameActivity.GENERAL_PADDING + GameActivity.HEART_SIZE, painter);
	}
	
	public boolean nextMovementSpacecraft() {
		PointF pointF = spacecraft.getPosition();
		if (spacecraft.isOutsideScreen()) return false;
		for (Asteroid asteroid : asteroidArray) {
			if (asteroid.intersects(pointF.x, pointF.y,(float)GameActivity.SPACECRAFT_SIZE/2))
				return false;
		}
		return true;
	}
	
	/* If oldPoint == null, the asteroid is new, otherwise the asteroid is reused */
	public PointF getAsteroidOrigin(int size, PointF oldPoint, int border) {
		PointF point = new PointF();
		if (oldPoint != null) {
			switch(border) {
				case GameActivity.LEFT_BORDER:
					point.x = activity.screenWidth;
					point.y = oldPoint.y;
					break;
					
				case GameActivity.RIGHT_BORDER:
					point.x = -(GameActivity.ASTEROID_SIZE/size)/2;
					point.y = oldPoint.y;
					break;
					
				case GameActivity.UP_BORDER:
					point.x = oldPoint.x;
					point.y = activity.screenHeight;
					break;
					
				case GameActivity.DOWN_BORDER:
					point.x = oldPoint.x;
					point.y = -(GameActivity.ASTEROID_SIZE/size)/2;
					break;
			}
		}
		else if (mode == GameActivity.MODE_ONE) {
			/* Vertical mode */
			point.x = random.nextInt(activity.screenWidth);
			point.y = -(GameActivity.ASTEROID_SIZE/size)/2;
		}
		else if (mode == GameActivity.MODE_TWO || mode == GameActivity.MODE_THREE) {
			/* Center mode */
			switch(border) {
				case GameActivity.LEFT_BORDER:
					point.x = -(GameActivity.ASTEROID_SIZE/size)/2;
					point.y = random.nextInt(activity.screenHeight - GameActivity.ASTEROID_SIZE/size) + (GameActivity.ASTEROID_SIZE/size)/2;
					break;
					
				case GameActivity.RIGHT_BORDER:
					point.x = activity.screenWidth + (GameActivity.ASTEROID_SIZE/size)/2;
					point.y = random.nextInt(activity.screenHeight - GameActivity.ASTEROID_SIZE/size) + (GameActivity.ASTEROID_SIZE/size)/2;
					break;
					
				case GameActivity.UP_BORDER:
					point.x = random.nextInt(activity.screenWidth - GameActivity.ASTEROID_SIZE/size) + (GameActivity.ASTEROID_SIZE/size)/2;
					point.y = -(GameActivity.ASTEROID_SIZE/size)/2;
					break;
					
				case GameActivity.DOWN_BORDER:
					point.x = random.nextInt(activity.screenWidth - GameActivity.ASTEROID_SIZE/size) + (GameActivity.ASTEROID_SIZE/size)/2;
					point.y = activity.screenHeight + (GameActivity.ASTEROID_SIZE/size)/2;
					break;
			}
		}
		return point;
	}
	
	public PointF getAsteroidVelocity(PointF origin, int border) {
		PointF point = new PointF();
		if (mode == GameActivity.MODE_ONE) {
			//Vertical mode
			point.x = 0;
			point.y = random.nextInt(2) +1;
		}
		else if (mode == GameActivity.MODE_TWO) {
			float dx = activity.screenWidth/2 - origin.x; 
			float dy = activity.screenHeight/2 - origin.y;
			float module = (float) Math.sqrt(dx*dx + dy*dy);
			point.x = (float) dx/module*1.5F;
			point.y = (float) dy/module*1.5F;
		}
		else if (mode == GameActivity.MODE_THREE) {
			switch(border) {
				case GameActivity.LEFT_BORDER:
					point.x = random.nextInt(2) +1;
					point.y = random.nextInt(5) -2;
					break;
					
				case GameActivity.RIGHT_BORDER:
					point.x = random.nextInt(2) -2;
					point.y = random.nextInt(5) -2;
					break;
					
				case GameActivity.UP_BORDER:
					point.y = random.nextInt(5) -2;
					point.x = random.nextInt(2) +1;
					break;
					
				case GameActivity.DOWN_BORDER:
					point.y = random.nextInt(5) -2;
					point.x = random.nextInt(2) -2;
					break;
			}
		}
		return point;
	}
	
	public synchronized Spacecraft getSpacecraft() {
		return spacecraft;
	}
	
	public Paint getPainter() {
		return painter;
	}
	
	public void newLaser() {
		PointF posSpacecraft = spacecraft.getPosition();
		final Laser laser = new Laser(this, posSpacecraft.x, posSpacecraft.y, 0, -10);
		float degree = spacecraft.getRotation();
		laser.setRotation(degree);
		degree = (float) Math.toRadians((double)degree);
		laser.setVelocity((float)Math.cos(degree)*10, (float)-Math.sin(degree)*10);
		synchronized(laserArray) {
			laserArray.add(laser);
		}
	}
	
	public void divideAsteroid(int i) {
		Asteroid parent = asteroidArray.get(i);
		float x = parent.getPosition().x, y = parent.getPosition().y;
		float xVel = 
				parent.getVelocityX(), yVel = parent.getVelocityY();
		switch(parent.getSize()) {
			case Asteroid.MED_ASTEROID:
				Asteroid small1 = new Asteroid(this, x, y, -xVel, yVel+1, Asteroid.SMALL_ASTEROID);
				Asteroid small2 = new Asteroid(this, x, y, xVel+2, yVel, Asteroid.SMALL_ASTEROID);
				asteroidArray.add(small1);
				asteroidArray.add(small2);
				break;
				
			case Asteroid.BIG_ASTEROID:
				Asteroid med1 = new Asteroid(this, x, y, -xVel, yVel+1, Asteroid.MED_ASTEROID);
				Asteroid med2 = new Asteroid(this, x, y, xVel+2, -yVel, Asteroid.MED_ASTEROID);
				Asteroid med3 = new Asteroid(this, x, y, -yVel, -yVel, Asteroid.MED_ASTEROID);
				Asteroid med4 = new Asteroid(this, x, y, yVel, yVel, Asteroid.MED_ASTEROID);
				asteroidArray.add(med1);
				asteroidArray.add(med2);
				asteroidArray.add(med3);
				asteroidArray.add(med4);
				break;
		}
	}
	
	private void newAsteroids(int N) {
		for (int i = 0; i < N; ++i) {
			int border = -1;
			if (mode != GameActivity.MODE_ONE) border = random.nextInt(4);
			PointF origin = getAsteroidOrigin(Asteroid.BIG_ASTEROID, null, border);
			PointF vel = getAsteroidVelocity(origin, border);
			Asteroid asteroidView = new Asteroid(this, origin.x, origin.y, vel.x, vel.y, Asteroid.BIG_ASTEROID);
			asteroidArray.add(asteroidView);
		}
	}
	
	@SuppressWarnings("unused")
	private void drawExplosion(Canvas canvas, float x, float y) {
		canvas.drawBitmap(activity.explosionBitmap, x - GameActivity.EXPLOSION_SIZE/2, y - GameActivity.EXPLOSION_SIZE/2, painter);
	}
	
	private void sleep(long initial) {
		long delay = drawDelay - (SystemClock.elapsedRealtime() - initial);
		if (delay > 0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawingThread = new Thread(this);
		drawingThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (null != drawingThread)
			drawingThread.interrupt();
	}

	@Override
	public void run() {
		Canvas canvas = null;
		while (!Thread.currentThread().isInterrupted()) {
			if (activity.isPlaying()) {
				long ini = SystemClock.elapsedRealtime();
				refreshGame();
				while ((canvas = surfaceHolder.lockCanvas()) == null && !Thread.currentThread().isInterrupted());
				if (null != canvas) {
					drawGame(canvas);
					sleep(ini);
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}	
	}
}