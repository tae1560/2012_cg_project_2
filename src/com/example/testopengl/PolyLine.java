package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class PolyLine extends Shape {
	Vector<PointData> mVertices = new Vector<PointData>();

	PolyLine() {
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
	
	
	
	
	void setLastPointPosition(float x, float y) {
//		mVertices[3] = x - position.x;
//		mVertices[4] = y - position.y;
//		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
		mVertices.lastElement().x = x;
		mVertices.lastElement().y = y;
		
		// convert vector to array
		float[] verticesArray = new float[mVertices.size() * 3];
		for (int i = 0; i < mVertices.size() * 3; i+=3 ) {
			verticesArray[i] = mVertices.get(i/3).x;
			verticesArray[i+1] = mVertices.get(i/3).y;
			verticesArray[i+2] = 0;
		}
		
		mVertexBuffer = getFloatBufferFromFloatArray(verticesArray);
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static PolyLine insertingShape = null;
	static public boolean onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			insertingShape.setLastPointPosition(event.getX(), event.getY());
//			insertingShape = null;
			break;
		case MotionEvent.ACTION_DOWN: 
			if (insertingShape == null) {
				insertingShape = new PolyLine();
				insertingShape.setColor(option_color);
				insertingShape.setSize(option_size);
				
				Toast.makeText(context, "입력을 완료하면 메뉴를 눌러 완료해주세요.", Toast.LENGTH_SHORT).show();
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
			menu.add(0, 0, 0, "입력완료");
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
			insertingShape = null;
		}
		
		
	}
}