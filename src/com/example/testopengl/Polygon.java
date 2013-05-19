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
		
		// 이동변환
		gl.glTranslatef(position.x, position.y, 0);
		
		// 회전 및 신축 변환
		gl.glTranslatef(median.x, median.y, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		gl.glScalef(scale, scale, 1);
		gl.glTranslatef(-median.x, -median.y, 0);
		
		// 선택 사각형 칠하기
		drawSelectionBox(gl);
		
		
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
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices.size());
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glPopMatrix();
			} else if (backgroundColor != null) {
				// 배경 칠하기
				gl.glPushMatrix();
				gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a); 
				gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, mVertices.size());
				gl.glPopMatrix();
			}
			

			// 테두리 칠하기
			gl.glPushMatrix();
			gl.glColor4f(color.r, color.g, color.b, color.a); 
			gl.glLineWidth(size);
			
			if (mVertices.size() > 1)
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.size());
			else {
				// 점이 하나일때는 표기해줌
				gl.glPointSize(20f);
				gl.glDrawArrays(GL10.GL_POINTS, 0, mVertices.size());
			}
			gl.glPopMatrix();
		}
		
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	
	
	boolean setLastPointPosition(float x, float y) {
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
		
		// 중앙점 설정
		median = ShapeUtil.findMedianPoint(mVertices);
		
		return isFinished;
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static Polygon insertingShape = null;
	static public void onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
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
	
	// selecting object
	public double calculateDistance(PointData point) {
		double dis1 = 9999;
		
		// 모든 선분중에서 가장 가까운 점과의 거리를 계산
		PointData prevVertex = mVertices.lastElement();
		for (PointData vertex : mVertices) {
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
		left = right = mVertices.firstElement().x;
		bottom = top = mVertices.firstElement().y;
		
		for (PointData vertex : mVertices) {
			if(left > vertex.x) left = vertex.x;
			if(right < vertex.x) right = vertex.x;
			if(top < vertex.y) top = vertex.y;
			if(bottom > vertex.y) bottom = vertex.y;
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
		mTextureBuffer = getFloatBufferFromFloatArray(ShapeUtil.convertPointsForTextureMapping(mVertices, selectBoxVertices));		
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