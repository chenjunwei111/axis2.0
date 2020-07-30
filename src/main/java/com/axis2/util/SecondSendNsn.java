package com.axis2.util;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
* Description 二次请求内容
* @param
* @Author 肖体俊
* @Date 14:41 2020/6/2
**/
public class SecondSendNsn {

    private static final Logger log = Logger.getLogger(SecondSendNsn.class.getClass());
    private static final Logger log2 = Logger.getLogger("FILE2");

    /**
     * 第二次发送Http post请求
     *
     */
    public static String secDoHttpPost(String nsnDataSecSend, String urlSec,Object orderNum,Object eomsNum,int sendNum) {
//        System.out.println("发起的数据:" + nsnDataSecSend);

        if(sendNum==1){
            log.info("二次失败后，开始再次请求");
        }

        byte[] xmlData = nsnDataSecSend.getBytes();
        InputStream instr = null;
//        java.io.ByteArrayOutputStream out = null;
        try {
            log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 开始二次连接请求1。。。。。。。");
            URL url = new URL(urlSec);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setRequestProperty("content-Type", "application/json; charset=utf-8");
            urlCon.setRequestProperty("Content-length",String.valueOf(xmlData.length));
            urlCon.setRequestProperty("X-Authorization-Ip", "10.173.128.22");
            urlCon.setRequestProperty("X-Authorization-Key", "W1L6-0Z6U-N8CY-7423-4721");


            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            instr = urlCon.getInputStream();
            if(instr==null){
             log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 接收返回数据异常。。。。。。。");
            }

            byte[] bis = IOUtils.toByteArray(instr);

            String ResponseString = new String(bis, "UTF-8");
            if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 二次透传无返回结果");
            }

            Boolean nextFlag=true;
            Map<String,Object> map= (Map<String, Object>) JSON.parse(ResponseString);
            if (map!=null && sendNum==0){
                String result=map.get("result").toString();
                if(result.equals("false")){
                    log.info(ResponseString+"二次透传请求返回失败，尝试第再次请求");
                    nextFlag=false;
                    secDoHttpPost(nsnDataSecSend,urlSec, orderNum, eomsNum,1);
                }
            }

            if(nextFlag){
                log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 二次透传完毕 返回数据为，内容为：\n"+ResponseString);
            }

            return ResponseString;

        } catch (Exception e) {
            log.error("*********************发送HTTP请求失败3.0",e);
            e.printStackTrace();
            return "0";
        } finally {
            try {
                if(instr!=null){
//                    out.close();
                    instr.close();
                }
            } catch (Exception ex) {
                log.error("*********************发送HTTP请求失败4.0",ex);
                return "0";
            }
        }
    }
}
