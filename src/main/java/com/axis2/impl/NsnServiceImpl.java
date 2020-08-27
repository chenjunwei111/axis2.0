package com.axis2.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.axis2.util.OracleUtil;
import com.axis2.util.SeqCheckPushUtil;
import com.axis2.util.httpUtil;
import com.axis2.util.jsonFormatUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
* Description
* @param
* @Author 肖体俊
* @Date 14:06 2020/6/2
**/
public class NsnServiceImpl {

    private static final Logger log = Logger.getLogger(NsnServiceImpl.class.getClass());
    private static final Logger log2 = Logger.getLogger("FILE2");

    public static String url = "http://10.174.240.17:8082/ease-flow-console-v2/api/open/form/basic/save?userId=1";


    /**
    * Description  核心代码
    * @param
    * @Author 肖体俊
    * @Date 15:03 2020/6/2
    **/
    public static  void  analyTurnToEoms(String sql){
        log.info("接收到预分析完成消息，开始数据回传EOMS。。。。。。");
        int orderId;
        //规整JSON格式
//        jsonFormatUtil jfu=new jsonFormatUtil();
        //按照大数据平台JSON对象要求格式进行回传,一单一单回传,先把所有需要回传工单实例化
        JSONArray complainArray=new OracleUtil().nsnTurnAnalyReport(sql);
        log.info("本次发送预分析完成工单："+complainArray.size()+"条");
        log.info("开始执行HTTP 发送数据到 EMOS ，\n" +
                " 概要日志//data1//spdbLogs//axisApp.log \n" +
                " 明细日志//data1//spdbLogs//axisAppDetail.log \n");

        for(orderId=0;orderId<complainArray.size();orderId++){
            //获取我们每单需要传送的JSON报文
            String nsnData=complainArray.getString(orderId);
//            log.info(new jsonFormatUtil().formatJson(nsnData));
            JSONObject obj = new JSONObject();
            obj.put("city",JSONObject.parseObject(nsnData).get("city").toString());
            obj.put("region",JSONObject.parseObject(nsnData).get("region").toString());
            obj.put("formBasicData",nsnData);
            obj.put("formMetaId", "97ece470edfd41eea6c18b9eb38926b7");
            obj.put("orderName",JSONObject.parseObject(nsnData).get("order_name").toString());
            obj.put("procDefKey", "complain10086_v1");

            //去除反斜杠进行最终结果输出
            String jsoninfo_tmp=StringEscapeUtils.unescapeJavaScript(obj.toJSONString());
            String jsoninfo_tmp1=jsoninfo_tmp.replace("\"{","{");
            //去除JSON子对象多余双引号以后的结果
            String jsoninfo=jsoninfo_tmp1.replace("}\"","}");
//            log.info(new jsonFormatUtil().formatJson(jsoninfo));
            //开始传送报文
            log.info(new jsonFormatUtil().formatJson(jsoninfo));
            Object orderNum= JSONObject.parseObject(nsnData).get("crm_ordernum");
            Object eomsNum= JSONObject.parseObject(nsnData).get("order_name");
            //开始等5分钟检测是不是SEQ推送的占用小区_20200824(SCORPIO)
            log.info("每个工单开始等待5分钟，先去SEQ查找对应的占用小区，如果有就做生成URL作为附件透传");
            //投诉号码
            Object customer_tel= JSONObject.parseObject(nsnData).get("customer_tel");
            //故障号码
            Object fault_msisdn= JSONObject.parseObject(nsnData).get("fault_msisdn");
            log.info("开始检查投诉号码对应的SEQ占用信息，准备生成csv文件");
            SeqCheckPushUtil.SeqCellCheck(orderNum,customer_tel,fault_msisdn);


            log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+"  数据封装完成，开始发送HTTP请求\n " +
                    "发送地址："+url);

            log2.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+"\n 回传内容："+jsoninfo);
            httpUtil.doHttpPost(jsoninfo,url,orderNum,eomsNum,1,5000);
        }

    }



}

