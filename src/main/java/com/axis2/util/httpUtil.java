package com.axis2.util;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


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
     * @param xmlInfo json转化成的字符串
     * @param URL     请求url
     * @param sendNum 请求次数，用于控制请求失败后，二次请求，首次为0
     * @return 返回信息
     */
    public static String doHttpPost(String xmlInfo, String URL, Object orderNum, Object eomsNum, int sendNum, long millis) {
//        System.out.println("发起的数据:" + xmlInfo);
        //针对大于6次就写入数据库
        if (sendNum > 6) {
            log.info("6次延时传送失败，写入数据库");
            String sql = "INSERT INTO nsn_response_failed_detail_table (xmlInfo,URL,orderNum,eomsNum,sendNum,millis,remarks) VALUES " + "(" + "'" + xmlInfo + "','" + URL + "','" + orderNum + "','" + eomsNum + "','" + sendNum + "','" + millis +"','" +"第一次透传6次以上失败" +"')";
            Connection conn = jdbcUtil.getConnection();
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            //执行sql
            try {
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
//            System.out.println("大数据平台推送数据插入底层数据库成功");
            } catch (SQLException e) {
                log.error("******************存储大数据平台返回结果 错误：", e);
                e.printStackTrace();
            } finally {
                jdbcUtil.release(conn, pstmt, rs);
            }
            return "0";
        }
        //如果小于六次开始重复回传
        else {
            log.info("工单号"+orderNum+":开始第" + sendNum + "次请求");
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
                urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
                urlCon.setRequestProperty("X-Authorization-Ip", "10.173.128.22");
                urlCon.setRequestProperty("X-Authorization-Key", "W1L6-0Z6U-N8CY-7423-4721");

//            System.out.println(String.valueOf(xmlData.length));
                DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
                printout.write(xmlData);
                printout.flush();
                printout.close();
                instr = urlCon.getInputStream();
                byte[] bis = IOUtils.toByteArray(instr);
                String ResponseString = new String(bis, "UTF-8");
                if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
                    log.info("投诉工单号:" + orderNum + "/EOMS工单号" + eomsNum + " 首次透传无返回结果");
                }
                //数据入库到131
                Boolean nextFlag = true;

                Map<String, Object> map = (Map<String, Object>) JSON.parse(ResponseString);
                if (map != null && sendNum <= 6) {
                    String result = map.get("result").toString();
                    if (result.equals("false")) {
                        log.info(ResponseString + "首次请求返回失败，尝试第" + sendNum + "次请求");
                        sendNum += 1;
                        nextFlag = false;
                        try {
                            Thread.currentThread().sleep(millis);
                            log.info(ResponseString + "尝试等待" + millis / 1000 + "S，发起请求");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            log2.info(e.toString());
                        }
                        millis += 5000;
                        doHttpPost(xmlInfo, URL, orderNum, eomsNum, sendNum, millis);
                    }
                }

                //返回正常的话，则继续执行
                if (nextFlag) {
                    log.info("\n投诉工单号:" + orderNum + "/EOMS工单号" + eomsNum + " 返回成功内容明细：\n" + ResponseString);
                    log2.info("投诉工单号:" + orderNum + "/EOMS工单号" + eomsNum + " 返回成功，数据入库，" + "内容明细：\n" + ResponseString);
                    log2.info("大数据平台开始回传结果入库到数据库留痕");

                    //到这里返回为空的话，会报错
                    OracleUtil.NsnDataPickup(ResponseString);

                    //大数据平台需要返回的字段保存到集合
                    log.info("投诉工单号:" + orderNum + "/EOMS工单号" + eomsNum + " 开始二次透传到大数据平台-推送报文制作");

                    String nsnDataSecSend = OracleUtil.NsnSecondSendData(ResponseString);
                    String urlSec = "http://10.174.240.17:8082/ease-flow-console-v2/api/open/flow/taskCompleteNotify";

                    log.info("投诉工单号:" + orderNum + "/EOMS工单号" + eomsNum + " 开始二次透传到大数据平台-开始推送报文 \n " + "请求地址：" + urlSec);
                    SecondSendNsn.secDoHttpPost(nsnDataSecSend, urlSec, orderNum, eomsNum, 1, 5000);
                }

                return ResponseString;

            } catch (Exception e) {
                log.error("***********************发送HTTP请求失败1.0", e);
                e.printStackTrace();
                return "0";
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        instr.close();
                    }
                } catch (Exception ex) {
                    log.error("***********************发送HTTP请求失败2.0", ex);
                    return "0";
                }
            }
        }
    }
}
