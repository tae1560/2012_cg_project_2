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
import android.widget.EditText;

abstract class Shape {
	protected FloatBuffer mVertexBuffer = null;
	protected ByteBuffer mIndexBuffer = null;
	protected FloatBuffer mColorBuffer = null;
	protected FloatBuffer mTextureBuffer = null;
	protected FloatBuffer mNormalBuffer = null;
	
	abstract void draw(GL10 gl);
	
	// properties
	// position
	protected PointData position = new PointData(0, 0);
	public void setPosition(float x, float y){
		position = new PointData(x, y);
	}
	
	public void setPosition(PointData point) {
		position = new PointData(point);
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
	
	// lotation
	protected float rotation = 0f;
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	
	// size
	float size = 20.0f;
	void setSize(float size) {
		this.size = size;
	}
	
	
	// input uis
	
	// input color ui
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
	
	// input size ui
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
}