package com.example.testopengl;

public class PointData {
	public float x;
	public float y;
	
	PointData(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	PointData(PointData point) {
		this.x = point.x;
		this.y = point.y;
	}
}
