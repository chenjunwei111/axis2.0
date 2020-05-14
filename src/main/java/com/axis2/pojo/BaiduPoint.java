package com.axis2.pojo;

/**
* Description
* @Author junwei
* @Date 14:13 2019/10/22
**/
public class BaiduPoint {
	private double lat;// 纬度
	private double lng;// 经度
	
	public BaiduPoint() {
	}
	
	public BaiduPoint(double lat, double lng) {
		this.lng = lng;
		this.lat = lat;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BaiduPoint) {
			BaiduPoint bmapPoint = (BaiduPoint) obj;
			return (bmapPoint.getLng() == lng && bmapPoint.getLat() == lat) ? true : false;
		} else {
			return false;
		}
	}
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	@Override
	public String toString() {
		return "Point [lat=" + lat + ", lng=" + lng + "]";
	}

}
