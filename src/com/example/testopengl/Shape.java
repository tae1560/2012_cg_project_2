package com.example.testopengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;

abstract class Shape {
	protected FloatBuffer mVertexBuffer = null;
	protected ByteBuffer mIndexBuffer = null;
	protected FloatBuffer mColorBuffer = null;
	protected FloatBuffer mTextureBuffer = null;
	protected FloatBuffer mNormalBuffer = null;
	
	abstract void draw(GL10 gl);
	
	// selection
	// 점과 도형의 거리 계산
	abstract double calculateDistance(PointData point);
	
	// 각 도형의 state 표기
	abstract int getState();
	protected boolean isSelected = false;
	
	// 선택되었을경우의 표기박스
	protected float[] selectBoxVertices = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
	protected FloatBuffer selectBoxVertexBuffer = null;
	protected float margin = 20;
	
	// 선택되고 선택취소될때 이벤트 함수
	abstract void onSelected();
	abstract void onUnselected();
	
	// event handling on edit mode
	abstract void onShapeTouchEvent(MotionEvent event, Context context, glesRenderer renderer);
	abstract void onShapePrepareOptionsMenu(Menu menu);
	abstract void onShapeOptionsItemSelected(MenuItem item, final Context context, glesRenderer renderer);
	
	// draw selection box
	protected void drawSelectionBox(GL10 gl) {
		if (isSelected) {
			// 선택 되어있으면 선택박스 그리기
			if (selectBoxVertexBuffer != null) {
				gl.glPushMatrix();
				gl.glColor4f(255 / 255f, 222 / 255f, 102 / 255f, 255 / 255f); 
				gl.glLineWidth(5f);
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, selectBoxVertexBuffer);
				gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, selectBoxVertices.length / 3);
				gl.glPopMatrix();
			}
		}
	}
	
	// 선택박스가 point를 포함하는지 판단
	protected boolean isSelctionBoxInclude(PointData point) {
		PointData topLeft = new PointData(selectBoxVertices[0], selectBoxVertices[1]);
		PointData bottomRight = new PointData(selectBoxVertices[6], selectBoxVertices[7]);
		topLeft = localPointToGlobalPoint(topLeft);
		bottomRight = localPointToGlobalPoint(bottomRight);
		
		return (topLeft.x - margin < point.x && bottomRight.x + margin > point.x
				&& topLeft.y + margin > point.y && bottomRight.y - margin < point.y);
	}
	
	// properties
	// position
	protected PointData position = new PointData(0, 0);
	protected PointData median = new PointData(0, 0);
	public void setPosition(float x, float y){
		position = new PointData(x, y);
	}
	
	public void setPosition(PointData point) {
		position = new PointData(point);
	}
	
	public void addPosition(PointData point) {
		position = position.add(point);
	}

	public PointData getPosition() {
		return position;
	}
	
	
	// color
	protected ColorData color = new ColorData(1,1,1,1);
	public void setColor(float r, float g, float b, float a) {
		this.setColor(new ColorData(r,g,b,a));
	}
	public void setColor(ColorData color) {
		this.color = new ColorData(color);
	}
	public ColorData getColor() {
		return this.color;
	}
	
	// lotation
	protected float rotation = 0f;
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	public float getRotation() {
		return this.rotation;
	}
	
	// scale
	protected float scale = 1f;
	public void setScale(float scale) {
		this.scale = scale;
	}
	public float getScale() {
		return this.scale;
	}
	
	
	// size
	float size = 20.0f;
	void setSize(float size) {
		this.size = size;
	}
	float getSize() {
		return this.size;
	}
	
	// background color
	protected ColorData backgroundColor = null;
	public void setBackgroundColor(float r, float g, float b, float a) {
		this.setBackgroundColor(new ColorData(r,g,b,a));
	}
	public void setBackgroundColor(ColorData color) {
		this.backgroundColor = new ColorData(color);
	}
	public ColorData getBackgroundColor() {
		return this.backgroundColor;
	}
	
	// texture
	protected boolean isTextureMapping = false;
	public void setIsTextureMapping(boolean isTextureMapping) {
		this.isTextureMapping = isTextureMapping;
	}
	public boolean getIsTextureMapping() {
		return this.isTextureMapping;
	}
	
	// input uis
	
	// input color ui - 선색깔
	static public ColorData option_color;
	public static void popupColorDialog(Context context) {
		new ColorPickerDialog(context, 
				new ColorPickerDialog.OnColorChangedListener() {
					
					@Override
					public void colorChanged(String key, int color) {
						// TODO Auto-generated method stub
						option_color = ColorData.intToColor(color);
					}
				}, 
				"KEY", 
				ColorData.colorToInt(option_color), 
				ColorData.colorToInt(option_color)).show();
	}
	
	// input size ui - 선굵기, 점크기 설정
	static public float option_size;
	public static void popupSizeDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("크기를 선택하세요.");

		// Set up the input
		final EditText input = new EditText(context);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(Integer.toString(Math.round(option_size)));
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		         option_size = Integer.parseInt(input.getText().toString());
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	
	// edit mode
	// 색깔 변경
	public static void popupEditColorDialog(Context context, final Shape shape) {
		new ColorPickerDialog(context, 
				new ColorPickerDialog.OnColorChangedListener() {
					
					@Override
					public void colorChanged(String key, int color) {
						// TODO Auto-generated method stub
						shape.setColor(ColorData.intToColor(color));
					}
				}, 
				"KEY", 
				ColorData.colorToInt(shape.getColor()), 
				ColorData.colorToInt(shape.getColor())).show();
	}
	
	// 배경색깔 채우기
	public static void popupEditBackgroundColorDialog(Context context, final Shape shape) {
		new ColorPickerDialog(context, 
				new ColorPickerDialog.OnColorChangedListener() {
					
					@Override
					public void colorChanged(String key, int color) {
						// TODO Auto-generated method stub
						shape.setBackgroundColor(ColorData.intToColor(color));
					}
				}, 
				"KEY", 
				ColorData.colorToInt(shape.getBackgroundColor()), 
				ColorData.colorToInt(shape.getBackgroundColor())).show();
	}
	
	// 선굵기, 점크기 변경
	public static void popupEditSizeDialog(Context context, final Shape shape) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("크기를 선택하세요.");

		// Set up the input
		final EditText input = new EditText(context);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(Integer.toString(Math.round(shape.getSize())));
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	shape.setSize(Integer.parseInt(input.getText().toString()));
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	

	// utility
	FloatBuffer getFloatBufferFromFloatArray(float array[]) {
		ByteBuffer tempBuffer = ByteBuffer.allocateDirect(array.length * 4);
		tempBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = tempBuffer.asFloatBuffer();
		buffer.put(array);
		buffer.position(0);
		return buffer;
	}

	ByteBuffer getByteBufferFromByteArray( byte array[]) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
		buffer.put(array);
		buffer.position(0);
		return buffer;
	}
	
	IntBuffer getIntBufferFromIntArray( int array[]) {
		IntBuffer buffer = IntBuffer.allocate(array.length * 4);
		buffer.put(array);
		buffer.position(0);
		return buffer;
	}
	
	// 좌표계의 변환 안드로이드 => OPENGL
	PointData globalPointToLocalPoint(PointData point) {
		PointData ret = ShapeUtil.translateDot(point, -position.x, -position.y);
		ret = ShapeUtil.translateDot(ret, median.x, median.y);
		ret = ShapeUtil.rotateDot(ret, -rotation);
		ret = ShapeUtil.scaleDot(ret, 1/scale, 1/scale);
		ret = ShapeUtil.translateDot(ret, -median.x, -median.y);
		return ret;
	}
	
	// 좌표계의 변환 OPENGL => 안드로이드 
	PointData localPointToGlobalPoint(PointData point) {
		PointData ret = ShapeUtil.translateDot(point, -median.x, -median.y);
		ret = ShapeUtil.scaleDot(ret, scale, scale);
		ret = ShapeUtil.rotateDot(ret, rotation);
		ret = ShapeUtil.translateDot(ret, median.x, median.y);
		ret = ShapeUtil.translateDot(ret, position.x, position.y);
		return ret;
	}
}