package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class Circle extends Shape {
	private float[] mVertices = new float[360 * 3];

	Circle() {
//		mVertexBuffer = getFloatBufferFromFloatArray(new float[]{0f, 0f, 0f});
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
		
		if (mVertexBuffer != null) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		
			if (isTextureMapping && mTextureBuffer != null) {
				// 텍스처 입히기
				gl.glPushMatrix();
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glColor4f(1f, 1f, 1f, 1f);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, glesRenderer.mMaterialBookTexture[0]);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices.length / 3);
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glPopMatrix();
			} else if (backgroundColor != null) {
				// 배경 칠하기
				gl.glPushMatrix();
				gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a); 
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices.length / 3);
				gl.glPopMatrix();
			}
			
			// 테두리 칠하기
			gl.glPushMatrix();
			gl.glColor4f(color.r, color.g, color.b, color.a); 
			gl.glLineWidth(size);
			
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.length / 3);
			gl.glPopMatrix();
		}
		
		// 선택 사각형 칠하기
		drawSelectionBox(gl);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	
	
	void setLastPointPosition(float x, float y) {
		// 원의 반지름에 맞게 다시그리기
		int xScale = Math.round(Math.abs(position.x -  x));
		int yScale = Math.round(Math.abs(position.y -  y));
		
		// 타원은 원에 각각 x값과 y값을 늘이면 타원이 된다.
		
		for (int i=0; i < 360; i++) {
			double degInRad = ShapeUtil.degreeToRadian(i); 
			mVertices[3*i] = (float) (xScale*Math.cos(degInRad*8));
			mVertices[3*i+1] = (float) (yScale*Math.sin(degInRad*8));
			mVertices[3*i+2] = 0;
		}	 
		
		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
		
		// 중앙점 설정
		Vector<PointData> points = new Vector<PointData>();
		for (int i = 0; i < mVertices.length; i+= 3) {
			points.add(new PointData(mVertices[i], mVertices[i+1]));
		}
		median = ShapeUtil.findMedianPoint(points);
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static Circle insertingShape = null;
	static public void onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			insertingShape.setLastPointPosition(event.getX(), event.getY());
			insertingShape = null;
			break;
		case MotionEvent.ACTION_DOWN: 
			if (insertingShape == null) {
				insertingShape = new Circle();
				insertingShape.setColor(option_color);
				insertingShape.setSize(option_size);
				
				renderer.getShapes().add(insertingShape);
			}
			insertingShape.setPosition(event.getX(), event.getY());
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
		double dis1 = 9999;
		
		// 모든 선분으로부터 거리 계산
		PointData prevVertex = new PointData(mVertices[360*3 - 3], mVertices[360*3 - 2]);
		for (int i = 0; i < mVertices.length; i+=3) {
			PointData vertex = new PointData(mVertices[i], mVertices[i+1]);
			if(prevVertex != null) {
				double dis2 = ShapeUtil.distanceOfDotAndSegment(point, localPointToGlobalPoint(vertex), localPointToGlobalPoint(prevVertex)); 
				if(dis1 > dis2) dis1 = dis2;
			}
			
			prevVertex = vertex;
		}
		
		return dis1;
	}
	
	public int getState() {
		return glesRenderer.EDIT_POLYGON_STATE;
	}
	
	public void onSelected() {
		isSelected = true;
		
		// find boundary
		float left, right, bottom, top;
		left = right = mVertices[0];
		bottom = top = mVertices[1];
		
		for (int i = 0; i < mVertices.length; i+=3) {
			if(left > mVertices[i]) left = mVertices[i];
			if(right < mVertices[i]) right = mVertices[i];
			if(top < mVertices[i+1]) top = mVertices[i+1];
			if(bottom > mVertices[i+1]) bottom = mVertices[i+1];
		}
		
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
		
		// 텍스처 설정
		Vector<PointData> points = new Vector<PointData>();
		for (int i = 0; i < mVertices.length; i+= 3) {
			points.add(new PointData(mVertices[i], mVertices[i+1]));
		}
		mTextureBuffer = getFloatBufferFromFloatArray(ShapeUtil.convertPointsForTextureMapping(points, selectBoxVertices));		
	}
	public void onUnselected() {
		isSelected = false;
		selectBoxVertexBuffer = null;
	}
	
	// event handling on edit mode
	boolean isSwalling = false;
	PointData prevPoint = null;
	public boolean onShapeTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		PointData point = new PointData(event.getX(), event.getY()); 
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			setLastPointPosition(point);
			isSwalling = false;
//			prevPoint = null;
			break;
		case MotionEvent.ACTION_DOWN:
			if (isSelected) {
//				selectBoxVertices[9, 10]
				// 모양변형
				PointData controlPoint = new PointData(new PointData(selectBoxVertices[9], selectBoxVertices[10]));
				double distance = ShapeUtil.distanceOfDots(localPointToGlobalPoint(controlPoint), point);
				
				if (distance < 50) {
					isSwalling = true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			setLastPointPosition(localPointToGlobalPoint(point));
			break;
		}
		
		if (isSwalling) {
			return true;
		}
		
		return false;
	}
	public void onShapePrepareOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "크기 설정");
		menu.add(0, 1, 0, "색 설정");
		menu.add(0, 2, 0, "채우기");
		if (this.getIsTextureMapping()) {
			menu.add(0, 3, 0, "텍스처삭제");
		} else {
			menu.add(0, 3, 0, "텍스처맵핑");
		}
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
			if (this.getIsTextureMapping()) {
				setIsTextureMapping(false);
			} else {
				setIsTextureMapping(true);
			}
			break;
		case 4:
			renderer.getShapes().remove(this);
			renderer.setState(glesRenderer.EDIT_STATE);
			break;
		case 5:
			renderer.setState(glesRenderer.IDEAL_STATE);
			break;
		}
	}
}