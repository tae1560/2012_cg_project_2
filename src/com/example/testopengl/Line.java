package com.example.testopengl;

import java.util.Vector;

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

		// 이동변환
		gl.glTranslatef(position.x, position.y, 0);
		
		// 회전 및 신축 변환
		gl.glTranslatef(median.x, median.y, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		gl.glScalef(scale, scale, 1);
		gl.glTranslatef(-median.x, -median.y, 0);
		
		// 선택 사각형 칠하기
		drawSelectionBox(gl);
		
		// 테두리 칠하기
		gl.glColor4f(color.r, color.g, color.b, color.a); 
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
		
		Vector<PointData> points = new Vector<PointData>();
		points.add(new PointData(mVertices[0], mVertices[1]));
		points.add(new PointData(mVertices[3], mVertices[4]));
		median = ShapeUtil.findMedianPoint(points);
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static Line insertingShape = null;
	static public void onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
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
	
	// selecting object
	public double calculateDistance(PointData point) {
		PointData tv1 = new PointData(mVertices[0], mVertices[1]);
		PointData tv2 = new PointData(mVertices[3], mVertices[4]);
		
		return ShapeUtil.distanceOfDotAndSegment(point, localPointToGlobalPoint(tv1), localPointToGlobalPoint(tv2));
	}
	
	public int getState() {
		return glesRenderer.EDIT_LINE_STATE;
	}
	
	public void onSelected() {
		isSelected = true;
		
		// find boundary
		float left, right, bottom, top;
		left = right = mVertices[0];
		bottom = top = mVertices[1];
		
		if(left > mVertices[3]) left = mVertices[3];
		if(right < mVertices[3]) right = mVertices[3];
		if(top < mVertices[4]) top = mVertices[4];
		if(bottom > mVertices[4]) bottom = mVertices[4];
		
		// set boundary
		selectBoxVertices[0] = left - margin;
		selectBoxVertices[1] = top + margin;
		selectBoxVertices[3] = left - margin;
		selectBoxVertices[4] = bottom - margin;
		selectBoxVertices[6] = right + margin;
		selectBoxVertices[7] = bottom - margin;
		selectBoxVertices[9] = right + margin;
		selectBoxVertices[10] = top + margin;
		
		selectBoxVertexBuffer = getFloatBufferFromFloatArray(selectBoxVertices);
	}
	public void onUnselected() {
		isSelected = false;
		selectBoxVertexBuffer = null;
	}
	
	// event handling on edit mode
	public void onShapeTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
	}
	public void onShapePrepareOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "크기 설정");
		menu.add(0, 1, 0, "색 설정");
		menu.add(0, 4, 0, "삭제");
		menu.add(0, 5, 0, "앞으로");
	}
	public void onShapeOptionsItemSelected(MenuItem item, final Context context, glesRenderer renderer) {
		switch (item.getItemId()) {
		case 0: 
			popupEditSizeDialog(context, this);
			break;
		case 1:
			popupEditColorDialog(context, this);
			break;
		case 2:
			popupEditBackgroundColorDialog(context, this);
			break;
		case 3:
			break;
		case 4:
			renderer.getShapes().remove(this);
			renderer.setState(glesRenderer.IDEAL_STATE);
			break;
		case 5:
			renderer.setState(glesRenderer.IDEAL_STATE);
			break;
		}
	}
}