package com.example.stellarsurvival;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

public class Spacecraft {
	
	public static final int MARGIN = 10;
	
	/* Parent surface */
	private final GameView parentView;
	
	/* Spacecraft bitmap */
	private Bitmap mBitmap;
	
	/* Attributes */
	private float mXPos, mYPos, mXVelocity, mYVelocity, mRotate;
	private boolean turbo;

	public Spacecraft(GameView parent, float x, float y) {
		
		parentView = parent;
		mBitmap = BitmapFactory.decodeResource(parentView.getResources(), R.drawable.spacecraft_paused);
		mBitmap = Bitmap.createScaledBitmap(mBitmap, GameActivity.SPACECRAFT_SIZE, GameActivity.SPACECRAFT_SIZE, false);
		mXPos = x - GameActivity.SPACECRAFT_SIZE/2;
		mYPos = y - GameActivity.SPACECRAFT_SIZE/2;
		mXVelocity = 0;
		mYVelocity = 0;
		mRotate = 90;
		turbo = false;
	}

	public synchronized void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate(-mRotate+90, mXPos + GameActivity.SPACECRAFT_SIZE/2, mYPos + GameActivity.SPACECRAFT_SIZE/2);
		canvas.drawBitmap(mBitmap, mXPos, mYPos, parentView.getPainter());
		canvas.restore();
	}
	
	public synchronized boolean isTurboEnabled() {
		return turbo;
	}
	
	public synchronized void changeTurbo() {
		turbo = !turbo;
		
		if (turbo) {
			mBitmap = BitmapFactory.decodeResource(parentView.getResources(), R.drawable.spacecraft);
			mBitmap = Bitmap.createScaledBitmap(mBitmap, GameActivity.SPACECRAFT_SIZE, GameActivity.SPACECRAFT_SIZE, false);
		}
		else {
			mBitmap = BitmapFactory.decodeResource(parentView.getResources(), R.drawable.spacecraft_paused);
			mBitmap = Bitmap.createScaledBitmap(mBitmap, GameActivity.SPACECRAFT_SIZE, GameActivity.SPACECRAFT_SIZE, false);
		}
	}
	
	private void updateVelocity() {
		mXVelocity *= 0.98;
		mYVelocity *= 0.98;
		if (turbo && Math.abs(mXVelocity) < 0.3 && Math.abs(mYVelocity) < 0.3) {
			mXVelocity = 0;
			mYVelocity = 0;
			changeTurbo();
		}
	}
	
	public synchronized boolean nextMovement() {
		if (parentView.nextMovementSpacecraft()) {
			mXPos += mXVelocity;
			mYPos += mYVelocity;
			updateVelocity();
			return true;
		}
		return false;
	}
	
	public synchronized void setVelocity(float xVel, float yVel) {
		mXVelocity = xVel*GameActivity.SPACECRAFT_SPEED_RATE;
		mYVelocity = yVel*GameActivity.SPACECRAFT_SPEED_RATE;
	}
	
	public synchronized void updateRotation(float rotate) {
		mRotate = rotate;
	}
	
	public synchronized float getRotation() {
		return mRotate;
	}
	
	public synchronized PointF getPosition() {
		return new PointF(mXPos + GameActivity.SPACECRAFT_SIZE/2,mYPos + GameActivity.SPACECRAFT_SIZE/2);
	}
	
	public synchronized boolean intersects(float x, float y) {
		float xCenter = mXPos + GameActivity.SPACECRAFT_SIZE/2, yCenter = mYPos + GameActivity.SPACECRAFT_SIZE/2;
		return (Math.sqrt(Math.pow(Math.abs(xCenter - x),2) + Math.pow(Math.abs(yCenter - y),2)) <= GameActivity.SPACECRAFT_SIZE/2);
	}
	
	public boolean isOutsideScreen() {
		return (mXPos <= -MARGIN || mXPos >= parentView.getGameActivity().screenWidth - GameActivity.SPACECRAFT_SIZE + MARGIN || mYPos <= -MARGIN
				|| mYPos >= parentView.getGameActivity().screenHeight - GameActivity.SPACECRAFT_SIZE + MARGIN);
	}
}