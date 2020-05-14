package com.axis2.test;

import com.axis2.util.ConfigReaderUtils;
import com.axis2.util.JdbcServer;
import com.axis2.util.XmlUtil;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
* Description 测试报文发送
* @Author junwei
* @Date 15:50 2019/9/30
**/
public class TestSendMsg {

    public static void main(String[] args) {

        String sendSheet="20180412X18787425301,20180412X18787908839";
        try {
            String url = ConfigReaderUtils.getProperty("yiYangReportOrderUrl");
            String methor = ConfigReaderUtils.getProperty("yiYangReportOrderMethor");
            String orderNums []=sendSheet.split(",");

            String orderS="";
            for (String order:orderNums){
                orderS+=order+"','";
            }
            orderS=orderS.substring(0,orderS.length()-3);

            String checkCityCode = "select t1.EOMS_ORDERNUM,t1.ORDERNUM,t2.申告地 CITY,t2.区县 as property,\n" +
                    "to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') export_time,\n" +
                    "t2.LTE关键小区问题描述 DETAIL from (SELECT ORDERNUM,EOMS_ORDERNUM\n" +
                    "FROM P_LTE_PARMETER_COMPLAINT\n" +
                    "WHERE ORDERNUM in ('"+orderS+"')) t1\n" +
                    "left join FINAL_ANALYSIS_TABLE_V2_1 t2 on t1.ORDERNUM=t2.工单流水号 ";
            ResultSet rs2 = JdbcServer.executeStatement(checkCityCode);
            LinkedList<Map<String,Object>> list=new LinkedList<>();
            while (rs2.next()) {
                Map<String,Object> map=new HashMap<>();
                map.put("EOMS_ORDERNUM",rs2.getString(1));
                map.put("ORDERNUM",rs2.getString(2));
                map.put("CITY",rs2.getString(3));
                map.put("PROPERTY",rs2.getString(4));
                map.put("EXPORT_TIME",rs2.getString(5));
                map.put("DETAIL",rs2.getString(6));
                list.add(map);
            }

            String sendxml = XmlUtil.xmlOrderMsg(list).replace("\n", "");

            String result2 = XmlUtil.invoke(url, methor, sendxml);

            System.out.println(sendxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
