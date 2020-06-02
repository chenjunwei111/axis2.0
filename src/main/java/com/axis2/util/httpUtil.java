package com.axis2.util;

import com.axis2.impl.NsnServiceImpl;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class httpUtil {
    private static final Logger log = Logger.getLogger(NsnServiceImpl.class.getClass());
    /**
     * 发送Http post请求
     *
     * @param xmlInfo
     *            json转化成的字符串
     * @param URL
     *            请求url
     * @return 返回信息
     */
    public static String doHttpPost(String xmlInfo, String URL) {
        System.out.println("发起的数据:" + xmlInfo);
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
                System.out.println("返回空");
            }
            System.out.println("返回数据为:" + ResponseString);
            //数据入库到131
            System.out.println("数据开始入库到131");
            new OracleUtil().NsnDataPickup(ResponseString);
            System.out.println("数据入库COC131完成");

            //大数据平台需要返回的字段保存到集合
            System.out.println("开始二次透传到大数据平台-推送报文制作");
            String nsnDataSecSend=new OracleUtil().NsnSecondSendData(ResponseString);
            System.out.println("开始二次透传到大数据平台-开始推送报文");
            String urlSec="http://10.174.240.17:8082/ease-flow-console-v2/api/open/flow/taskCompleteNotify";
            SecondSendNsn.secDoHttpPost(nsnDataSecSend,urlSec);
            return ResponseString;

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            try {
                out.close();
                instr.close();

            } catch (Exception ex) {
                return "0";
            }
        }
    }
}
