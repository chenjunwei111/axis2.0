package com.axis2.test;


import com.axis2.util.ExcelUtil;
import com.axis2.util.JdbcServer;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
* Description 测试excel文件读取
* @Author junwei
* @Date 11:08 2019/9/30
**/
public class TestExcelRead {


    public static void main(String[] args) {


        List<List<Object>> listTotal= ExcelUtil.getExeclData("D:\\complaintLogs\\20190905153715118.xls");
//        System.out.println(list);

        //插入日志记录
        String insertLogSql=" insert into P_CHH_INFO(" +
                " PEOPLE_NAME, PHONE_YD, PHONE_LT, PHONE_DX, CITY, PROPERTY," +
                "JOB_1, JOB_2, JOB_3, JOB_4, JOB_5, " +
                "WIRELESS_SUBJECT, MOVING_RING_SUBJECT, TRAN_SUBJECT, CORE_SUBJECT, DATA_SUBJECT, " +
                "NET_OPTIMIZE_SUBJECT, NET_ADMIN_SUBJECT, COLLECT_CUSTOM_SUBJECT, HOME_CUSTOM_SUBJECT, SAFE_SUBJECT, " +
                "INTEGRATED_DISPATCHING, WORK_BUILD_SUBJECT, IF_EMERGENCY_COMMUNICATION, IF_RECEIVE_IVR_CALL, IF_RECEIVE_VR_MESSAGE," +
                "START_TIME_HOUR_IVR1, START_TIME_MINUTE_IVR1, END_TIME_HOUR_IVR1, END_TIME_MINUTE_IVR1, START_TIME_HOUR_IVR2," +
                "START_TIME_MINUTE_IVR2, END_TIME_HOUR_IVR2, END_TIME_MINUTE_IVR2, DEFEND_COMPANY, REMARK, IF_RECEIVE_EOMS_MESSAGE" +
                ") " +
                "values (" +
                "?,?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?,?" +
                ")";

        LinkedList tempList = new LinkedList<>();

        for (int i=0;i<listTotal.size();i++){
            //前两行为表头，去掉
            if(i<=1){
                continue;
            }

           Map<String,Object> map=new LinkedHashMap<>();
            map.put("PEOPLE_NAME",listTotal.get(i).get(0)==null?null:listTotal.get(i).get(0));
            map.put("PHONE_YD",listTotal.get(i).get(1)==null?null:listTotal.get(i).get(1));
            map.put("PHONE_LT",listTotal.get(i).get(2)==null?null:listTotal.get(i).get(2));
            map.put("PHONE_DX",listTotal.get(i).get(3)==null?null:listTotal.get(i).get(3));
            map.put("CITY",listTotal.get(i).get(4)==null?null:listTotal.get(i).get(4));
            map.put("PROPERTY",listTotal.get(i).get(5)==null?null:listTotal.get(i).get(5));

            map.put("JOB_1",listTotal.get(i).get(6)==null?null:listTotal.get(i).get(6));
            map.put("JOB_2",listTotal.get(i).get(7)==null?null:listTotal.get(i).get(7));
            map.put("JOB_3",listTotal.get(i).get(8)==null?null:listTotal.get(i).get(8));
            map.put("JOB_4",listTotal.get(i).get(9)==null?null:listTotal.get(i).get(9));
            map.put("JOB_5",listTotal.get(i).get(10)==null?null:listTotal.get(i).get(10));

            map.put("WIRELESS_SUBJECT",listTotal.get(i).get(11)==null?null:listTotal.get(i).get(11));
            map.put("MOVING_RING_SUBJECT",listTotal.get(i).get(12)==null?null:listTotal.get(i).get(12));
            map.put("TRAN_SUBJECT",listTotal.get(i).get(13)==null?null:listTotal.get(i).get(13));
            map.put("CORE_SUBJECT",listTotal.get(i).get(14)==null?null:listTotal.get(i).get(14));
            map.put("DATA_SUBJECT",listTotal.get(i).get(15)==null?null:listTotal.get(i).get(15));

            map.put("NET_OPTIMIZE_SUBJECT",listTotal.get(i).get(16)==null?null:listTotal.get(i).get(16));
            map.put("NET_ADMIN_SUBJECT",listTotal.get(i).get(17)==null?null:listTotal.get(i).get(17));
            map.put("COLLECT_CUSTOM_SUBJECT",listTotal.get(i).get(18)==null?null:listTotal.get(i).get(18));
            map.put("HOME_CUSTOM_SUBJECT",listTotal.get(i).get(19)==null?null:listTotal.get(i).get(19));
            map.put("SAFE_SUBJECT",listTotal.get(i).get(20)==null?null:listTotal.get(i).get(20));

            map.put("INTEGRATED_DISPATCHING",listTotal.get(i).get(21)==null?null:listTotal.get(i).get(21));
            map.put("WORK_BUILD_SUBJECT",listTotal.get(i).get(22)==null?null:listTotal.get(i).get(22));
            map.put("IF_EMERGENCY_COMMUNICATION",listTotal.get(i).get(23)==null?null:listTotal.get(i).get(23));
            map.put("IF_RECEIVE_IVR_CALL",listTotal.get(i).get(24)==null?null:listTotal.get(i).get(24));
            map.put("IF_RECEIVE_VR_MESSAGE",listTotal.get(i).get(25)==null?null:listTotal.get(i).get(25));

            map.put("START_TIME_HOUR_IVR1",listTotal.get(i).get(26)==null?null:listTotal.get(i).get(26));
            map.put("START_TIME_MINUTE_IVR1",listTotal.get(i).get(27)==null?null:listTotal.get(i).get(27));
            map.put("END_TIME_HOUR_IVR1",listTotal.get(i).get(28)==null?null:listTotal.get(i).get(28));
            map.put("END_TIME_MINUTE_IVR1",listTotal.get(i).get(29)==null?null:listTotal.get(i).get(29));
            map.put("START_TIME_HOUR_IVR2",listTotal.get(i).get(30)==null?null:listTotal.get(i).get(30));

            map.put("START_TIME_MINUTE_IVR2",listTotal.get(i).get(31)==null?null:listTotal.get(i).get(31));
            map.put("END_TIME_HOUR_IVR2",listTotal.get(i).get(32)==null?null:listTotal.get(i).get(32));
            map.put("END_TIME_MINUTE_IVR2",listTotal.get(i).get(33)==null?null:listTotal.get(i).get(33));
            map.put("DEFEND_COMPANY",listTotal.get(i).size()<=34?null:listTotal.get(i).get(34));
            map.put("REMARK",listTotal.get(i).size()<=35?null:listTotal.get(i).get(35));
            map.put("IF_RECEIVE_EOMS_MESSAGE",listTotal.get(i).size()<=36?null:listTotal.get(i).get(36));

            tempList.add(map);
        }
        boolean res1= JdbcServer.inserts(insertLogSql,tempList);
        System.out.println(tempList);
        System.out.println(res1);
    }

}
