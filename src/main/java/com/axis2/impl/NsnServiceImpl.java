package com.axis2.impl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.axis2.util.OracleUtil;
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

    public static String url = "http://10.174.240.17:8082/ease-flow-console-v2/api/open/form/basic/save?userId=1";

    public static void main(String[] args) {
        //
        int orderId;
        //规整JSON格式
        jsonFormatUtil jfu=new jsonFormatUtil();
        //按照大数据平台JSON对象要求格式进行回传,一单一单回传,先把所有需要回传工单实例化
        JSONArray complainArray=new OracleUtil().nsnTurnAnalyReport();

        for(orderId=0;orderId<complainArray.size();orderId++){
            //获取我们每单需要传送的JSON报文
            String nsnData=complainArray.getString(orderId);
//            log.info(jfu.formatJson(nsnData));
            JSONObject obj = new JSONObject();
            obj.put("city",JSONObject.parseObject(nsnData).get("city").toString());
            obj.put("region",JSONObject.parseObject(nsnData).get("region").toString());
            obj.put("formBasicData",nsnData);
            obj.put("formMetaId", "97ece470edfd41eea6c18b9eb38926b7");
            obj.put("orderName",JSONObject.parseObject(nsnData).get("crm_ordernum").toString());
            obj.put("procDefKey", "complain10086_v1");

            //去除反斜杠进行最终结果输出
//            String jsoninfo_tmp=StringEscapeUtils.unescapeJavaScript(obj.toJSONString(obj));
            String jsoninfo_tmp=StringEscapeUtils.unescapeJavaScript(obj.toJSONString());
            String jsoninfo_tmp1=jsoninfo_tmp.replace("\"{","{");
            //去除JSON子对象多余双引号以后的结果
            String jsoninfo=jsoninfo_tmp1.replace("}\"","}");
            //开始传送报文
//            log.info(url);
//            httpUtil hu=new httpUtil();
            httpUtil.doHttpPost(jsoninfo,url);
        }
    }
}

