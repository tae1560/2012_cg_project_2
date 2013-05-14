package com.example.testopengl;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

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
//		gl.glPointSize(size);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.length / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
		
		gl.glPopMatrix();
	}
	
	
	Point position = new Point(0, 0);
	void setPosition(float x, float y){
		position = new Point(x, y);
	}
	
	void setPosition(Point point) {
		position = new Point(point);
	}
	
	void setLastPointPosition(float x, float y) {
		mVertices[3] = x - position.x;
		mVertices[4] = y - position.y;
		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
	}
	
	void setLastPointPosition(Point point) {
		setLastPointPosition(point.x, point.y);
	}
	
	Point getPosition() {
		return position;
	}
	
	ColorData color = new ColorData(1,1,1,1);
	void setColor(float r, float g, float b, float a) {
		this.color = new ColorData(r,g,b,a);
	}
	void setColor(ColorData color) {
		this.color = new ColorData(color);
	}
	
	float rotation = 0f;
	void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	float size = 20.0f;
	void setSize(float size) {
		this.size = size;
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
	
	static public float option_size = 20.0f;
	static public ColorData option_color = new ColorData(1,1,1,1);
	static public boolean onOptionsItemSelected(MenuItem item, final Context context, glesRenderer renderer) {
		switch (item.getItemId()) {
		case 0: 
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("속성을 선택하세요.");

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
			
			break;
		case 1:
			new ColorPickerDialog(context, 
					new ColorPickerDialog.OnColorChangedListener() {
						
						@Override
						public void colorChanged(String key, int color) {
							// TODO Auto-generated method stub
							Toast.makeText(context, key+ " color : " + ColorData.intToColor(color).a + " " + ColorData.intToColor(color).r + " " + ColorData.intToColor(color).g + " " + ColorData.intToColor(color).b , Toast.LENGTH_LONG).show();
							Toast.makeText(context, key+ " color : " + Integer.toHexString(color) , Toast.LENGTH_LONG).show();
							
							option_color = ColorData.intToColor(color);
						}
					}, 
					"DROIDS_COLOR_KEY", 
					ColorData.colorToInt(option_color), 
					ColorData.colorToInt(option_color)).show();
			break;
		case 2:
			// 기본값 복원
			option_size = 20.0f;
			option_color = new ColorData(1,1,1,1);
			renderer.setState(0);
			break;
		}
		
		return true;
	}
}