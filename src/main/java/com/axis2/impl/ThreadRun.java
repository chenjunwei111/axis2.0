package com.axis2.impl;

import com.axis2.ftp.Connection;
import com.axis2.ftp.XFtp;
import com.axis2.pojo.BaiduPoint;
import com.axis2.pojo.WebDeCoding;
import com.axis2.util.*;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* Description 线程类
* @param
* @Author junwei
* @Date 17:10 2020/6/2
**/
public class ThreadRun  extends Thread {

    private static final Logger log = Logger.getLogger(EomsServiceImpl.class.getClass());
    private static final Logger log2 = Logger.getLogger("FILE2");

    String sendSheet;
    String chhUrl;


    ThreadRun(String sendSheet,String chhUrl){
        this.sendSheet=sendSheet;
        this.chhUrl=chhUrl;
    }


    @Override
    public void run(){
        getComMsg(sendSheet,chhUrl);
    }

    /**
    * Description
    * @param sendSheet 工单报文
    * @param chhUrl CHH报文
    * @Author junwei
    * @Date 10:24 2020/4/28
    **/
    public String getComMsg(String sendSheet, String chhUrl) {
        try {
            if(sendSheet!=null){
                getEmosComMsg(sendSheet);
            }
            if(chhUrl!=null){
                getFtpMsg(chhUrl.replace("\n", ""),null);
            }
            return "0";
        } catch (Exception e) {
            log.error("*******************************报文错误：\n", e);
            return "error";
        }
    }


    public void getEmosComMsg(String sendSheet){
       try {
           //解析报文
           List<Map<String, Object>> listTotal = XmlUtil.xmlElementsList(sendSheet.replace("\n", ""));
           log.info("报文解析开始。。。。。");
           String resposeMsg = "";
           if (listTotal.size() == 0) {
               resposeMsg = "Message Exception";
               log.error("***********************报文异常，无法正常解析***********************");
               return ;
           }


           //插入日志记录
           String insertLogSql = " INSERT INTO AXIS_MESSAGE_LOG (MESSAGE_TYPE,CITY,VERSION_DATE,MESSAGE_ID,ACCEPT_TIME,MESSAGE_DETAIL ) " +
                   "values(?,?,to_date(?,'yyyy-mm-dd'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?) ";

           LinkedList tempList = new LinkedList<>();

           String insertComSql = "insert into P_LTE_PARMETER_COMPLAINT(" +
                   "CITY_CODE,CITY,VERSION_DATE,ORDERNUM,BUSINESSTYPE,ORDER_BENCH," +
                   "PHONENUM,COMPLAINTTYPE2,COMPLIANTPLACE,BUSSINESSCONTENT,SENDTIME," +
                   "PROPERTY,LONGITUDE,LATITUDE,UE_X,UE_Y,UE_ADDRESS,UE_PROPERTY,UE_DATE,COMPLAINT_LEVEL,STATE,ORDER_SOURCE," +
                   "COMPLIANT_LOCATE_TYPE,VOICE_DATA_CLASSIFY,BUSINESS_CLASSIFY,PROBLEM_CLASSIFY,PROBLEM_DETAIL," +
                   "ORDER_ACCEPT_LIMIT_TIME,CCH_DEAL_TIME,LOCATION_SUGGEST_ONE,IF_BIG_AREA_COMPLAINT,EOMS_ORDERNUM,FOLLOWER," +
                   "CUSTOMER_NAME,TERMINAL_DESCRIPTION,TD_SUPPORTED,ANALYSIS_CONDITION,HOME_SUBSCRIBER,COMPLAIN_AREA," +
                   "COMPLAIN_SUGGESTION1,COMPLAIN_SUGGESTION2,COMPLAIN_SUGGESTION3,COMPLAIN_SUGGESTION4,COMPLAIN_SUGGESTION5," +
                   "FAULT_MSISDN,COMPLAIN_CITY ,DEMARCATION_ANALYSIS_RESULTS,COMPLAINT_ACCEPTANCE_PROVINCE," +
                   "REPEATED_COMPLAINTS,CUSTOMER_TYPE)" +
                   " values(" +
                   "?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?," +//6
                   "?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss')," +//5
                   "?,?,?,?,?,?,?,?,?,?,?," +//11
                   "?,?,?,?,?," +//5
                   "to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,?,?," +//6
                   "?,?,?,?,?,?, " +//6
                   "?,?,?,?,?," +//5
                   "?,?,?,?,?,?)";//6

           LinkedList tempList2 = new LinkedList<>();
           log.info("遍历报文。。。。。");

           for (Map<String, Object> map : listTotal) {
               //       获取时间
               SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               String time = sf.format(new Date());

               String orderType = "工单";
               //地市
               String city = map.get("mainComplaintPlace") == null ? null : map.get("mainComplaintPlace").toString();
               //报文ID（工单号）
               String msgId = map.get("crmSheetId") == null ? null : map.get("crmSheetId").toString();


               //检查工单是否存在
               String checkSql = "select ordernum from P_LTE_PARMETER_COMPLAINT where ordernum='" + msgId + "'";

               ResultSet rs = JdbcServer.executeStatement(checkSql);

               while (rs.next()) {
                   if (rs.getString(1) == null) {
                       log.info("报文工单不存在：" + msgId + ",执行下一步");
                   } else {
                       log.info("报文工单已存在：" + msgId + ",终止执行下一步");
                       orderType = "重复工单";
                   }
               }

               LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
               map1.put("MESSAGE_TYPE", orderType);
               map1.put("CITY", city);
               map1.put("VERSION_DATE", time.substring(0, 10));
               map1.put("MESSAGE_ID", msgId);
               map1.put("ACCEPT_TIME", time);
               map1.put("MESSAGE_DETAIL", map.toString());
               tempList.add(map1);

               if ("重复工单".equals(orderType)) {
                   resposeMsg += msgId + " order repeat \n";
                   continue;
               }



               String cityCode = null;
               String checkCityCode = "select CITY_CODE from department_level where CITY_NAME='" + city + "' and rownum=1  ";
               ResultSet rs2 = JdbcServer.executeStatement(checkCityCode);
               while (rs2.next()) {
                   cityCode = rs2.getString(1);
               }

               String lat = null;
               String lng = null;

               String sAddree=null;
               String sProperty=null;
               log.info("开始获取经纬度。。。。");
               List<WebDeCoding> list = MapUtil.getBaiDuPoint(map.get("faultSite").toString(), city, null);

               if (list != null && list.size() != 0 && list.get(0).getPoint() != null) {
                   WebDeCoding web = list.get(0);
                   BaiduPoint point = PositionUtil.bd09_To_BaiduPoint84(web.getPoint().getLat(), web.getPoint().getLng());
                   lat = String.valueOf(point.getLat());
                   lng = String.valueOf(point.getLng());
                   sAddree=web.getAddr();

                   //获取经纬度
                   String checkProprtysql = "select DEPT_NAME From department\n" +
                           "where instr('"+web.getArea()+"',REGION_NAME)>0\n" +
                           "and PARENT_DEPT_CODE!='YUNNAN' ";
                   ResultSet rs3 = JdbcServer.executeStatement(checkProprtysql);
                   while (rs3.next()) {
                       sProperty = rs3.getString(1);
                   }

               } else {
                   log.info("无法获取经纬度");
               }

               LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
               //6
               map2.put("CITY_CODE", cityCode);
               map2.put("CITY", city);
               map2.put("VERSION_DATE", map.get("sendTime") == null ? null : map.get("sendTime").toString().substring(0, 19));
               map2.put("ORDERNUM", map.get("crmSheetId") == null ? null : map.get("crmSheetId"));
               map2.put("BUSINESSTYPE", map.get("title") == null ? null : map.get("title"));

               //20200317 jw新增5G判断工单
               Object tilte=map2.get("BUSINESSTYPE");

               String order_bench="";

               if(tilte!=null  ){
                   String busType2=tilte.toString().toUpperCase();
                   if( busType2.indexOf("4G")>=0 || busType2.indexOf("VOLTE")>=0 || busType2.indexOf("CPE")>=0 ||busType2.indexOf("手机上网")>=0 ||
                           busType2.indexOf("H5")>=0 ||busType2.indexOf("语音通话")>=0 ||busType2.indexOf("跨网协同")>=0 ||busType2.indexOf("已预告警类网络故障")>=0 ||
                           busType2.indexOf("升级投诉组专用")>=0 ){
                       order_bench="LTE投诉";
                   }
                   else if(busType2.indexOf("5G")>=0){
                       order_bench="5G投诉";
                   }else if (busType2.indexOf("2、3G")>=0 || busType2.indexOf("2G")>=0 || busType2.indexOf("GPRS")>=0){
                       order_bench="2G投诉";
                   }
               }
               map2.put("ORDER_BENCH",order_bench);


               //5
               map2.put("PHONENUM", map.get("customPhone") == null ? null : map.get("customPhone"));
               map2.put("COMPLAINTTYPE2", map.get("complaintType6") == null ? null : map.get("complaintType6"));
               map2.put("COMPLIANTPLACE", map.get("faultSite") == null ? null : map.get("faultSite"));
               map2.put("BUSSINESSCONTENT", map.get("complaintDesc") == null ? null : map.get("complaintDesc"));
               map2.put("SENDTIME", map.get("mainCustomerSerTime") == null ? null : map.get("mainCustomerSerTime").toString().substring(0, 19));

               //5
               map2.put("PROPERTY", map.get("mainPlaceCountry") == null ? null : map.get("mainPlaceCountry"));
               map2.put("LONGITUDE",lng);
               map2.put("LATITUDE", lat);
               map2.put("UE_X", lng);
               map2.put("UE_Y", lat);
               //5
               map2.put("UE_ADDRESS",sAddree);
               map2.put("UE_PROPERTY",sProperty);
               map2.put("UE_DATE",map.get("complaintTime")==null?null:map.get("complaintTime").toString());
               map2.put("COMPLAINT_LEVEL", map.get("customLevel") == null ? null : getLevel(map.get("customLevel").toString()));
               map2.put("STATE", "1");
               //2
               map2.put("ORDER_SOURCE", "4");
               map2.put("COMPLIANT_LOCATE_TYPE", "3");

               //       拆分业务类型字段
               String[] busType = map.get("title").toString().split("→");

               log.info("业务类型：" + map.get("title").toString());
               String voice1;
               if (busType.length < 5) {
                   voice1 = busType[busType.length - 1];//如果第5位没有，则拿最后一个做
               } else {
                   voice1 = busType[5];
               }

               String voice2;
               if (voice1.indexOf("通") != -1) {
                   voice2 = "语音通信";
               } else if (voice1 == "信号不稳定" || voice1.indexOf("区域") != -1) {
                   voice2 = "基础通信";
               } else {
                   voice2 = "数据通信";
               }
               //5
               map2.put("VOICE_DATA_CLASSIFY", voice2);
               map2.put("BUSINESS_CLASSIFY", busType.length>2 ? busType[2] : null);
               map2.put("PROBLEM_CLASSIFY", busType.length>3  ? busType[3] :null );
               map2.put("PROBLEM_DETAIL", busType.length < 5 ? null : busType[busType.length - 1]);
               map2.put("ORDER_ACCEPT_LIMIT_TIME", getTrueVal(map.get("sheetCompleteLimit")) == null ? null : map.get("sheetCompleteLimit").toString().substring(0, 19));

               //5
               map2.put("CCH_DEAL_TIME", getTrueVal(map.get("mainFaultCchTime"))  == null ? null : map.get("mainFaultCchTime").toString().substring(0, 19));
               map2.put("LOCATION_SUGGEST_ONE", map.get("mainFaultCchTypeOne") == null ? null : map.get("mainFaultCchTypeOne"));
               map2.put("IF_BIG_AREA_COMPLAINT", map.get("isWideComplaint") == null ? null : map.get("isWideComplaint"));
               map2.put("EOMS_ORDERNUM", map.get("sheetId") == null ? null : map.get("sheetId"));
               map2.put("FOLLOWER", map.get("operateUser") == null ? null : map.get("operateUser"));


               //新增字段20200602
               //6
               map2.put("CUSTOMER_NAME", map.get("customerName")==null?null:map.get("customerName").toString());
               map2.put("TERMINAL_DESCRIPTION", map.get("terminalType")==null?null:map.get("terminalType").toString());
               map2.put("TD_SUPPORTED", map.get("mainIfTD")==null?null:map.get("mainIfTD").toString());
               map2.put("ANALYSIS_CONDITION", map.get("preDealResult")==null?null:map.get("preDealResult").toString());
               map2.put("HOME_SUBSCRIBER", map.get("customAttribution")==null?null:map.get("customAttribution").toString());
               map2.put("COMPLAIN_AREA",  getTrueVal(map.get("complaintAdd")));

               //5
               map2.put("COMPLAIN_SUGGESTION1", map.get("mainFaultCchTypeOne")==null?null:map.get("mainFaultCchTypeOne").toString());
               map2.put("COMPLAIN_SUGGESTION2", map.get("mainFaultCchTypeTwo")==null?null:map.get("mainFaultCchTypeTwo").toString());
               map2.put("COMPLAIN_SUGGESTION3", map.get("mainFaultCchTypeThree")==null?null:map.get("mainFaultCchTypeThree").toString());
               map2.put("COMPLAIN_SUGGESTION4", map.get("mainFaultCchTypeFour")==null?null:map.get("mainFaultCchTypeFour").toString());
               map2.put("COMPLAIN_SUGGESTION5", map.get("mainFaultCchTypeFive")==null?null:map.get("mainFaultCchTypeFive").toString());

               //故障号码
               map2.put("FAULT_MSISDN", map.get("complaintNum")==null?null:map.get("complaintNum").toString());
               //申告地
               map2.put("COMPLAIN_CITY",city);
               //故障定界分析成败结果
               map2.put("DEMARCATION_ANALYSIS_RESULTS", map.get("mainFaultCchResult")==null?null:map.get("mainFaultCchResult").toString());
               //投诉处理省份
               map2.put("COMPLAINT_ACCEPTANCE_PROVINCE", map.get("startDealCity")==null?null:map.get("startDealCity").toString());
               //重复头次数
               map2.put("REPEATED_COMPLAINTS",  getTrueVal(map.get("repeatComplaintTimes"))==null?null:map.get("repeatComplaintTimes").toString());
               //客户类型(20200720新增)
               map2.put("CUSTOMER_TYPE",  getTrueVal(map.get("mainReserveTwo"))==null?null:map.get("mainReserveTwo").toString());
               tempList2.add(map2);

//               System.out.println(map2);
               log.info("插入字段：" + map2);

               resposeMsg += "Message(" + msgId + ") Accpet Success \n";
           }

           boolean res1 = JdbcServer.inserts(insertLogSql, tempList);
           boolean res2 = JdbcServer.inserts(insertComSql, tempList2);

           log.info(resposeMsg);
       }
       catch (Exception e) {
        log.error("*******************************工单报文接收错误：",e);
       }
    }


    /**
    * Description 过滤为空的数据
    * @param
    * @Author junwei
    * @Date 11:04 2020/6/3
    **/
    public String getTrueVal(Object object){
        if(object!=null){
            if(object.toString().length()!=0 && !object.toString().equals("") ){
                return object.toString();
            }
        }
        return  null;
    }

    public String getFtpMsg(String sendSheet, String orderNum) {
        XFtp ftp = null;
        Connection conn = null;
        log.info("开始解析ftp报文。。。。。");

        String saveFilePath = ConfigReaderUtils.getProperty("saveFilePath");
        try {
//            String user = ConfigReaderUtils.getProperty("ftpUser");
//            String pass = ConfigReaderUtils.getProperty("ftpPassWord");
            if (sendSheet == null || sendSheet.length() == 0) {
                log.info("ftp报文为空。。。。。不进行分析");
                return null;
            }
            List<Map<String, Object>> msgList = XmlUtil.xmlElementListFtp(sendSheet);
            log.info("解析ftp报文完成。。。。。");

            String resposeMsg = "";

            String insertLogSql = " INSERT INTO AXIS_MESSAGE_LOG (MESSAGE_TYPE,VERSION_DATE,MESSAGE_ID,ACCEPT_TIME,MESSAGE_DETAIL ) " +
                    "values(?,to_date(?,'yyyy-mm-dd'),?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),?) ";
            LinkedList tempList = new LinkedList<>();
            String insertChhSql = " insert into P_ACCEPT_CHH_INFO(" +
                    " VERSION_DATE,ORDERNUM,START_TIME,END_TIME,FLOW_TYPE" +
                    " ,FLOW_STATE,IMSI,MSISDN,IMEI,MS_IP " +
                    " ,ACCEPT_TYPE,ROUTE_CODE,SECTOR_ID,SECTOR_NAME,APN " +
                    " ,ENODEB_NAME,ENODEB_XDR_IP,MME_NAME,MME_IP ,TERMINAL_BRAND" +
                    " ,TERMINAL_MODEL,EMM_WORK,EMM_REASON,UE_ESM,UE_ESM_REASON" +
                    " ,NET_ESM_WORK,NET_ESM_REASON,S1AP_WORK,S1AP_REASON ,IDENTITY_REASON" +
                    " ,AUTHENTICATION_REASON,SECURITY_MODE_REASON) " +
                    "values (" +
                    "to_date(?,'yyyy-MM-dd'),?,to_date(?,'yyyy-MM-dd hh24:mi:ss'),to_date(?,'yyyy-MM-dd hh24:mi:ss'),?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,? )";

            LinkedList tempList2 = new LinkedList<>();
            for (Map<String, Object> map : msgList) {
                //       获取时间
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sf.format(new Date());
                String fileName = map.get("attachName").toString();
                String msgId = map.get("attachName") == null ? null : map.get("attachName").toString().replaceAll(".zip", "");
                LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
                map1.put("MESSAGE_TYPE", "FTP报文");
                map1.put("VERSION_DATE", time.substring(0, 10));
                map1.put("MESSAGE_ID", msgId);
                map1.put("ACCEPT_TIME", time);
                map1.put("MESSAGE_DETAIL", map.toString());
                tempList.add(map1);
                resposeMsg += "Ftp-Message (" + msgId + ")  Accepet Success \n";
                String path = map.get("attachURL").toString().replace("ftp://", "");
                String ftpHost = path.substring(0, path.indexOf("/"));
                String pathArr[] = ftpHost.split("_");
                //IP地址
                ftpHost = pathArr[0];
                //帐号
                String account = pathArr[1];
                //密码
                String pass = pathArr[2];
                log.info("连接FTP。。。。。");
                log.info("IP:" + ftpHost);
                log.info("帐号：" + account);
                log.info("密码：" + pass);

                //连接数据库
                ftp = new XFtp(ftpHost, account, pass, 21);
                conn = ftp.createConnection();
                conn.openxFtpChannel(Connection.xftpChannel.FTP);
                boolean res = conn.connectServer();
                log.info("连接FTP完成。。(" + res + ")");

                //需要去掉IP和文件名
                String filePath = path.substring(path.indexOf("/"), path.lastIndexOf("/") + 1);
                log.info("获取文件地址：" + filePath);
                Boolean b = ftp.receiveFile(filePath, fileName, saveFilePath);
                log.info("下载FTP文件。。(" + b + ")");

                if (!b) {
                    resposeMsg += fileName + " FTP文件不存在，地址：" + path + "\n ";
                    continue;
                }
                //关闭
                conn.disConnectServer();

                ArrayList<String> rstfile = CompressionUtil.extractBigZipFiles(saveFilePath + fileName);
                log.info("解压本地FTP文件。。" + rstfile);
                ArrayList<List<Object>> listTotal = new ArrayList<>();
                for (String str : rstfile) {
                    log.info("读取CSV文件数据。。");
                    ArrayList<List<Object>> listArr = CsvUtils.getCsvData(str);
                    if (listArr.size() != 0) {
                        log.info("文件：" + str + "有：" + listArr.size() + "条数据");
                        listTotal.addAll(listArr);
                    }
                }
                log.info("遍历完成，开始入库。。。。。");

                JdbcServer.createDbLinkDatePartition("P_ACCEPT_CHH_INFO", time.substring(0, 10), 0);
                log.info("P_ACCEPT_CHH_INFO （" + time.substring(0, 10) + "）分区创建成功。。。。。");

                for (int i = 0; i < listTotal.size(); i++) {
                    //前两行为表头，去掉
                    Map<String, Object> map2 = new LinkedHashMap<>();

                    map2.put("VERSION_DATE", time.substring(0, 10));
                    map2.put("ORDERNUM", orderNum==null?null:orderNum);
                    map2.put("START_TIME", listTotal.get(i).get(1) == null ? null : listTotal.get(i).get(1).toString().substring(0, 19));
                    map2.put("END_TIME", listTotal.get(i).get(2) == null ? null : listTotal.get(i).get(2).toString().substring(0, 19));
                    map2.put("FLOW_TYPE", listTotal.get(i).get(3) == null ? null : listTotal.get(i).get(3));
                    map2.put("FLOW_STATE", listTotal.get(i).get(4) == null ? null : listTotal.get(i).get(4));
                    map2.put("IMSI", listTotal.get(i).get(5) == null ? null : listTotal.get(i).get(5));

                    map2.put("MSISDN", listTotal.get(i).get(6) == null ? null : listTotal.get(i).get(6));
                    map2.put("IMEI", listTotal.get(i).get(7) == null ? null : listTotal.get(i).get(7));
                    map2.put("MS_IP", listTotal.get(i).get(8) == null ? null : listTotal.get(i).get(8));
                    map2.put("ACCEPT_TYPE", listTotal.get(i).get(9) == null ? null : listTotal.get(i).get(9));
                    map2.put("ROUTE_CODE", listTotal.get(i).get(10) == null ? null : listTotal.get(i).get(10));

                    map2.put("SECTOR_ID", listTotal.get(i).size() <= 11 ? null : listTotal.get(i).get(11));
                    map2.put("SECTOR_NAME", listTotal.get(i).size() <= 12 ? null : listTotal.get(i).get(12));
                    map2.put("APN", listTotal.get(i).size() <= 13 ? null : listTotal.get(i).get(13));
                    map2.put("ENODEB_NAME", listTotal.get(i).size() <= 14 ? null : listTotal.get(i).get(14));
                    map2.put("ENODEB_XDR_IP", listTotal.get(i).size() <= 15 ? null : listTotal.get(i).get(15));

                    map2.put("MME_NAME", listTotal.get(i).size() <= 16 ? null : listTotal.get(i).get(16));
                    map2.put("MME_IP", listTotal.get(i).size() <= 17 ? null : listTotal.get(i).get(17));
                    map2.put("TERMINAL_BRAND", listTotal.get(i).size() <= 18 ? null : listTotal.get(i).get(18));
                    map2.put("TERMINAL_MODEL", listTotal.get(i).size() <= 19 ? null : listTotal.get(i).get(19));
                    map2.put("EMM_WORK", listTotal.get(i).size() <= 20 ? null : listTotal.get(i).get(20));

                    map2.put("EMM_REASON", listTotal.get(i).size() <= 21 ? null : listTotal.get(i).get(21));
                    map2.put("UE_ESM", listTotal.get(i).size() <= 22 ? null : listTotal.get(i).get(22));
                    map2.put("UE_ESM_REASON", listTotal.get(i).size() <= 23 ? null : listTotal.get(i).get(23));
                    map2.put("NET_ESM_WORK", listTotal.get(i).size() <= 24 ? null : listTotal.get(i).get(24));
                    map2.put("NET_ESM_REASON", listTotal.get(i).size() <= 25 ? null : listTotal.get(i).get(25));

                    map2.put("S1AP_WORK", listTotal.get(i).size() <= 26 ? null : listTotal.get(i).get(26));
                    map2.put("S1AP_REASON", listTotal.get(i).size() <= 27 ? null : listTotal.get(i).get(27));
                    map2.put("IDENTITY_REASON", listTotal.get(i).size() <= 28 ? null : listTotal.get(i).get(28));
                    map2.put("AUTHENTICATION_REASON", listTotal.get(i).size() <= 29 ? null : listTotal.get(i).get(29));
                    map2.put("SECURITY_MODE_REASON", listTotal.get(i).size() <= 30 ? null : listTotal.get(i).get(30));

                    tempList2.add(map2);
                }
                log.info("数据遍历完成，数量（" + tempList2.size() + "）开始入库。。。。。");
                resposeMsg += fileName + " 文件入库完成 \n";

            }
             JdbcServer.inserts(insertLogSql, tempList);
             JdbcServer.inserts(insertChhSql, tempList2);
            log.info(resposeMsg);
            return resposeMsg;
        } catch (Exception e) {
            log.error("*******************************FTP报文错误：\n", e);
            return "error";
        } finally {
            if (conn != null) {
                conn.disConnectServer();
            }
            //最后删除文件夹内容
            UtilFile.deletefile(saveFilePath);
            log.info("任务完成，删除本地ftp文件");
        }
    }

    public String getLevel(String strLevle) {
        if (strLevle == null) {
            return "0";
        }
        Integer level = 0;
        if (strLevle.indexOf("零星") != -1) {
            level = 0;
        } else if (strLevle.indexOf("一星") != -1) {
            level = 1;
        } else if (strLevle.indexOf("二星") != -1) {
            level = 2;
        } else if (strLevle.indexOf("三星") != -1) {
            level = 3;
        } else if (strLevle.indexOf("四星") != -1) {
            level = 4;
        } else if (strLevle.indexOf("五星") != -1) {
            level = 5;
        } else if (strLevle.indexOf("六星") != -1) {
            level = 6;
        } else if (strLevle.indexOf("七星") != -1) {
            level = 7;
        } else if (strLevle.indexOf("八星") != -1) {
            level = 8;
        } else if (strLevle.indexOf("九星") != -1) {
            level = 9;
        } else if (strLevle.indexOf("十星") != -1) {
            level = 10;
        }
        return level.toString();
    }
}
