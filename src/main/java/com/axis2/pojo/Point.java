package com.axis2.pojo;


import com.axis2.util.Transformation;

/**
* Description
* @Author junwei
* @Date 15:02 2019/9/27
**/
public class Point {
	private double lng;
	private double lat;

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public Point(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	public void Round() {
		if ((Double) this.lng != null) {
			this.lng = Double.parseDouble(String.format("%.6f", this.lng));
		}
		if ((Double) this.lat != null) {
			this.lat = Double.parseDouble(String.format("%.6f", this.lat));
		}
	}

	public void tobd() {
		Transformation tf = new Transformation();
		Point point = tf.trans(this, "w2b");
		this.lng = point.lng;
		this.lat = point.lat;
	}

	@Override
	public String toString() {
		return "Point [lng=" + lng + ", lat=" + lat + "]";
	}

}
