package com.example.testopengl;

public class Point {
	public float x;
	public float y;
	
	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
}
