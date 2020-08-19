package com.axis2.service;

/**
* Description  Eoms 接口类
* @Author junwei
* @Date 16:35 2019/9/26
**/
 public interface EomsService {

    /**
     * Description 功能接口测试
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    public String testSpdb(String sendSheet,String s2);


    /**
    * Description 获取工单报文
    * @param sendSheet
    * @Author junwei
    * @Date 9:53 2019/9/27
    **/
    public String SheetInfoSer(String sendSheet,String chhUrl);


    /**
     * Description 获取工单报文
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
/*
    public String getComMsg(String sendSheet,String orderNum);
*/

    /**
     * Description 获取FTP报文
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
/*
    public String getFtpMsg(String sendSheet,String orderNum);
*/

    /**
     * Description 报文返回到益阳平台
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    public String turnAnalyReport(String sendSheet);


    /**
     * Description 报文返回到益阳平台
     * @param
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    public void turnAnalyReport2(String sendSheet);
}
