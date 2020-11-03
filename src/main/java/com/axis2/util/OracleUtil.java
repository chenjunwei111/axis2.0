package com.axis2.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;


/**
* Description
* @param
* @Author 肖体俊
* @Date 14:22 2020/6/2
**/
public class OracleUtil {
    private static final Logger log = Logger.getLogger(OracleUtil.class.getClass());
    // 第一次透传数据查询并写入到json对象

    public JSONArray  nsnTurnAnalyReport(String sql) {

        Connection conn = jdbcUtil.getConnection();
        String sql_bak = "select  a.NSN_PARENT_ID city,a.NSN_ID region, b.order_name,a.UE_X,a.UE_Y," +
                "b.order_theme,b.order_time,b.accept_time_limit,b.complain_time,b.customer_name," +
                "b.customer_tel,b.complain_area," + "b.complain_content," +
                "b.large_area_complain,b.repeated_complaints,b.customer_level,b.home_subscriber," +
                "b.complaint_acceptance_province,b.fault_msisdn," +
                "b.fault_area,b.complain_city,b.appeal_area,a.crm_ordernum,b.terminal_description," +
                "b.td_supported,b.analysis_condition,b.customer_dispatch_time," +
                "b.demarcation_analysis_results,b.cch_sys_delimit_time,b.complain_suggestion1," +
                "b.complain_suggestion2,b.complain_suggestion3," +
                "b.complain_suggestion4,b.complain_suggestion5,a.cgi,a.cgi1,a.analysis_result from" +
                "(select S.NSN_ID, S.NSN_PARENT_ID,T.CITY, T.PROPERTY,F.工单流水号 crm_ordernum,F.备注1 " +
                "analysis_result,F.LTE关键小区 cgi,substr(COMPARE_SECTOR,0," +
                "instr(T.COMPARE_SECTOR,'(')-1) cgi1,T.UE_X, T.UE_Y "
                + "from TEMP_CP_COMPLAIN_LIST_ORDER_V1 T,FINAL_ANALYSIS_TABLE_V2_1 F," +
                "spdb_nsn_area_change S  WHERE T.ORDERNUM=F.工单流水号 " +
                "AND T.PROPERTY=S.SPDB_PROPERTY ) a, " +
                "(select eoms_ordernum as  order_name,businesstype as order_theme, " +
                "to_char(version_date,'yyyy-mm-dd hh24:mi:ss') as order_time," +
                "to_char(order_accept_limit_time,'yyyy-mm-dd hh24:mi:ss') as accept_time_limit," +
                "city,ue_property as region,ue_date as complain_time, customer_name," +
                " phonenum as customer_tel, complain_area,bussinesscontent as complain_content," +
                " if_big_area_complaint as large_area_complain, repeated_complaints," +
                "complaint_level as customer_level, home_subscriber," +
                "complaint_acceptance_province, fault_msisdn,compliantplace as fault_area, " +
                "complain_city,property as  appeal_area,ordernum as crm_ordernum, " +
                "terminal_description, td_supported, analysis_condition," +
                " to_char(sendtime,'yyyy-mm-dd hh24:mi:ss') as customer_dispatch_time, " +
                "demarcation_analysis_results,to_char(cch_deal_time,'yyyy-mm-dd hh24:mi:ss') as cch_sys_delimit_time," +
                "complain_suggestion1, complain_suggestion2, complain_suggestion3, " +
                "complain_suggestion4, complain_suggestion5 " +
                "from p_lte_parmeter_complaint )b where a.crm_ordernum=b.crm_ordernum";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
//            转换为JSON对象
//            JSONObject jsonObj = new JSONObject();
            //转换为JSON集合
            JSONArray  array = new JSONArray () ;
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject(new LinkedHashMap());

                // 遍历ResultSet中的每条数据
                String city=rs.getString("city");
                String region = rs.getString("region");
                String community_id = rs.getString("community_id");
                String community_name = rs.getString("community_name");
                String poi_tag = rs.getString("poi_tag");
                String community_dis = rs.getString("community_dis");
                String ue_x = rs.getString("ue_x");
                String ue_y = rs.getString("ue_y");
                String order_name = rs.getString("order_name");
                String order_theme = rs.getString("order_theme");
                String order_time = rs.getString("order_time");
                String accept_time_limit = rs.getString("accept_time_limit");
                String complain_time = rs.getString("complain_time");
                String customer_name = rs.getString("customer_name");
                String customer_type = rs.getString("customer_type");
                String customer_tel = rs.getString("customer_tel");
                String complain_area = rs.getString("complain_area");
                String complain_content = rs.getString("complain_content");
                String large_area_complain = rs.getString("large_area_complain");
                String repeated_complaints = rs.getString("repeated_complaints");
                String customer_level = rs.getString("customer_level");
                String home_subscriber = rs.getString("home_subscriber");
                String complaint_acceptance_province = rs.getString("complaint_acceptance_province");
                String fault_msisdn = rs.getString("fault_msisdn");
                String fault_area = rs.getString("fault_area");
                String complain_city = rs.getString("complain_city");
                String appeal_area = rs.getString("appeal_area");
                String crm_ordernum = rs.getString("crm_ordernum");
                String terminal_description = rs.getString("terminal_description");
                String td_supported = rs.getString("td_supported");
                String analysis_condition = rs.getString("analysis_condition");
                String customer_dispatch_time = rs.getString("customer_dispatch_time");
                String demarcation_analysis_results = rs.getString("demarcation_analysis_results");
                String cch_sys_delimit_time = rs.getString("cch_sys_delimit_time");
                String complain_suggestion1 = rs.getString("complain_suggestion1");
                String complain_suggestion2 = rs.getString("complain_suggestion2");
                String complain_suggestion3 = rs.getString("complain_suggestion3");
                String complain_suggestion4 = rs.getString("complain_suggestion4");
                String complain_suggestion5 = rs.getString("complain_suggestion5");
                String cgi = rs.getString("cgi");
                String cgi1 = rs.getString("cgi1");
                String analysis_result = rs.getString("analysis_result");
                //放到JSON对象里面
                jsonObj.put("order_name", order_name);
                jsonObj.put("order_theme", order_theme);
                jsonObj.put("order_time", order_time);
                jsonObj.put("accept_time_limit", accept_time_limit);
                jsonObj.put("city", city);
                jsonObj.put("region", region);
                jsonObj.put("community_id", community_id);
                jsonObj.put("community_name", community_name);
                jsonObj.put("poi_tag", poi_tag);
                jsonObj.put("community_dis", community_dis);
                jsonObj.put("ue_x", ue_x);
                jsonObj.put("ue_y", ue_y);
                jsonObj.put("complain_time", complain_time);
                jsonObj.put("customer_name", customer_name);
                jsonObj.put("customer_type", customer_type);
                jsonObj.put("customer_tel", customer_tel);
                jsonObj.put("complain_area", complain_area);
                jsonObj.put("complain_content", complain_content);
                jsonObj.put("large_area_complain", large_area_complain);
                jsonObj.put("repeated_complaints", repeated_complaints);
                jsonObj.put("customer_level", customer_level);
                jsonObj.put("home_subscriber", home_subscriber);
                jsonObj.put("complaint_acceptance_province", complaint_acceptance_province);
                jsonObj.put("fault_msisdn", fault_msisdn);
                jsonObj.put("fault_area", fault_area);
                jsonObj.put("complain_city", complain_city);
                jsonObj.put("appeal_area", appeal_area);
                jsonObj.put("crm_ordernum", crm_ordernum);
                jsonObj.put("terminal_description", terminal_description);
                jsonObj.put("td_supported", td_supported);
                jsonObj.put("analysis_condition", analysis_condition);
                jsonObj.put("customer_dispatch_time", customer_dispatch_time);
                jsonObj.put("demarcation_analysis_results", demarcation_analysis_results);
                jsonObj.put("cch_sys_delimit_time", cch_sys_delimit_time);
                jsonObj.put("complain_suggestion1", complain_suggestion1);
                jsonObj.put("complain_suggestion2", complain_suggestion2);
                jsonObj.put("complain_suggestion3", complain_suggestion3);
                jsonObj.put("complain_suggestion4", complain_suggestion4);
                jsonObj.put("complain_suggestion5", complain_suggestion5);
                if(cgi!=null){
                    jsonObj.put("cgi","460-00-"+cgi);
                }
                else{
                    jsonObj.put("cgi",cgi);
                }
//最近小区补充
                if(cgi1!=null){
                    jsonObj.put("cgi1","460-00-"+cgi1);
                }
                else{
                    jsonObj.put("cgi1",cgi1);
                }
                jsonObj.put("analysis_result", analysis_result);
                array.add(jsonObj);
            }

            return array;

        } catch (SQLException e) {
            log.error("******************封装预分析结果数据错误：",e);
            e.printStackTrace();
        } finally {
            jdbcUtil.release(conn, pstmt, rs);
        }
        return null;

    }

    //大数据平台查询数据并入库回传到COC131
    public static void NsnDataPickup(String ResponseString){
        //关键值提取,先创建JSONObject对象指向需要解析的JSON报文
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(ResponseString.trim());
        String message=jsonObject.getString("message");
        String result=jsonObject.getString("result");
        //只保存最终需要的解析字段
        String ResponseStringRestore=jsonObject.get("data").toString();
        net.sf.json.JSONObject DataRestore = net.sf.json.JSONObject.fromObject(ResponseStringRestore.trim());
        String orderNo=DataRestore.getString("orderNo");
        String procInstId=DataRestore.getString("procInstId");
        String orderName=DataRestore.getString("orderName");
        String crmOrderNum=DataRestore.getJSONObject("formBasicData").getString("crm_ordernum");
        String city=DataRestore.getString("city");
        String region=DataRestore.getString("region");
        //把诺西回传结果提取以后入库数据库
        String sql="INSERT INTO nsn_send_back (orderno,procinstid,emos_ordernum,crm_ordernum,city,region,message,results) VALUES " +
                "("+"'"+orderNo+"','"+procInstId+"','"+orderName+"','"+crmOrderNum+"','"+city+"','"+region+"','"+message+"','"+result+"')";
        Connection conn = jdbcUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //执行sql
        try{
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
//            System.out.println("大数据平台推送数据插入底层数据库成功");
        } catch (SQLException e) {
            log.error("******************存储大数据平台返回结果 错误：",e);
            e.printStackTrace();
        } finally {
            jdbcUtil.release(conn,pstmt,rs);
        }
    }
    //大数据平台返回数据接收到一个集合里面并且返回到一个JSONobject里面来

    public static String NsnSecondSendData(String ResponseString,JSONObject attachUrl){
        try {
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(ResponseString.trim());
            //data
            String ResponseStringRestore=jsonObject.get("data").toString();
            net.sf.json.JSONObject DataRestore = net.sf.json.JSONObject.fromObject(ResponseStringRestore.trim());
//            log.info(new jsonFormatUtil().formatJson(DataRestore.toString()));
            //创建一个JsonObject保存第二次要传送的数据
            String orderNo=DataRestore.getString("orderNo");
            String procInstId=DataRestore.getString("procInstId");
            String form_ins_main=DataRestore.getString("formBasicData");

            //关键字段传入到json对象并返回
            JSONObject jo=new JSONObject(new LinkedHashMap());
            JSONArray ja=new JSONArray();
            JSONArray form_ins_sub = new JSONArray() ;
            JSONObject form_his = new JSONObject(new LinkedHashMap());
            form_his.put("create_time","");
            form_his.put("result","提交");
            form_his.put("comment","");
            form_his.put("sequence_name","提交");
            form_his.put("sequence_code","0");
//        JSONObject form_attachments = new JSONObject(new LinkedHashMap()) ;
//            form_attachments.put("name","");
//            form_attachments.put("url","");
//            form_attachments.put("create_time","");
//            form_attachments.put("file_size","");
//        ja.add(0,form_attachments);
            jo.put("orderNo",orderNo);
            jo.put("procInstId",procInstId);
            jo.put("nodeId","jt54c741580cfd44d880256e8104bd8a43");
            jo.put("nodeName","EMOS工单生成");
            jo.put("userId","4079");
            jo.put("userName","肖体俊");
            jo.put("form_ins_main",form_ins_main);
            jo.put("form_ins_sub",form_ins_sub);
            jo.put("form_his",form_his);

            if(attachUrl!=null) {
                ja.add(0, attachUrl);
                jo.put("form_attachments", ja);
            }else{
            jo.put("form_attachments",ja.clone());}

            //更新称为最终第二次回传大数据平台的json报文
            String jsoninfo_tmp=StringEscapeUtils.unescapeJavaScript(jo.toJSONString());
            String nsnData_tmp1=jsoninfo_tmp.replace("\"{","{");
            String nsnDataSecSend=nsnData_tmp1.replace("}\"","}");
            jsonFormatUtil jfu=new jsonFormatUtil();
            log.info("第二次封装发送到大数据平台JSON"+jfu.formatJson(nsnDataSecSend));
            return nsnDataSecSend;
        }
        catch (Exception e) {
          e.printStackTrace();
          log.error("*************大数据平台返回数据封装错误：",e);
          return null;
        }

    }

}
