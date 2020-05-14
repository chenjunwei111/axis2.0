package com.axis2.pojo;


/**
* Description
* @Author junwei
* @Date 15:02 2019/9/27
**/
public class Pixel {
	private double x;
	private double y;
	private String _x;
	private String _y;
	public Pixel(double x, double y)
	{
		this.x=x;
		this.y=y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public  void   Round(int n) {
		if ((Double) this.x != null) {
			this.x = Double.parseDouble(String.format("%." + n + "f", this.x));
		}
		if ((Double) this.y != null) {
			this.y = Double.parseDouble(String.format("%." + n + "f", this.y));
		}
	}

	public String get_x() {
		if (this._x == null) {
			if ((Double) this.x != null) {
				this._x = String.format("%.2f", this.x);
			}
		}
		return _x;
	}

	public String get_y() {
		if (this._y == null) {
			if ((Double) this.y != null) {
				this._y = String.format("%.2f", this.y);
			}
		}
		return _y;
	}

	@Override
	public String toString() {
		return "Pixel{" +
				"x=" + x +
				", y=" + y +
				", _x='" + _x + '\'' +
				", _y='" + _y + '\'' +
				'}';
	}
}
