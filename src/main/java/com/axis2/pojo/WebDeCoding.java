package com.axis2.pojo;


public class WebDeCoding {

    private String name;
    private String area;
    private String addr;
    private Point point;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "WebDeCoding{" +
                "name='" + name + '\'' +
                ", area='" + area + '\'' +
                ", addr='" + addr + '\'' +
                ", point=" + point +
                '}';
    }
}
