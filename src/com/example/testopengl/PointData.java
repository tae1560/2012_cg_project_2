package com.example.testopengl;

public class PointData {
	public float x;
	public float y;
	
	PointData(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	PointData(double x, double y) {
		this.x = (float)x;
		this.y = (float)y;
	}
	
	PointData(PointData point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	public PointData add(PointData point) {
		return new PointData(this.x + point.x, this.y + point.y);
	}
}
