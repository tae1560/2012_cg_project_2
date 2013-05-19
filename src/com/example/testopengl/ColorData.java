package com.example.testopengl;

//색깔 저장을 위한 클래스
public class ColorData {
	public float r;
	public float g;
	public float b;
	public float a;
	
	ColorData(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	ColorData(ColorData color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}
	
	// 16진수 int를 ColorData 클래스로 변경 
	static ColorData intToColor(int i) {
		int b = (i)&0xFF;
		int g = (i>>8)&0xFF;
		int r = (i>>16)&0xFF;
		int a = (i>>24)&0xFF;
		
		return new ColorData(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
	// ColorData 클래스를 16진수 int로 변경
	static int colorToInt(ColorData c) {
		if (c == null) {
			c = new ColorData(1f, 1f, 1f, 1f);
		}
		int rgb = Math.round(c.a * 255);
		rgb = (rgb << 8) + Math.round(c.r * 255);
		rgb = (rgb << 8) + Math.round(c.g * 255);
		rgb = (rgb << 8) + Math.round(c.b * 255);
		
		return rgb;
	}
}
