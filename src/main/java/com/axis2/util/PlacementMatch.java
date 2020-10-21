package com.axis2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PlacementMatch {

    private String _layer = "建筑面;";
    private HashMap<String, List<Double>> _builds_x;
    private HashMap<String, List<Double>> _builds_y;

    // 如果区域比较大，则将门限设置大一些；否则门限设置小一下
    private Double x_z = 0.0088;
    private Double y_z = 0.0075;

    public Double getX_z() {
        return x_z;
    }

    public void setX_z(Double x_z) {
        this.x_z = x_z;
    }

    public Double getY_z() {
        return y_z;
    }

    public void setY_z(Double y_z) {
        this.y_z = y_z;
    }

    public PlacementMatch() {

    }

    public PlacementMatch(String bid, List<Double> vertx, List<Double> verty) {
        _builds_x = new HashMap<String, List<Double>>();
        _builds_y = new HashMap<String, List<Double>>();

        _builds_x.put(bid, vertx);
        _builds_y.put(bid, verty);
    }

    // layer= "建筑面;"
    // x_x = 0.0088
    // y_y = 0.0075
    // 通过文件读取的方式加载面对象集合信息
    public PlacementMatch(String filePath, String layer, Double x_x, Double y_y) {

        x_z = x_x;
        y_z = y_y;
        if (!layer.equals("")) {
            _layer = layer;
        }
        List<String> buildings = loadbuildings(filePath);

        init(buildings);
    }

    // 初始化面对象集合到内存中
    public void init(List<String> buildings) {
        _builds_x = new HashMap<String, List<Double>>();
        _builds_y = new HashMap<String, List<Double>>();

        for (String xyList : buildings) {
            String[] xyl = xyList.split("=");
            String bid = xyl[0];
            String zbxl = xyl[1].replace(_layer, "");
            // System.out.println("--->zbxl:" + zbxl);
            String[] xys = zbxl.split(";");
            Integer nvert = xys.length; // 多边形的坐标点的个数
            List<Double> vertx = new ArrayList<Double>(nvert);
            List<Double> verty = new ArrayList<Double>(nvert);
            for (int num = 0; num < nvert; num++) {
                String[] xy = xys[num].split(",");
                // System.out.println("--->xys[" + num + "]:" + xys[num]);
                vertx.add(Double.parseDouble(xy[0]));
                verty.add(Double.parseDouble(xy[1]));
            }
            _builds_x.put(bid, vertx);
            _builds_y.put(bid, verty);
        }
    }

    // 初始化面对象集合到内存中 bid=112.2323,12.13132;123.1232222,23.43243
    private void init2(List<String> buildings) {
        _builds_x = new HashMap<String, List<Double>>();
        _builds_y = new HashMap<String, List<Double>>();

        for (String xyList : buildings) {
            String[] xyl = xyList.split("=");
            String bid = xyl[0];
            String zbxl = xyl[1];
            // System.out.println("--->zbxl:" + zbxl);
            String[] xys = zbxl.split(";");
            Integer nvert = xys.length; // 多边形的坐标点的个数
            List<Double> vertx = new ArrayList<Double>(nvert);
            List<Double> verty = new ArrayList<Double>(nvert);
            for (int num = 0; num < nvert; num++) {
                String[] xy = xys[num].split(",");
                vertx.add(Double.parseDouble(xy[0]));
                verty.add(Double.parseDouble(xy[1]));
            }
            _builds_x.put(bid, vertx);
            _builds_y.put(bid, verty);
        }
    }

    // 返回区域ID，绝对判断是否落入区域内
    public String Iterator(Double x, Double y) {
        String buildingID = "";
        Integer in = 0;
        Set<Map.Entry<String, List<Double>>> sets = _builds_x.entrySet();
        for (Map.Entry<String, List<Double>> entry : sets) {
            // System.out.print(entry.getKey() + ", ");
            // System.out.println(entry.getValue());
            List<Double> bidlist = entry.getValue();
            if ((Math.abs(bidlist.get(0) - x) < x_z)
                    && (Math.abs(_builds_y.get(entry.getKey()).get(0) - y) < y_z)) {
                boolean flag = FallIntoTheBuilding(entry.getValue(),
                        _builds_y.get(entry.getKey()), x, y);
                in++;
                if (flag) {
                    buildingID = entry.getKey();
                    break;
                }
            }
        }
        return buildingID;
    }

    // 用于比较大区域的多边形
    public String IteratornearLarge(Double x, Double y) {

        String buildingID = "";
        Integer in = 0;
        ArrayList<String> build_nos = new ArrayList<String>();
        Set<Map.Entry<String, List<Double>>> sets = _builds_x.entrySet();
        for (Map.Entry<String, List<Double>> entry : sets) {
            // System.out.print(entry.getKey() + ", ");
            // System.out.println(entry.getValue());
            List<Double> bidlist = entry.getValue();
            boolean flag = FallIntoTheBuilding(entry.getValue(),
                    _builds_y.get(entry.getKey()), x, y);
            in++;
            if (flag) {
                buildingID = entry.getKey();

                break;
            }
            build_nos.add(entry.getKey());
        }

        if (buildingID.equals("")) {
            // 计算最近的多边形
            Double minlen = 500000.0;
            // println("build_nos: " + build_nos.length)
            Integer irow = 0;
            for (String build_no : build_nos) {
                Double len = pointTopolygon(_builds_x.get(build_no),
                        _builds_y.get(build_no), x, y);
                irow = irow + 1;
                if (len < minlen) {
                    minlen = len;
                    buildingID = build_no;
                }
            }
            return buildingID + "\t" + minlen;
        } else {
            return buildingID + "\t" + 0;
        }
    }

    // 判断是否有落入到多边形范围内的小区，如果没有，则将最近的多边形记录下来，再求最近的多边形(用于比较小的多边形区域)
    public String Iteratornear(Double x, Double y) {
        String buildingID = "";
        int num = 0;
        List<String> build_nos = new ArrayList<String>();
        for (Map.Entry<String, List<Double>> bid : _builds_x.entrySet()) {
            // 如果采样点与建筑物的任意一个坐标点超过800米，则认为不在这个坐标序列内，直接不进入判断,是否可以加快解析速度
            if ((Math.abs(bid.getValue().get(0) - x) < x_z)
                    && (Math.abs(_builds_y.get(bid.getKey()).get(0) - y) < y_z)) {
                Boolean flag = FallIntoTheBuilding(bid.getValue(),
                        _builds_y.get(bid.getKey()), x, y);
                num = num + 1;
                if (flag) {
                    buildingID = bid.getKey();
                    break;
                }
                build_nos.add(bid.getKey());
            }
        }

        if (buildingID.equals("")) {
            // 计算最近的多边形
            Double minlen = (double) 500000;
            int irow = 0;

            for (String build_no : build_nos) {
                Double len = pointTopolygon(_builds_x.get(build_no),
                        _builds_y.get(build_no), x, y);
                irow = irow + 1;
                if (len < minlen) {
                    minlen = len;
                    buildingID = build_no;
                }
            }
            return buildingID + "\t" + String.format("%.6f", minlen);
        } else {
            return buildingID + "\t" + 0;
        }
    }

    private Double pointTopolygon(List<Double> vertx, List<Double> verty,
                                  Double x, Double y) {
        Integer nvert = vertx.size(); // 多边形的坐标点的个数
        Integer i = 0;
        Integer j = nvert - 1;

        Integer num = 0;
        Double minlength = 500000.0;
        while (i < nvert) {
            Double length = pointToLine(vertx.get(j), verty.get(j),
                    vertx.get(i), verty.get(i), x, y);
            if (length < minlength) {
                minlength = length;
                num = num + 1;
            }
            j = i;
            i = i + 1;
        }
        return minlength;
    }

    private Double pointToLine(Double x1, Double y1, Double x2, Double y2,
                               Double x0, Double y0) {

        Double space = 0.0;
        Double a = 0.0;
        Double b = 0.0;
        Double c = 0.0;

        a = lineSpace(x1, y1, x2, y2); // 线段的长度
        b = lineSpace(x1, y1, x0, y0); // (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0); // (x2,y2)到点的距离

        // println("a:" + f"${a}%10.6f" + ",b:" + f"${b}%10.6f" + ",c:" +
        // f"${c}%10.6f")

        if (c + b == a) {
            // 点在线段上
            space = 0.0;
            // println("c + b == a " + space)
            return space;
        }

        if (a <= 0.0000001) {
            // 不是线段，是一个点
            space = b;
            // println("a <= 0.0000001 " + space)
            return space;
        }

        if (c * c >= a * a + b * b) {
            // 组成直角三角形或钝角三角形，(x1,y1)为直角或钝角
            space = b;
            // println("c * c >= a * a + b * b " + space)
            return space;
        }

        if (b * b >= a * a + c * c) {
            // 组成直角三角形或钝角三角形，(x2,y2)为直角或钝角
            space = c;
            // println("b * b >= a * a + c * c " + space)
            return space;
        }

        // 组成锐角三角形，则求三角形的高
        Double p = (a + b + c) / 2; // 半周长
        Double s = Math.sqrt(p * (p - a) * (p - b) * (p - c)); // 海伦公式求面积
        space = 2 * s / a; // 返回点到线的距离（利用三角形面积公式求高）
        // println("space: " + space)
        return space;
    }

    // 判断是否有落入到多边形范围内的小区，如果没有，则将最近的多边形记录下来，再求最近的多边形(用于比较小的多边形区域)
    public String Iteratornearth(Double x, Double y) {
        String buildingID = "";
        int num = 0;
        List<String> build_nos = new ArrayList<String>();
        for (Map.Entry<String, List<Double>> bid : _builds_x.entrySet()) {
            // 如果采样点与建筑物的任意一个坐标点超过800米，则认为不在这个坐标序列内，直接不进入判断,是否可以加快解析速度
            if ((Math.abs(bid.getValue().get(0) - x) < x_z)
                    && (Math.abs(_builds_y.get(bid.getKey()).get(0) - y) < y_z)) {
                Boolean flag = FallIntoTheBuilding(bid.getValue(),
                        _builds_y.get(bid.getKey()), x, y);
                num = num + 1;
                if (flag) {
                    buildingID = bid.getKey();
                    break;
                }
                build_nos.add(bid.getKey());
            }
        }

        if (buildingID.equals("")) {
            // 计算最近的多边形
            Double minlen = (double) 500000;
            int irow = 0;

            for (String build_no : build_nos) {
                Double len = pointTopolygonEarth(_builds_x.get(build_no),
                        _builds_y.get(build_no), x, y);
                irow = irow + 1;
                if (len < minlen) {
                    minlen = len;
                    buildingID = build_no;
                }
            }
            return buildingID + "\t" + String.format("%.3f", minlen);
        } else {
            return buildingID + "\t" + 0;
        }
    }

    // 计算点到多边形的距离
    private Double pointTopolygonEarth(List<Double> vertx, List<Double> verty,
                                       Double x, Double y) {
        Integer nvert = vertx.size(); // 多边形的坐标点的个数
        Integer i = 0;
        Integer j = 0;
        j = nvert - 1;

        Integer num = 0;
        Double minlength = 500000.0;
        // println("nvert:" + nvert)
        while (i < nvert && minlength > 0) {
            Double length = pointToLineEarth(vertx.get(j), verty.get(j),
                    vertx.get(i), verty.get(i), x, y);
            // println("length:" + length)
            if (length < minlength) {
                minlength = length;
                num = num + 1;
                // println("num:" + num + "  x:" + x + ",y:" + y + "  " +
                // minlength)
                // println("计算点到多边形的距离 minlength: " + minlength)
            }
            j = i;
            i = i + 1;
        }
        // println("minlength:" + minlength)
        return minlength;
    }

    // 根据地球半径计算点到线的距离
    private Double pointToLineEarth(Double x1, Double y1, Double x2, Double y2,
                                    Double x0, Double y0) {

        Double space = 0.0;
        Double a = 0.0;
        Double b = 0.0;
        Double c = 0.0;

        a = JWD.getDistance(x1, y1, x2, y2); // 线段的长度
        b = JWD.getDistance(x1, y1, x0, y0); // (x1,y1)到点的距离
        c = JWD.getDistance(x2, y2, x0, y0); // (x2,y2)到点的距离

        // println("a:" + f"${a}%10.6f" + ",b:" + f"${b}%10.6f" + ",c:" +
        // f"${c}%10.6f")

        if (c + b == a) {
            // 点在线段上
            space = 0.0;
            // println("c + b == a " + space)
            return space;
        }

        if (a <= 0.0000001) {
            // 不是线段，是一个点
            space = b;
            // println("a <= 0.0000001 " + space)
            return space;
        }

        if (c * c >= a * a + b * b) {
            // 组成直角三角形或钝角三角形，(x1,y1)为直角或钝角
            space = b;
            // println("c * c >= a * a + b * b " + space)
            return space;
        }

        if (b * b >= a * a + c * c) {
            // 组成直角三角形或钝角三角形，(x2,y2)为直角或钝角
            space = c;
            // println("b * b >= a * a + c * c " + space)
            return space;
        }

        // 组成锐角三角形，则求三角形的高
        Double p = (a + b + c) / 2; // 半周长
        Double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a; // 返回点到线的距离（利用三角形面积公式求高）
        // println("space: " + space)
        return space;
    }

    private Double lineSpace(Double x1, Double y1, Double x2, Double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // 返回是否落入到多边形中
    public boolean FallIntoTheBuilding(List<Double> vertx, List<Double> verty,
                                       Double x, Double y) {
        Integer nvert = vertx.size(); // 多边形的坐标点的个数
        Integer i = 0;
        Integer j = 0;
        j = nvert - 1;
        boolean c = false;
        while (i < nvert) {
            if (((verty.get(i) > y) != (verty.get(j) > y))
                    && (x < (vertx.get(j) - vertx.get(i)) * (y - verty.get(i))
                    / (verty.get(j) - verty.get(i)) + vertx.get(i))) {
                c = !c;
            }
            j = i;
            i = i + 1;
        }
        return c;
    }

    // / <summary>
    // / 计算多边形面积的函数
    // / (以原点为基准点,分割为多个三角形)
    // / 定理：任意多边形的面积可由任意一点与多边形上依次两点连线构成的三角形矢量面积求和得出。矢量面积=三角形两边矢量的叉乘。
    // / </summary>
    // / <param name="vectorPoints"></param>
    // / <returns></returns>
    public double CalculateArea(List<Double> xs, List<Double> ys) {
        int iCycle, iCount;
        iCycle = 0;
        double iArea = 0;
        iCount = xs.size();

        for (iCycle = 0; iCycle < iCount; iCycle++) {
            iArea = iArea
                    + (xs.get(iCycle) * ys.get((iCycle + 1) % iCount) - xs
                    .get((iCycle + 1) % iCount) * ys.get(iCycle));
        }

        return (double) Math.abs(0.5 * iArea);
    }

    // 计算任意多边形面积
    public double ComputePolygonArea(List<Double> xs, List<Double> ys) {
        int point_num = xs.size();
        if (point_num < 3) {
            return 0.0;
        } else {
            double s = ys.get(0) * (xs.get(point_num - 1) - xs.get(1));
            for (int i = 1; i < point_num; i++) {
                s += ys.get(i) * (xs.get(i - 1) - xs.get((i + 1) % point_num));
            }
            return Math.abs(s) / 2.0 * 9101160000.085981;
        }
    }

    private List<String> loadbuildings(String filePath) {
        List<String> buildings = new ArrayList<String>();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(
                        new FileInputStream(file), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    buildings.add(lineTxt);
                }
                br.close();
            } else {
                System.out.println("文件不存在!");
            }
        } catch (Exception e) {
            System.out.println("文件读取错误!");
        }
        return buildings;
    }


    // key1:CITY_CODE，key2:COMMUNITY_ID
    //这个是存放场景的集合
    private static HashMap<String, HashMap<String, PoiScenePojo>> cCMap =
            new HashMap<String, HashMap<String, PoiScenePojo>>();

    //基于投诉地点获取经纬度
    public static String queryPoiData10(
            String cityCode, Double longitude, Double latitude, String fixpoi) {
            if (cCMap.containsKey(cityCode)) {
                List<String> buildings = new ArrayList<String>();
                PlacementMatch pm = new PlacementMatch();
                pm.setX_z(0.52);
                pm.setY_z(0.52);
                // String sss =
                // "0j5c6a0veu48;0jf7cu3lkulo;0ikjkl21rqk;07g5o42rsuj;06barcuuhk;0gtnrbhbg748;06t0ba3psoh;0m0r7ceijg6o;0ho241ni3h0o;0b0f45dd99ng;0mbjf7rrsjk;06kvedmk61f8;0rf2kjbst5j;0ubmm9bo3h1o;0ck8hh4o0kg";
                List<String> list = null;
                if (fixpoi != null && (!"".equals(fixpoi))) {
                    list = Arrays.asList(fixpoi.split(";"));
                }
                // System.out.println("总长度:"
                // + cCMap.get(cityCode).entrySet().size());
                for (Map.Entry<String, PoiScenePojo> ps : cCMap.get(cityCode).entrySet()) {

                    if (list != null && (!list.contains(ps.getValue().getCommunityId()))) {
                        continue;
                    }

                    // 判断投诉点地址与POI的地址是否相差3公里以上,如果超过,则返回无匹配结果，有则，继续匹配
                    String[] locations = ps.getValue().getLocations().split(";");
                    boolean isfar = false;
                    // 任何一个投诉点多余6公里的POI场景，则不需要进行精确判断
                    for (String ll : locations) {
                        String[] point = ll.split(",");

                        Double distance =
                                JWD.getDistance(
                                        Double.parseDouble(point[0]),
                                        Double.parseDouble(point[1]),
                                        longitude,
                                        latitude);
                        // System.out.println(distance);
                        if (distance > 30) {
                            isfar = true;
                            break;
                        }
                    }

                    if (isfar) {
                        continue;
                    } else {
                        buildings.add(ps.getValue().getCommunityId() + "=" + ps.getValue().getLocations());
                    }
                }

                if (buildings.size() > 0) {
                    pm.init(buildings); // 筛选后的近距离POI信息
                    String[] poid = pm.Iteratornearth(longitude, latitude).split("\t");
                    // System.out.println(poid[0] + "," + poid[1]);
                    return poid[0] + "," + poid[1];
                    // if (Double.parseDouble(poid[1]) < 0.01) {
                    // return poid[0];
                    // // return poid[0] + "," + poid[1];
                    // } else
                    // return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
    }

    public static void main(String[] args) {
        List<Double> xs = new ArrayList<Double>();
        List<Double> ys = new ArrayList<Double>();
        // 102.738731,24.993306;102.739179,24.993067;102.738874,24.992620;102.738463,24.992877
        // 坐标序列：102.717637,25.014062;102.717865,25.013567;102.716734,25.012950;102.716317,25.013545
        // 投诉点：102.716536，25.01419

        xs.add(102.717637);
        xs.add(102.717865);
        xs.add(102.716734);
        xs.add(102.716317);

        ys.add(25.014062);
        ys.add(25.013567);
        ys.add(25.012950);
        ys.add(25.013545);
        //
        PlacementMatch pm = new PlacementMatch("bid1", xs, ys);
        pm.setX_z(0.02);
        pm.setY_z(0.02);

        System.out.println("102.716536，25.01419==>"
                + pm.Iteratornearth(102.716536, 25.01419));

        // 坐标102.738815,24.992968，经纬度：102.739198，24.993952
        // 坐标序列102.738731,24.993306;102.739179,24.993067;102.738874,24.992620;102.738463,24.992877
        // PlacementMatch pm2 = new PlacementMatch();
        // double area = pm2.ComputePolygonArea(xs, ys);
        // System.out.println(area);
        //
        // return;
        // TODO Auto-generated method stub
        // String path = PlacementMatch.class.getResource("/").getPath()
        // .concat("areaProperties/" + "kunming" + "_special_scene.txt");
        //
        // System.out.println("path:" + path);
        // String layer = "Special;";
        // // 经纬度隔离距离
        // Double x_x = 0.0088;
        // Double y_y = 0.0075;
        // PlacementMatch pm = new PlacementMatch("bid1", xs, ys);

        // 判断点在什么区域ID内//可对同一个地市数据进行遍历
        Double x = 102.738815;
        Double y = 24.992968;
        String bid = pm.Iterator(x, y);
        // System.out.println("------>bid:" + bid);
    }

}
