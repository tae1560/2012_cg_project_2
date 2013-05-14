package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

public class glesRenderer implements Renderer {

	// 그리는 shape 들의 객체 array
	Vector<Shape> shapes = new Vector<Shape>();
	Context context = null;
	
	public glesRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
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
		
		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = shapes.get(i);
			shape.draw(gl);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
//		gl.glViewport(0, 0, width, height);    
//		gl.glEnable(GL10.GL_TEXTURE_2D);
//		gl.glMatrixMode(GL10.GL_PROJECTION);
//		gl.glLoadIdentity();                   
//		GLU.gluPerspective(gl, 45.0f, (float) width / height, 0.01f, 1000.0f);                    
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//
//		gl.glLoadIdentity();
		// viewport 를 디바이스의 화면과 일치시킴
		gl.glViewport(0, 0, width, height);

	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
	    // 직교투영
	    GLU.gluOrtho2D(gl, 0, width, height, 0);
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	
	// 상태 리스트
	private final int IDEAL_STATE = 0;
	private final int INPUT_DOT_STATE = 1;
	private final int INPUT_LINE_STATE = 2;
	
	// 현재 상태
	int state = IDEAL_STATE;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	// 터치 이벤트에 대한 처리 => 각각의 도형 클래스가 일을 처리함
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

		default:
			break;
		}
		
		return true;
	}
	
	// 메뉴에 대한 layout 설정
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		switch (state) {
		case IDEAL_STATE:
			menu.add(0, 0, 0, "점 그리기");		
			menu.add(0, 1, 0, "선 그리기");
			menu.add(0, 2, 0, "편집");
			break;
		case INPUT_DOT_STATE: 
			menu.add(0, 0, 0, "크기 설정");
			menu.add(0, 1, 0, "색 설정");
			menu.add(0, 2, 0, "완료");
			break;
			
		case INPUT_LINE_STATE: 
			menu.add(0, 0, 0, "크기 설정");
			menu.add(0, 1, 0, "색 설정");
			menu.add(0, 2, 0, "완료");
			break;

		default:
			break;
		}
		
		return true;
	}
	
	// 메뉴 선택에 대한 이벤트 처리
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (state) {
		case IDEAL_STATE:
			switch (item.getItemId()) {
			case 0: 
				state = INPUT_DOT_STATE;
				Toast.makeText(context, "메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				state = INPUT_LINE_STATE;
				Toast.makeText(context, "메뉴를 눌러 속성을 설정하고 화면에 입력하세요.", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				break;
			}
			break;
		case INPUT_DOT_STATE: // 점 그리기
			Dot.onOptionsItemSelected(item, context, this);
			break;
		case INPUT_LINE_STATE: // 점 그리기
			Line.onOptionsItemSelected(item, context, this);
			break;
		default:
			break;
		}
		
		// TODO Auto-generated method stub
		return true;
	}

	public Vector<Shape> getShapes() {
		return shapes;
	}
	
}
