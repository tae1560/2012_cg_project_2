package com.example.testopengl;

import java.util.Vector;


public class ShapeUtil {
	// 점과 점 사이의 거리
	public static double distanceOfDots(PointData point1, PointData point2) {
		return Math.sqrt(Math.pow(point1.x - point2.x,2f) + Math.pow(point1.y - point2.y,2f));
	}
	
	// 점과 선분 사이의 거리
	public static double distanceOfDotAndSegment(PointData point1, PointData line1, PointData line2) {
		PointData se = new PointData(line2.x - line1.x, line2.y - line1.y);
		PointData sp = new PointData(line1.x - point1.x, line1.y - point1.y);
		PointData ep = new PointData(line2.x - point1.x, line2.y - point1.y);
		
		double dis = 9999;
		if((se.x*sp.x + se.y*sp.y) * (se.x*ep.x + se.y*ep.y) <= 0) {
			// 선분 내부에 점이 있을경우
			double a = se.y;
			double b = -se.x;
			double c = -se.y * line1.x + se.x * line1.y;
			
			dis =  (a*point1.x + b*point1.y + c) / (Math.sqrt(Math.pow(a,2f) + Math.pow(b,2f)));
			dis = Math.abs(dis);
		} else {
			// 선분 외부에 점이 있을경우
			
			double dis1 = Math.sqrt(Math.pow(point1.x - line1.x, 2f) + Math.pow(point1.y - line1.y, 2f));
			double dis2 = Math.sqrt(Math.pow(point1.x - line2.x, 2f) + Math.pow(point1.y - line2.y, 2f));
			dis = Math.min(dis1, dis2);
		}
		
		
		
		return dis;
	}
	
	// 이동변환
	public static PointData translateDot(PointData point1, double mx, double my) {
		return new PointData(point1.x + mx, point1.y + my);
	}

	// 신축변환
	public static PointData scaleDot(PointData point1, double sx, double sy) {
		return new PointData(point1.x * sx, point1.y * sy);
	}
	
	// 회전변환
	public static PointData rotateDot(PointData point1, double theta) {
		double radian = degreeToRadian(theta);
		return new PointData(point1.x * Math.cos(radian) - point1.y * Math.sin(radian),
				point1.x * Math.sin(radian) + point1.y * Math.cos(radian));
	}
	
	// 각도 변환
	public static double degreeToRadian(double degree) {
		return degree / 180 * Math.PI;
	}
	
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}
	
	// 중심점 찾기
	public static PointData findMedianPoint (Vector<PointData> points) {
		double x = 0;
		double y = 0;
		for (int i = 0; i < points.size(); i++) {
			x += points.get(i).x;
			y += points.get(i).y;
		}
		x /= points.size();
		y /= points.size();
		
		return new PointData(x, y);
	}
	
	// 텍스처 매핑을 위한 좌표변환
	public static float[] convertPointsForTextureMapping(Vector<PointData> points, float[] boundary) {
		float[] ret = new float[points.size() * 2];
		float left = boundary[0];
		float right = boundary[6];
		float top = boundary[1];
		float bottom = boundary[7];
		
		float diffx = right - left;
		float diffy = top - bottom;
		
		for (PointData point : points) {
			try {
				float ratiox = (point.x - left) / diffx;
				float ratioy = (point.y - bottom) / diffy;
				
				ret[points.indexOf(point) * 2 + 0] = ratiox;
				ret[points.indexOf(point) * 2 + 1] = ratioy;
			} catch (Exception e) {
				// 비율이 0일경우 예외처리
				ret[points.indexOf(point) * 2 + 0] = 0;
				ret[points.indexOf(point) * 2 + 1] = 0;
			}
		}

		return ret;
	}
	
}
