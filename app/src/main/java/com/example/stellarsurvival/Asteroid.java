package com.example.stellarsurvival;

import android.graphics.Canvas;
import android.graphics.PointF;


public class Asteroid {
	public static final int BIG_ASTEROID = 1;
	public static final int MED_ASTEROID = 2;
	public static final int SMALL_ASTEROID = 3;
	
	/* Parent surface */
	private final GameView parentView;
	
	/* Attributes */
	private float mXPos, mYPos, mXVelocity, mYVelocity;
	private int mBitmapSize;
	private long mRotate;
	int mSize;

	public Asteroid(GameView parent, float x, float y, float xVel, float yVel, int size) {
		
		parentView = parent;
		mSize = size;
		mBitmapSize = GameActivity.ASTEROID_SIZE/size;
		mXPos = x - mBitmapSize/2;
		mYPos = y - mBitmapSize/2;
		mXVelocity = xVel*GameActivity.ASTEROID_SPEED_RATE;
		mYVelocity = yVel*GameActivity.ASTEROID_SPEED_RATE;
		mRotate = 0;
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate(mRotate, mXPos + mBitmapSize/2, mYPos + mBitmapSize/2);
		if (mSize == BIG_ASTEROID) canvas.drawBitmap(parentView.getGameActivity().asteroidBitmapBig, mXPos, mYPos, parentView.getPainter());
		else if (mSize == MED_ASTEROID) canvas.drawBitmap(parentView.getGameActivity().asteroidBitmapMed, mXPos, mYPos, parentView.getPainter());
		else canvas.drawBitmap(parentView.getGameActivity().asteroidBitmapSmall, mXPos, mYPos, parentView.getPainter());
		canvas.restore();
	}
	
	public boolean nextMovement() {
		mRotate += 2;
		mXPos += mXVelocity;
		mYPos += mYVelocity;
		int out = isOutsideScreen();
		if (out >= 0) {
			PointF p = parentView.getAsteroidOrigin(mSize, new PointF(mXPos,mYPos), out);
			mXPos = p.x- mBitmapSize/2;
			mYPos = p.y - mBitmapSize/2;
		}
		return true;
	}
	
	public synchronized void setVelocity(float xVel, float yVel) {
		mXVelocity = xVel;
		mYVelocity = yVel;
	}
	
	public synchronized PointF getPosition() {
		return new PointF(mXPos + (GameActivity.ASTEROID_SIZE/mSize)/2, mYPos + (GameActivity.ASTEROID_SIZE/mSize)/2);
	}
	
	public synchronized float getVelocityX() {
		return mXVelocity;
	}
	
	public synchronized float getVelocityY() {
		return mYVelocity;
	}
	
	public synchronized int getSize() {
		return mSize;
	}
	
	public synchronized boolean intersects(float x, float y, float radius) {
		float dx = Math.abs(x - (mXPos + (float)mBitmapSize/2));
		float dy = Math.abs(y - (mYPos + (float)mBitmapSize/2));
		return (Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2)) <=  (float)mBitmapSize/2 + radius -5);
	}
	
	private int isOutsideScreen() {
		int out = -1;
		if (mXPos <= -mBitmapSize) out = GameActivity.LEFT_BORDER;
		if (mXPos >= parentView.getGameActivity().screenWidth) 
			out = GameActivity.RIGHT_BORDER;
		if (mYPos <= -mBitmapSize) out = GameActivity.UP_BORDER;
		if (mYPos >= parentView.getGameActivity().screenHeight) out = GameActivity.DOWN_BORDER;
		/*return (mXPos <= -mBitmapSize || mXPos >= parentView.getGameActivity().screenWidth + mBitmapSize || mYPos <= -mBitmapSize
				|| mYPos >= parentView.getGameActivity().screenHeight);*/
		return out;
	}
}
