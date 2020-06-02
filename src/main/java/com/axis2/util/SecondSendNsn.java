package com.axis2.util;

import com.axis2.impl.NsnServiceImpl;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SecondSendNsn {

    private static final Logger log = Logger.getLogger(NsnServiceImpl.class.getClass());
    /**
     * 第二次发送Http post请求
     *
     */
    public static String secDoHttpPost(String nsnDataSecSend, String urlSec) {
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
                System.out.println("返回空");
            }
            System.out.println("二次透传完毕返回数据为:" + ResponseString);
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
