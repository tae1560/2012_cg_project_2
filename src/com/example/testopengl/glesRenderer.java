package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class glesRenderer implements Renderer {

	// 그리는 shape 들의 객체 array
	Vector<Shape> shapes = new Vector<Shape>();
	Context context = null;
	
	public glesRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		setState(IDEAL_STATE);
	}
	
	// surface 가 처음 만들어 질때
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glLoadIdentity();
//		gl.glClearColor(1f, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT|
				GL10.GL_DEPTH_BUFFER_BIT);
//		gl.glTranslatef(0, 0, -600f);
		
		// shapes에 저장된 도형들을 그린다.
		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = shapes.get(i);
			shape.draw(gl);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// viewport 를 디바이스의 화면과 일치시킴
		gl.glViewport(0, 0, width, height);
	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glLoadIdentity();
	    GLU.gluOrtho2D(gl, 0, width, height, 0);
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	
	// 상태 리스트
	public static final int IDEAL_STATE = 0;
	public static final int INPUT_DOT_STATE = 1;
	public static final int INPUT_LINE_STATE = 2;
	public static final int INPUT_POLYLINE_STATE = 3;
	public static final int INPUT_CIRCLE_STATE = 4;
	public static final int INPUT_OVAL_STATE = 5;
	public static final int INPUT_POLYGON_STATE = 6;
	public static final int EDIT_STATE = 7;
	
	public static final int EDIT_DOT_STATE = 7;
	public static final int EDIT_LINE_STATE = 7;
	public static final int EDIT_POLYLINE_STATE = 7;
	public static final int EDIT_CIRCLE_STATE = 7;
	public static final int EDIT_OVAL_STATE = 7;
	public static final int EDIT_POLYGON_STATE = 7;
	
	// 현재 상태
	int state;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		didChangedState(state);
	}
	
	// state 바뀐 후 event
	public void didChangedState(int state) {
		switch (state) {
		case IDEAL_STATE: // 제일 처음 시작시 터치를 하면 메시지 출력
			Toast.makeText(context, "기본 모드 : 메뉴버튼을 눌러 메뉴를 선택하세요.", Toast.LENGTH_SHORT).show();
			break;
		case INPUT_DOT_STATE: // 점 그리기
			Dot.initialize();
			Toast.makeText(context, "점그리기 : 메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
			break;
			
		case INPUT_LINE_STATE:
			Line.initialize();
			Toast.makeText(context, "선그리기 : 메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
			break;
			
		case INPUT_POLYLINE_STATE:
			PolyLine.initialize();
			Toast.makeText(context, "연속선그리기 : 메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
			break;
			
		case INPUT_CIRCLE_STATE:
			break;
		case INPUT_OVAL_STATE:
			break;
		case INPUT_POLYGON_STATE:
			Polygon.initialize();
			Toast.makeText(context, "다각형그리기 : 메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
			
			break;
		case EDIT_STATE:
			break;

		default:
			break;
		}
	}
		
	// 터치 이벤트에 대한 처리
	public boolean onTouchEvent(MotionEvent event) {
		switch (state) {
		case IDEAL_STATE: // 제일 처음 시작시 터치를 하면 메시지 출력
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Toast.makeText(context, "메뉴버튼을 눌러 메뉴를 선택하세요.", Toast.LENGTH_SHORT).show();
			}
			break;
		case INPUT_DOT_STATE: // 점 그리기
			Dot.onTouchEvent(event, context, this);
			break;
			
		case INPUT_LINE_STATE:
			Line.onTouchEvent(event, context, this);
			break;
			
		case INPUT_POLYLINE_STATE:
			PolyLine.onTouchEvent(event, context, this);
			break;
			
		case INPUT_CIRCLE_STATE:
			break;
		case INPUT_OVAL_STATE:
			break;
		case INPUT_POLYGON_STATE:
			Polygon.onTouchEvent(event, context, this);
			break;
		case EDIT_STATE:
			break;

		default:
			break;
		}
		
		return true;
	}
	
	// 메뉴에 대한 layout 설정
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
//		menu.add(0, 100, 0, "temp");		
		switch (state) {
		case IDEAL_STATE:
			menu.add(0, INPUT_DOT_STATE, 0, "점 그리기");		
			menu.add(0, INPUT_LINE_STATE, 1, "선 그리기");
			menu.add(0, INPUT_POLYLINE_STATE, 2, "연속선 그리기");
//			menu.add(0, INPUT_CIRCLE_STATE, 3, "원 그리기");
//			menu.add(0, INPUT_OVAL_STATE, 4, "타원 그리기");
			menu.add(0, INPUT_POLYGON_STATE, 5, "다각형 그리기");
//			menu.add(0, EDIT_STATE, 6, "편집");
			break;
		case INPUT_DOT_STATE: 
			Dot.onPrepareOptionsMenu(menu);
			break;
			
		case INPUT_LINE_STATE: 
			Line.onPrepareOptionsMenu(menu);
			break;
			
		case INPUT_POLYLINE_STATE: 
			PolyLine.onPrepareOptionsMenu(menu);
			break;
			
//		case INPUT_CIRCLE_STATE:
//			break;
//		case INPUT_OVAL_STATE: 
//			break;
		case INPUT_POLYGON_STATE:
//			Toast.makeText(context, "INPUT_POLYGON_STATE", Toast.LENGTH_SHORT).show();
			
			Polygon.onPrepareOptionsMenu(menu);
//			menu.add(0, 0, 0, "입력취소");
//			Toast.makeText(context, "menu", Toast.LENGTH_SHORT).show();
//			Polygon.onPrepareOptionsMenu(menu);
			
			break;
//		case EDIT_STATE:
//			break;

		default:
			break;
		}
		
		return true;
	}
	
	// 메뉴 선택에 대한 이벤트 처리
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (state) {
		case IDEAL_STATE:
			// 그리기 모드의 각 도형 혹은 편집모드 선택
			setState(item.getItemId());
			break;
		case INPUT_DOT_STATE: // 점 그리기
			Dot.onOptionsItemSelected(item, context, this);
			break;
		case INPUT_LINE_STATE: // 점 그리기
			Line.onOptionsItemSelected(item, context, this);
			break;
		case INPUT_POLYLINE_STATE: // 점 그리기
			PolyLine.onOptionsItemSelected(item, context, this);
			break;
		case INPUT_CIRCLE_STATE: // 원 그리기
			break;
		case INPUT_OVAL_STATE: // 타원 그리기
			break;
		case INPUT_POLYGON_STATE: // 다각형 그리기
			Polygon.onOptionsItemSelected(item, context, this);
			break;
		case EDIT_STATE: // 편집 모드
			break;
		
		default:
			break;
		}
		
		// TODO Auto-generated method stub
		return true;
	}

	
	// getter and setter
	public Vector<Shape> getShapes() {
		return shapes;
	}
	
}
