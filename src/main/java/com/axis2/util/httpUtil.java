package com.axis2.util;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
* Description http请求工具类
* @param
* @Author junwei
* @Date 14:32 2020/6/2
**/
public class httpUtil {

    private static final Logger log = Logger.getLogger(httpUtil.class.getClass());
    private static final Logger log2 = Logger.getLogger("FILE2");

    /**
     * 发送Http post请求
     *
     * @param xmlInfo
     *            json转化成的字符串
     * @param URL
     *            请求url
     * @return 返回信息
     */
    public static String doHttpPost(String xmlInfo, String URL,Object orderNum,Object eomsNum) {
//        System.out.println("发起的数据:" + xmlInfo);
        byte[] xmlData = xmlInfo.getBytes();
        InputStream instr = null;
        java.io.ByteArrayOutputStream out = null;
        try {
            URL url = new URL(URL);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setRequestProperty("content-Type", "application/json; charset=utf-8");
            urlCon.setRequestProperty("Content-length",String.valueOf(xmlData.length));
            urlCon.setRequestProperty("X-Authorization-Ip", "10.173.128.22");
            urlCon.setRequestProperty("X-Authorization-Key", "W1L6-0Z6U-N8CY-7423-4721");

            System.out.println(String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(
                    urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            instr = urlCon.getInputStream();
            byte[] bis = IOUtils.toByteArray(instr);
            String ResponseString = new String(bis, "UTF-8");
            if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 首次透传无返回结果");
            }
            //数据入库到131
            log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 返回成功");
            log2.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 返回成功，数据入库，" +
                    "内容明细：\n"+ResponseString);

            OracleUtil.NsnDataPickup(ResponseString);

            //大数据平台需要返回的字段保存到集合
//            System.out.println("开始二次透传到大数据平台-推送报文制作");
            log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 开始二次透传到大数据平台-推送报文制作");

            String nsnDataSecSend=OracleUtil.NsnSecondSendData(ResponseString);
//            System.out.println("开始二次透传到大数据平台-开始推送报文");
            String urlSec="http://10.174.240.17:8082/ease-flow-console-v2/api/open/flow/taskCompleteNotify";

            log.info("投诉工单号:"+orderNum+"/EOMS工单号"+eomsNum+" 开始二次透传到大数据平台-开始推送报文 \n " +
                    "请求地址："+urlSec);
            SecondSendNsn.secDoHttpPost(nsnDataSecSend,urlSec, orderNum, eomsNum);
            return ResponseString;

        } catch (Exception e) {
            log.error("***********************发送HTTP请求失败1.0",e);
            e.printStackTrace();
            return "0";
        } finally {
            try {
                out.close();
                instr.close();
            } catch (Exception ex) {
                log.error("***********************发送HTTP请求失败2.0",ex);
                return "0";
            }
        }
    }
}
