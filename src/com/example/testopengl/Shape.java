package com.example.testopengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

abstract class Shape {
	public FloatBuffer mVertexBuffer;
	public ByteBuffer mIndexBuffer;
	public FloatBuffer mColorBuffer;
	public FloatBuffer mTextureBuffer;
	public FloatBuffer mNormalBuffer;
	
	abstract void draw(GL10 gl);

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