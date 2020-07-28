package com.axis2.test;

public class Test1 {

    public static void main(String[] args) {
        String[] busType = "本地类型→内部工单→流程穿越→问题提交→业务使用障碍".split("→");

        String voice1;
        if (busType.length < 5) {
            voice1 = busType[busType.length - 1];//如果第5位没有，则拿最后一个做
        } else {
            voice1 = busType[4];
        }
        System.out.println(voice1);
    }
}
