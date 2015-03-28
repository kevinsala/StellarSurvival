package com.example.stellarsurvival;

import android.graphics.Canvas;
import android.graphics.PointF;

public class Laser {
	/* Parent surface */
	private final GameView parentView;
	
	/* Attributes */
	private float mXPos, mYPos, mXVelocity, mYVelocity, mRotate;

	public Laser(GameView parent, float x, float y, float xVel, float yVel) {
		
		parentView = parent;
		mXPos = x - GameActivity.LASER_WIDTH/2;
		mYPos = y - GameActivity.LASER_HEIGHT/2;
		mXVelocity = xVel;
		mYVelocity = yVel;
		mRotate = 0;
	}

	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate(-mRotate+90, mXPos + GameActivity.LASER_WIDTH/2, mYPos + GameActivity.LASER_HEIGHT/2);
		canvas.drawBitmap(parentView.getGameActivity().laserBitmap, mXPos, mYPos, parentView.getPainter());
		canvas.restore();
	}
	
	public boolean nextMovement() {
		if (!isOutsideScreen()) {
			mXPos += mXVelocity;
			mYPos += mYVelocity;
			return true;
		}
		return false;
	}
	
	public synchronized void setVelocity(float xVel, float yVel) {
		mXVelocity = xVel*GameActivity.LASER_SPEED_RATE;
		mYVelocity = yVel*GameActivity.LASER_SPEED_RATE;
	}
	
	public void setRotation(float rotation) {
		mRotate = rotation;
	}
	
	public synchronized PointF getPosition() {
		return new PointF(mXPos,mYPos);
	}
	
	public synchronized boolean intersects(float x, float y, float radius) {
		float dxMid = Math.abs(x - (mXPos + (float)GameActivity.LASER_WIDTH/2));
		float dyMid = Math.abs(y - (mYPos + (float)GameActivity.LASER_HEIGHT/2));
		return (Math.sqrt(Math.pow(dxMid,2) + Math.pow(dyMid,2)) <= radius);
	}
	
	private boolean isOutsideScreen() {
		return (mXPos <= -GameActivity.LASER_HEIGHT || mXPos >= parentView.getGameActivity().getScreenWidth() + GameActivity.LASER_HEIGHT 
				|| mYPos <= -GameActivity.LASER_HEIGHT || mYPos >= parentView.getGameActivity().getScreenHeight());
	}
}
