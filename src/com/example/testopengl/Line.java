package com.example.testopengl;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class Line extends Shape {
	float[] mVertices = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};

	Line() {
		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
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
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.length / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	
	
	void setLastPointPosition(float x, float y) {
		mVertices[3] = x - position.x;
		mVertices[4] = y - position.y;
		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static Line insertingShape = null;
	static public boolean onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			insertingShape.setLastPointPosition(event.getX(), event.getY());
			insertingShape = null;
			break;
		case MotionEvent.ACTION_DOWN: 
			insertingShape = new Line();
			renderer.getShapes().add(insertingShape);
			insertingShape.setPosition(event.getX(), event.getY());
			insertingShape.setColor(option_color);
			insertingShape.setSize(option_size);
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
		menu.add(0, 0, 0, "크기 설정");
		menu.add(0, 1, 0, "색 설정");
		menu.add(0, 2, 0, "앞으로");
	}
	
	static public void onOptionsItemSelected(MenuItem item, final Context context, glesRenderer renderer) {
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
	}
}