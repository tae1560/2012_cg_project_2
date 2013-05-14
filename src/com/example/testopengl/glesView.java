package com.example.testopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class glesView extends GLSurfaceView {

	glesRenderer renderer;
	public glesView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		renderer = new glesRenderer(context);
		setRenderer(renderer);
	}
	
	public glesRenderer getRenderer() {
		return renderer;
	}



	private float prevX = 0;
	private float prevY = 0;
	private boolean isFirstTouch = true;
	private float prevDistance = 0;
	private boolean isFirstDistance = true;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		Light.isTurnOn = !Light.isTurnOn;
		renderer.onTouchEvent(event);
		
//		switch(event.getAction()) {
//		case MotionEvent.ACTION_UP:
//			isFirstTouch = true;
//			isFirstDistance = true;
//			break;
//		case MotionEvent.ACTION_DOWN: 
//			prevX = event.getX();
//			prevY = event.getY();
//			isFirstTouch = false;
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (event.getPointerCount() == 1) {
//				// one touch => drag
//				if (!isFirstTouch) {
//					float diffX = event.getX() - prevX;
//					float diffY = event.getY() - prevY;
//					
////					mRenderer.drag(diffX, diffY);
//				}
//				
//				isFirstTouch = false;
//				prevX = event.getX();
//				prevY = event.getY();
//			} else if (event.getPointerCount() == 2) {
//				float currentDistance = (float) (Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
//				if(!isFirstDistance) {
//					float diffDistance = currentDistance - prevDistance;
////					Log.e("TAEHO_OPENGL", "diffDistance =>" + diffDistance);
////					mRenderer.zoom(diffDistance);
//				}
//				
//				isFirstDistance = false;
//				isFirstTouch = true;
//				prevDistance = currentDistance;
//			}
////			Log.e("TAEHO_OPENGL", "MOVE =>" + event.getPointerCount());
////			Log.e("TAEHO_OPENGL", "X : " + event.getX());
////			Log.e("TAEHO_OPENGL", "Y : " + event.getY());
//			break;
//		}
		
		return true;
	}
}
