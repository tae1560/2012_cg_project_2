package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class Polygon extends Shape {
	Vector<PointData> mVertices = new Vector<PointData>();

	Polygon() {
//		mVertexBuffer = getFloatBufferFromFloatArray(new float[]{0f, 0f, 0f});
	}

	@Override
	void draw(GL10 gl) {
		gl.glPushMatrix();
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glFrontFace(GL10.GL_CW);

		gl.glColor4f(color.r, color.g, color.b, color.a);  // 새로 추가
		gl.glRotatef(rotation, 0, 0, 1); 
		
//		Log.e("test", ": " + position.x + " " + position.y);
		gl.glTranslatef(position.x, position.y, 0);
		gl.glLineWidth(size);
		
		if (mVertexBuffer != null)
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		if (mVertices.size() > 1)
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.size());
		else {
			gl.glPointSize(20f);
			gl.glDrawArrays(GL10.GL_POINTS, 0, mVertices.size());
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	
	
	boolean setLastPointPosition(float x, float y) {
//		mVertices[3] = x - position.x;
//		mVertices[4] = y - position.y;
//		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
		boolean isFinished = false;
		
		// 처음과 비슷하면 중력장 기능
		if(mVertices.size()>2 && Math.pow(mVertices.firstElement().x - x, 2f) + Math.pow(mVertices.firstElement().y - y, 2f) < 2500) {
			mVertices.lastElement().x = mVertices.firstElement().x;
			mVertices.lastElement().y = mVertices.firstElement().y;
			isFinished = true;
		} else {
			mVertices.lastElement().x = x;
			mVertices.lastElement().y = y;
		}
		
		// convert vector to array
		float[] verticesArray = new float[mVertices.size() * 3];
		for (int i = 0; i < mVertices.size() * 3; i+=3 ) {
			verticesArray[i] = mVertices.get(i/3).x;
			verticesArray[i+1] = mVertices.get(i/3).y;
			verticesArray[i+2] = 0;
		}
		
		mVertexBuffer = getFloatBufferFromFloatArray(verticesArray);
		
		return isFinished;
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static Polygon insertingShape = null;
	static public boolean onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			if(insertingShape.setLastPointPosition(event.getX(), event.getY()))
				insertingShape = null;
//			insertingShape = null;
			break;
		case MotionEvent.ACTION_DOWN: 
			if (insertingShape == null) {
				insertingShape = new Polygon();
				insertingShape.setColor(option_color);
				insertingShape.setSize(option_size);
				
				Toast.makeText(context, "입력을 완료하려면 다각형을 만들어야 합니다.", Toast.LENGTH_SHORT).show();
				renderer.getShapes().add(insertingShape);
			}
			insertingShape.mVertices.add(new PointData(0f, 0f));
			insertingShape.setLastPointPosition(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			insertingShape.setLastPointPosition(event.getX(), event.getY());
			break;
		}
		
		return true;
	}
		
	static public void initialize() {
		option_size = 5.0f;
		option_color = new ColorData(1,1,1,1);
	}
	
	static public void onPrepareOptionsMenu(Menu menu) {
		if(insertingShape == null) {
			menu.add(0, 0, 0, "크기 설정");
			menu.add(0, 1, 0, "색 설정");
			menu.add(0, 2, 0, "앞으로");
		} else {
			menu.add(0, 0, 0, "입력취소");
		}
		
	}
	
	static public void onOptionsItemSelected(MenuItem item, final Context context, glesRenderer renderer) {
		
		if(insertingShape == null) {
			switch (item.getItemId()) {
			case 0: 
				popupSizeDialog(context);
				break;
			case 1:
				popupColorDialog(context);
				break;
			case 2:
				renderer.setState(glesRenderer.IDEAL_STATE);
				break;
			}
		} else {
			renderer.getShapes().remove(insertingShape);
			insertingShape = null;
		}
		
		
	}
}