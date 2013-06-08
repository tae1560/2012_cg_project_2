package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class PolyLine extends Shape {
	Vector<PointData> mVertices = new Vector<PointData>();

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
		
		
		// 테두리 칠하기
		gl.glColor4f(color.r, color.g, color.b, color.a); 
		gl.glLineWidth(size);
		
		if (mVertexBuffer != null)
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		if (mVertices.size() > 1)
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.size());
		else {
			// 점이 하나일때는 표기해줌
			gl.glPointSize(20f);
			gl.glDrawArrays(GL10.GL_POINTS, 0, mVertices.size());
		}
		
		// 선택 사각형 칠하기
		drawSelectionBox(gl);
				
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	
	// 마지막 입력중인 점의 위치 설정
	void setLastPointPosition(float x, float y) {
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
		
		// 중앙값 설정
		median = ShapeUtil.findMedianPoint(mVertices);
	}
	
	void setLastPointPosition(PointData point) {
		setLastPointPosition(point.x, point.y);
	}
	
	// ui
	static PolyLine insertingShape = null;
	static public void onTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			insertingShape.setLastPointPosition(event.getX(), event.getY());
//			insertingShape = null;
			break;
		case MotionEvent.ACTION_DOWN: 
			if (insertingShape == null) {
				// 새로운 입력일때
				insertingShape = new PolyLine();
				insertingShape.setColor(option_color);
				insertingShape.setSize(option_size);
				
				Toast.makeText(context, "입력을 완료하면 메뉴를 눌러 완료해주세요.", Toast.LENGTH_SHORT).show();
				renderer.getShapes().add(insertingShape);
			}
			// 누르면 새로운 점 추가
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
			// 입력중일 경우
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
	
	// selecting object
	public double calculateDistance(PointData point) {
		double dis1 = 9999;
		
		// 모든 선분으로부터의 거리에서 가장 작은값 계산
		PointData prevVertex = null;
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
		return glesRenderer.EDIT_POLYLINE_STATE;
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
	}
	public void onUnselected() {
		isSelected = false;
		selectBoxVertexBuffer = null;
	}
	
	// event handling on edit mode
	public boolean onShapeTouchEvent(MotionEvent event, Context context, glesRenderer renderer) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		
		return false;
	}
	public void onShapePrepareOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "크기 설정");
		menu.add(0, 1, 0, "색 설정");
		menu.add(0, 2, 0, "채우기");
		menu.add(0, 3, 0, "텍스처맵핑");
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