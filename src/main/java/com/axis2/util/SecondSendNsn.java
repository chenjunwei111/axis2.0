package com.axis2.util;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
    public static String secDoHttpPost(String nsnDataSecSend, String urlSec,Object orderNum,Object eomsNum) {
        System.out.println("发起的数据:" + nsnDataSecSend);
        byte[] xmlData = nsnDataSecSend.getBytes();
        InputStream instr = null;
        java.io.ByteArrayOutputStream out = null;
        try {
            URL url = new URL(urlSec);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setRequestProperty("content-Type", "application/json; charset=utf-8");
            urlCon.setRequestProperty("Content-length",String.valueOf(xmlData.length));
            urlCon.setRequestProperty("X-Authorization-Ip", "10.173.128.22");
            urlCon.setRequestProperty("X-Authorization-Key", "W1L6-0Z6U-N8CY-7423-4721");

            System.out.println(String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            instr = urlCon.getInputStream();
            byte[] bis = IOUtils.toByteArray(instr);
            String ResponseString = new String(bis, "UTF-8");
            if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 二次透传无返回结果");
            }
            log2.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 二次透传完毕返回数据为，内容为：\n"+ResponseString);

            return ResponseString;

        } catch (Exception e) {
            log.error("*********************发送HTTP请求失败3.0",e);
            e.printStackTrace();
            return "0";
        } finally {
            try {
                out.close();
                instr.close();

            } catch (Exception ex) {
                log.error("*********************发送HTTP请求失败3.0",ex);
                return "0";
            }
        }
    }
}
