package com.example.testopengl;

import javax.microedition.khronos.opengles.GL10;

public class Triangle extends Shape {
	float[] mVertices = { 
			0.0f, 1.0f, 0.0f,
			-0.5f, -1.0f, 0.0f,
			0.5f, -1.0f, 0.0f };

	Triangle() {
		mVertexBuffer = getFloatBufferFromFloatArray(mVertices);
	}


	@Override
	void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glFrontFace(GL10.GL_CW);
		gl.glColor4f(1f, 0, 0, 0.5f);  // 새로 추가
		gl.glRotatef(45.0f, 0, 0, 1); 
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);     
	}
}