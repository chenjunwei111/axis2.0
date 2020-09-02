package com.axis2.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SeqCheckPushUtil {

    private static final Logger log = Logger.getLogger(httpUtil.class.getClass());

    //"每个工单开始等待5分钟，先去SEQ查找对应的占用小区，如果有就做生成URL作为附件透传"
    public JSONObject SeqCellCheck(Object orderNum,Object customer_tel,Object fault_msisdn){
        String[] csvHeaders={"DAY_ID", "START_TIME", "END_TIME", "MSISDN", "CELL_ID"};
        String csvFilePath="\\data2\\ftp_root\\xiaotijun";
//        String csvFilePath="\\D:\\TEST\\";
        Connection conn = jdbcUtil.getConnection();
        String orderNumRes=orderNum.toString();
        String dayIdRes=orderNum.toString().substring(0,8);
        //投诉号码
        String customerTelRes=customer_tel.toString();
        //故障号码
        String faultMsisdnRes=fault_msisdn.toString();

    //关键字传入以后，开始从SEQ查询对应的指标
        String fetchSeqInfo="select DAY_ID, START_TIME, END_TIME, MSISDN, CELL_ID from (select DAY_ID, " +
                "START_TIME, END_TIME, MSISDN,to_number(ENB_ID,'xxxxx')||'-'||to_number(SECTOR_ID,'xxxxx') CELL_ID " +
                "from(select A.DAY_ID,A.START_TIME, A.END_TIME, A.MSISDN, A.cell_id,substr(A.CELL_ID,0,5) enb_id," +
                "substr(A.CELL_ID,6,2) sector_id FROM (select to_char(VERSION_DATE,'yyyymmdd') day_id," +
                "to_char(START_TIME,'yyyymmdd hh:mm:ss') START_TIME,to_char(END_TIME,'yyyymmdd hh:mm:ss') END_TIME," +
                "MSISDN,substr(SECTOR_ID,6,20) CELL_ID from P_ACCEPT_CHH_INFO " +
                "where substr(SECTOR_ID,6,20) is not null) A)) where MSISDN IN (" +"'"+customerTelRes+"','"+faultMsisdnRes+"')"
                +" and day_id="+"'"+dayIdRes+"'";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(fetchSeqInfo);
            rs = pstmt.executeQuery();
//            转换为JSON对象
//            JSONObject jsonObj = new JSONObject();
            //转换为JSON集合
            ArrayList array = new ArrayList() ;
            //增加输出表头
            array.add(csvHeaders);
            while(rs.next()) {
                String DAY_ID = rs.getString("DAY_ID");
                String START_TIME = rs.getString("START_TIME");
                String END_TIME = rs.getString("END_TIME");
                String MSISDN = rs.getString("MSISDN");
                String CELL_ID = rs.getString("CELL_ID");
                Object[] objects = {DAY_ID,START_TIME,END_TIME,MSISDN,CELL_ID};
                array.add(objects);
            }
            //log.info("正在查询投诉用户对应的SEQ占用小区，生成csv文件");
            JSONObject JsonSeqcsv=new CsvWriteUtils().createCsvFile(array,csvFilePath,customer_tel.toString(),dayIdRes);
                if (JsonSeqcsv!=null){
                    log.info("检查csv文件存在，开始生成对应url连接");
                    //JsonSeqcsv.put("url","http://10.174.238.10/SpdbShareData/complain/seq_detail/"+dayIdRes+"_"+customer_tel.toString());
                    //封装成JSON对象，传回来的JSONOBJECT文件大小/日期/名称重新增加URL封装
                    JSONObject jo=new JSONObject(new LinkedHashMap());
                    jo.put("name",JsonSeqcsv.getString("name"));
                    jo.put("url","http://10.174.238.10/SpdbShareData/complain/seq_detail/"+dayIdRes+"_"+customer_tel.toString()+".csv");
                    jo.put("create_time",JsonSeqcsv.getString("create_time"));
                    jo.put("file_size",JsonSeqcsv.getString("file_size"));
                    log.info(jo);
                    //继续把JSON对象封装成JSON集合
                    return jo;
                    //return "http://10.174.238.10/SpdbShareData/complain/seq_detail/"+dayIdRes+"_"+customer_tel.toString();
                }else{
                    log.info("检查csv文件不存在，返回空");
                    return null;
                }

        } catch (SQLException e) {
            log.error("******************封装SEQ占用小区数据错误：",e);
            e.printStackTrace();
        } finally {
            jdbcUtil.release(conn, pstmt, rs);
        }
        return null;
    }
}
