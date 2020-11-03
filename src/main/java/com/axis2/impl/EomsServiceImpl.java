package com.axis2.impl;

import com.axis2.service.EomsService;
import com.axis2.util.ConfigReaderUtils;
import com.axis2.util.JdbcServer;
import com.axis2.util.Task;
import com.axis2.util.XmlUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description 实现类
 *
 * @Author junwei
 * @Date 16:36 2019/9/26
 **/
public class EomsServiceImpl implements EomsService , ServletContextListener {

    private static final Logger log = Logger.getLogger(EomsServiceImpl.class.getClass());
    private static final Logger log2 = Logger.getLogger("FILE2");
//正常的查询
    public static String sql="select  a.NSN_PARENT_ID city,a.NSN_ID region, b.order_name,a.UE_X,a.UE_Y,a.COMMUNITY_ID,a.COMMUNITY_NAME,a.POI_TAG,a.COMMUNITY_DIS,\n" + "b.order_theme, b.order_time,b.accept_time_limit,b.complain_time,b.customer_name,b.customer_type, b.customer_tel, \n" + "b.complain_area,b.complain_content,b.large_area_complain,b.repeated_complaints,b.customer_level, b.home_subscriber,\n" + "b.complaint_acceptance_province,b.fault_msisdn,b.fault_area,b.complain_city, a.PROPERTY appeal_area,a.crm_ordernum,\n" + "b.terminal_description,b.td_supported,b.analysis_condition, b.customer_dispatch_time,b.demarcation_analysis_results,\n" + "b.cch_sys_delimit_time,b.complain_suggestion1, b.complain_suggestion2,b.complain_suggestion3,b.complain_suggestion4,\n" + "b.complain_suggestion5,a.cgi,a.cgi1,a.analysis_result from (SELECT S1.CITY,S1.PROPERTY,S1.CRM_ORDERNUM,S1.ANALYSIS_RESULT, \n" + "S.SPDB_PROPERTY,S.NSN_PROPERTY,S.NSN_ID,S.NSN_PARENT_ID,S1.CGI1,S1.CGI,S1.UE_X,S1.UE_Y,S1.COMMUNITY_ID,S1.COMMUNITY_NAME,S1.POI_TAG,\n" + "S1.COMMUNITY_DIS FROM (select CITY,case when LTE_PROPERTY is null then GSM_PROPERTY  else LTE_PROPERTY END PROPERTY, CRM_ORDERNUM, \n" + "ANALYSIS_RESULT, CGI1, CGI, UE_X, UE_Y,COMMUNITY_ID,COMMUNITY_NAME,POI_TAG,COMMUNITY_DIS from (select a1.CITY,CASE WHEN a1.CITY='大理' and \n" + "a1.PROPERTY='大理古城区' THEN (case when a1.cgi is null then a3.area else a2.area end ) else a1.PROPERTY end LTE_PROPERTY,\n" + "CASE WHEN a1.CITY='大理' and a1.PROPERTY='大理古城区' THEN (case when a1.cgi is null then  a5.area else a4.area end ) else a1.PROPERTY end GSM_PROPERTY,\n" + "a1.CRM_ORDERNUM, a1.ANALYSIS_RESULT,a1.cgi1, a1.CGI,a1.UE_X,a1.UE_Y,a1.COMMUNITY_ID,a1.COMMUNITY_NAME,a1.POI_TAG,a1.COMMUNITY_DIS\n" + "from (select T.CITY,T.COMMUNITY_ID,T.COMMUNITY_NAME,T.POI_TAG,T.PROPERTY,F.工单流水号 crm_ordernum,F.备注1 analysis_result,  \n" + "F.LTE关键小区 cgi,substr(COMPARE_SECTOR,0,instr(T.COMPARE_SECTOR,'(')-1) cgi1,T.UE_X, T.UE_Y,T.COMMUNITY_DIS  from (\n" + "select a.CITY, a.COMMUNITY_ID,b.COMMUNITY_NAME,b.poi_tag, a.ORDERNUM, a.PHONENUM, a.PROPERTY, a.PROBLEM_CLASSIFY, a.BUSINESSTYPE,\n" + "a.BUSINESS_CLASSIFY,a.PROBLEM_DETAIL,a.COMPLIANTPLACE, a.UE_X, a.UE_Y, a.UE_ADDRESS, a.VOICE_DATA_CLASSIFY,a.BUSSINESSCONTENT,\n" + "a.COMPLAINT_TIME,a.ORDER_SOURCE, a.STATE,a.SEQ_CELL, a.SEQ_CELL_LONGITUDE, a.SEQ_CELL_LATITUDE,a.SENDTIME, a.BENCHMARK, \n" + "a.CITY_CODE,a.CITY_OR_RURAL, a.COMPARE_SECTOR, a.GRID_ID,b.COMMUNITY_DIS from\n" + "(select * from TEMP_CP_COMPLAIN_LIST_ORDER_V1 where ordernum not in(select distinct  CRM_ORDERNUM from nsn_send_back)) a\n" + " left join (SELECT p.ORDERNUM, p.COMMUNITY_ID,sc.COMMUNITY_NAME,sc.TAG poi_tag, case when p.COMMUNITY_DIS<=5 then  p.COMMUNITY_DIS else null end COMMUNITY_DIS\n" + "        FROM p_lte_parmeter_complaint_state p,p_poi_scene sc where p.community_id=sc.community_id) b\n" + "        on a.ORDERNUM=b.ORDERNUM )T,FINAL_ANALYSIS_TABLE_V2_1 F  WHERE T.ORDERNUM=F.工单流水号) a1 \n" + "         left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a2 on a1.cgi=a2.SECTOR_ID \n" + "         left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a3  on a1.CGI1=a3.sector_id  \n" + "         left join (select lac||'-'||ci as SECTOR_ID,area from p_gsm_sector_bak p) a4  \n" + "        on a1.cgi=a4.sector_id left join (select lac||'-'||ci as SECTOR_ID,area from p_gsm_sector_bak p) a5  \n" + "         on a1.CGI1=a5.sector_id)) S1,(SELECT SPDB_PROPERTY, NSN_PROPERTY, NSN_ID, NSN_PARENT_ID  \n" + "        FROM spdb_nsn_area_change) S WHERE S1.PROPERTY=S.SPDB_PROPERTY) a,(select eoms_ordernum as  order_name, \n" + "        businesstype as order_theme,  to_char(version_date,'yyyy-mm-dd hh24:mi:ss') as order_time, \n" + "        to_char(order_accept_limit_time,'yyyy-mm-dd hh24:mi:ss') as accept_time_limit,city, \n" + "        ue_property as region,ue_date as complain_time, customer_name,phonenum as customer_tel, \n" + "        complain_area,bussinesscontent as complain_content,if_big_area_complaint as large_area_complain, \n" + "        repeated_complaints,complaint_level as customer_level, home_subscriber,complaint_acceptance_province, \n" + "        fault_msisdn,compliantplace as fault_area, complain_city,property as appeal_area, \n" + "        ordernum as crm_ordernum,terminal_description, td_supported, analysis_condition, \n" + "        to_char(sendtime,'yyyy-mm-dd hh24:mi:ss') as customer_dispatch_time,demarcation_analysis_results, \n" + "        to_char(cch_deal_time,'yyyy-mm-dd hh24:mi:ss') as cch_sys_delimit_time,complain_suggestion1, \n" + "        complain_suggestion2, complain_suggestion3,complain_suggestion4, complain_suggestion5,customer_type \n" + "         from p_lte_parmeter_complaint )b where a.crm_ordernum=b.crm_ordernum";
//应急单机版执行的查询
    public static String sqltest="select  a.NSN_PARENT_ID city,a.NSN_ID region, b.order_name,a.UE_X,a.UE_Y,a.COMMUNITY_ID,a.COMMUNITY_NAME,a.COMMUNITY_DIS,  \n" + "b.order_theme, b.order_time,b.accept_time_limit,b.complain_time,b.customer_name,b.customer_type, b.customer_tel,   \n" + "b.complain_area,b.complain_content,b.large_area_complain,b.repeated_complaints,b.customer_level, b.home_subscriber,  \n" + "b.complaint_acceptance_province,b.fault_msisdn,b.fault_area,b.complain_city, a.PROPERTY appeal_area,a.crm_ordernum,  \n" + "b.terminal_description,b.td_supported,b.analysis_condition, b.customer_dispatch_time,b.demarcation_analysis_results,  \n" + "b.cch_sys_delimit_time,b.complain_suggestion1, b.complain_suggestion2,b.complain_suggestion3,b.complain_suggestion4,  \n" + "b.complain_suggestion5,a.cgi,a.cgi1,a.analysis_result from (SELECT S1.CITY,S1.PROPERTY,S1.CRM_ORDERNUM,S1.ANALYSIS_RESULT,   \n" + "S.SPDB_PROPERTY,S.NSN_PROPERTY,S.NSN_ID,S.NSN_PARENT_ID,S1.CGI1,S1.CGI,S1.UE_X,S1.UE_Y,S1.COMMUNITY_ID,S1.COMMUNITY_NAME, \n" + "S1.COMMUNITY_DIS FROM (select CITY,case when LTE_PROPERTY is null then GSM_PROPERTY  else LTE_PROPERTY END PROPERTY,\n" + "CRM_ORDERNUM,  ANALYSIS_RESULT, CGI1, CGI, UE_X, UE_Y,COMMUNITY_ID,COMMUNITY_NAME,COMMUNITY_DIS \n" + "from (select a1.CITY,CASE WHEN a1.CITY='大理' and  a1.PROPERTY='大理古城区' THEN \n" + "(case when a1.cgi is null then a3.area else a2.area end ) else a1.PROPERTY end LTE_PROPERTY,  \n" + "CASE WHEN a1.CITY='大理' and a1.PROPERTY='大理古城区' THEN (\n" + "case when a1.cgi is null then  a5.area else a4.area end ) else a1.PROPERTY end GSM_PROPERTY,  \n" + "a1.CRM_ORDERNUM, a1.ANALYSIS_RESULT,a1.cgi1, a1.CGI,a1.UE_X,a1.UE_Y,a1.COMMUNITY_ID,a1.COMMUNITY_NAME,a1.COMMUNITY_DIS  \n" + "from (select T.CITY,T.COMMUNITY_ID,T.COMMUNITY_NAME,T.PROPERTY,F.工单流水号 crm_ordernum,F.备注1 analysis_result,    \n" + "F.LTE关键小区 cgi,substr(COMPARE_SECTOR,0,instr(T.COMPARE_SECTOR,'(')-1) cgi1,T.UE_X, T.UE_Y,T.COMMUNITY_DIS  from (  \n" + "select a.CITY, a.COMMUNITY_ID,b.COMMUNITY_NAME, a.ORDERNUM, a.PHONENUM, a.PROPERTY, a.PROBLEM_CLASSIFY, a.BUSINESSTYPE,  \n" + "a.BUSINESS_CLASSIFY,a.PROBLEM_DETAIL,a.COMPLIANTPLACE, a.UE_X, a.UE_Y, a.UE_ADDRESS, a.VOICE_DATA_CLASSIFY,a.BUSSINESSCONTENT, \n" + "a.COMPLAINT_TIME,a.ORDER_SOURCE, a.STATE,a.SEQ_CELL, a.SEQ_CELL_LONGITUDE, a.SEQ_CELL_LATITUDE,a.SENDTIME, a.BENCHMARK,   \n" + "a.CITY_CODE,a.CITY_OR_RURAL, a.COMPARE_SECTOR, a.GRID_ID,b.COMMUNITY_DIS from  \n" + "(select * from TEMP_CP_COMPLAIN_LIST_ORDER_V1 where ordernum not in(select distinct  CRM_ORDERNUM from nsn_send_back)) a  \n" + " left join \n" + "(SELECT p.ORDERNUM, p.COMMUNITY_ID,sc.COMMUNITY_NAME, 0 COMMUNITY_DIS \n" + "        FROM p_lte_parmeter_complaint p,p_poi_scene sc where p.community_id=sc.community_id) b \n" + "        on a.ORDERNUM=b.ORDERNUM )T,FINAL_ANALYSIS_TABLE_V2_1 F  WHERE T.ORDERNUM=F.工单流水号) a1   \n" + "         left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a2 on a1.cgi=a2.SECTOR_ID   \n" + "         left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a3  on a1.CGI1=a3.sector_id    \n" + "         left join (select lac||'-'||ci as SECTOR_ID,area from p_gsm_sector_bak p) a4    \n" + "        on a1.cgi=a4.sector_id left join (select lac||'-'||ci as SECTOR_ID,area from p_gsm_sector_bak p) a5    \n" + "         on a1.CGI1=a5.sector_id)) S1,(SELECT SPDB_PROPERTY, NSN_PROPERTY, NSN_ID, NSN_PARENT_ID    \n" + "        FROM spdb_nsn_area_change) S WHERE S1.PROPERTY=S.SPDB_PROPERTY) a,(select eoms_ordernum as  order_name,   \n" + "        businesstype as order_theme,  to_char(version_date,'yyyy-mm-dd hh24:mi:ss') as order_time,   \n" + "        to_char(order_accept_limit_time,'yyyy-mm-dd hh24:mi:ss') as accept_time_limit,city,   \n" + "        ue_property as region,ue_date as complain_time, customer_name,phonenum as customer_tel,   \n" + "        complain_area,bussinesscontent as complain_content,if_big_area_complaint as large_area_complain,   \n" + "        repeated_complaints,complaint_level as customer_level, home_subscriber,complaint_acceptance_province,   \n" + "        fault_msisdn,compliantplace as fault_area, complain_city,property as appeal_area,   \n" + "        ordernum as crm_ordernum,terminal_description, td_supported, analysis_condition,   \n" + "        to_char(sendtime,'yyyy-mm-dd hh24:mi:ss') as customer_dispatch_time,demarcation_analysis_results,   \n" + "        to_char(cch_deal_time,'yyyy-mm-dd hh24:mi:ss') as cch_sys_delimit_time,complain_suggestion1,   \n" + "        complain_suggestion2, complain_suggestion3,complain_suggestion4, complain_suggestion5,customer_type   \n" + "         from p_lte_parmeter_complaint )b where a.crm_ordernum=b.crm_ordernum";

    /**
     * Description 功能接口测试
     *
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    @Override
    public String testSpdb(String sendSheet, String chhUrl) {
        try {
            log.info(sendSheet);
            log.info(chhUrl);
            String res = "接收数据成功,数据内容：" + sendSheet;
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "接收数据异常";
        }
    }

    /**
     * Description 接收亿阳的工单报文信息，进行数据入库
     *
     * @param sendSheet 工单信息
     * @param chhUrl CHH信息
     * @Author junwei
     * @Date 16:36 2019/9/27
     **/
    @Override
    public String SheetInfoSer(String sendSheet, String chhUrl) {
        try {

            if(sendSheet==null && chhUrl==null){
                log.info("\n接收到空的报文信息，返回1");
                return "1";
            }

            //log日志记录
            log2.info("报文处理：开始接收 ***************");

            log2.info("\n工单报文:" + sendSheet);
            log2.info("\nCHH报文:" + chhUrl);
            //启动线程
            ThreadRun threadRun=new ThreadRun(sendSheet,chhUrl);
            threadRun.start();
            //工单报文
            log2.info("报文处理：接收报文完成***************");
            return "0";
        } catch (Exception e) {
            return "error";
        }
    }



    /**
     * Description 预分析报文生成，返回到亿阳平台
     *
     * @param sendSheet
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    @Override
    public String turnAnalyReport(String sendSheet) {
        log.info("接收到投诉平台预分析结果工单：" + sendSheet);

        String insertLogSql = " INSERT INTO AXIS_MESSAGE_LOG (MESSAGE_TYPE,VERSION_DATE,ACCEPT_TIME,MESSAGE_DETAIL )\n" +
                "values(?,to_date(?,'yyyy-mm-dd'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?) ";
        LinkedList tempList = new LinkedList<>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
        try {
            String url = ConfigReaderUtils.getProperty("yiYangReportOrderUrl");
            String methor = ConfigReaderUtils.getProperty("yiYangReportOrderMethor");

            log.info("接收到：投诉平台预分析结果，预分析完成工单，现回传到亿阳平台\n" + url + "\n" + methor);


            String orderNums[] = sendSheet.split(",");
            String orderS = "";
            for (String order : orderNums) {
                orderS += order + "','";
            }
            orderS = orderS.substring(0, orderS.length() - 3);
            String checkCityCode = "select t1.EOMS_ORDERNUM,t1.ORDERNUM,nvl(t2.申告地,t1.CITY) CITY," +
                    "t2.备注2 as property,t2.备注3 export_time,\n" +
                    "t2.备注1 DETAIL from (SELECT ORDERNUM,EOMS_ORDERNUM,CITY,PROPERTY\n" +
                    "FROM P_LTE_PARMETER_COMPLAINT\n" +
                    "WHERE ORDERNUM in ('" + orderS + "')) t1\n" +
                    "left join FINAL_ANALYSIS_TABLE_V2_1 t2 on t1.ORDERNUM=t2.工单流水号 ";
            ResultSet rs2 = JdbcServer.executeStatement(checkCityCode);
            LinkedList<Map<String, Object>> list = new LinkedList<>();

            String result2 = "";
            while (rs2.next()) {
                String time = sf.format(new Date());
                list.clear();
                tempList.clear();
                Map<String, Object> map = new HashMap<>();
                map.put("EOMS_ORDERNUM", rs2.getString(1));
                map.put("ORDERNUM", rs2.getString(2));
                map.put("CITY", rs2.getString(3));
                map.put("PROPERTY", rs2.getString(4));
                map.put("EXPORT_TIME", rs2.getString(5));
                map.put("DETAIL", rs2.getString(6));
                list.add(map);

                String sendxml = XmlUtil.xmlOrderMsg(list).replace("\n", "").replace("&"," ");
                log2.info("\n预分析结果报文：" + sendxml);
//                System.out.println(sendxml);

                String result = XmlUtil.invoke(url, methor, sendxml);
                result2 += result + "\n";
                map1.put("MESSAGE_TYPE", "预分析回传报文");
                map1.put("VERSION_DATE", time.substring(0, 10));
                map1.put("ACCEPT_TIME", time);
                map1.put("MESSAGE_DETAIL", rs2.getString(2));
                if (result2 == null) {
                    map1.put("MESSAGE_TYPE", "预分析回传报文出错");
                } else {
                    map1.put("MESSAGE_TYPE", "亿阳返回数据成功");
                    log.info("发送亿阳报文成功，返回：" + result);
                }
                tempList.add(map1);
                boolean res1 = JdbcServer.inserts(insertLogSql, tempList);
            }
            return result2;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*******************************发送亿阳数据错误：\n" + e);
            return "error";
        }
    }


    /**
     * Description 预分析报文生成，返回到亿阳平台
     * @param
     * @Author junwei
     * @Date 9:53 2019/9/27
     **/
    @Override
    public void turnAnalyReport2(String sendSheet) {
        //调用肖老板的HTTP 请求代码
        NsnServiceImpl.analyTurnToEoms(sql);
    }


    public static void main(String[] args) {

        EomsServiceImpl eomsService = new EomsServiceImpl();
        eomsService.turnAnalyReport2(sql);

//        String sql="<opDetail><recordInfo><fieldInfo><fieldChName>网优投诉T1处理组（操作人）</fieldChName><fieldEnName>operateUser</fieldEnName><fieldContent>贾蓉</fieldContent></fieldInfo><fieldInfo><fieldChName>EOMS工单流水号</fieldChName><fieldEnName>sheetId</fieldEnName><fieldContent>YN-052-191105-00164</fieldContent></fieldInfo><fieldInfo><fieldChName>工单主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>移动业务→网络质量→手机上网（4G）→网速慢或掉线→功能使用→网速慢或网页无法打开→室内</fieldContent></fieldInfo><fieldInfo><fieldChName>派单时间</fieldChName><fieldEnName>sendTime</fieldEnName><fieldContent>2019-11-05 15:35:19</fieldContent></fieldInfo><fieldInfo><fieldChName>工单处理时限</fieldChName><fieldEnName>sheetCompleteLimit</fieldEnName><fieldContent>2019-11-07 09:35:22</fieldContent></fieldInfo><fieldInfo><fieldChName>紧急程度</fieldChName><fieldEnName>urgentDegree</fieldEnName><fieldContent>一般</fieldContent></fieldInfo><fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2019-11-05 15:33:47</fieldContent></fieldInfo><fieldInfo><fieldChName>客户电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>15187842259</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>#同步要素#联系要求:联系本机\n" +
//                "客户要求:用户联系在线客服反映在云南大学滇池学院杨林校区4G网络慢，要求尽快处理，谢谢！\n" +
//                "处于室内还是室外:室内\n" +
//                "（室内）处于该场景下的具体位置:窗边\n" +
//                "（数据）访问哪个网站出现问题:新浪\n" +
//                "周围其他用户是否有同样情况:有\n" +
//                "（室内）处于哪个场景:校园\n" +
//                "（室外）处于哪个场景:校园\n" +
//                "手机显示的网络标识:G\n" +
//                "无信号出现的频率:经常\n" +
//                "开关机情况:情况依旧#同步要素#</fieldContent></fieldInfo><fieldInfo><fieldChName>是否大面积投诉</fieldChName><fieldEnName>isWideComplaint</fieldEnName><fieldContent>否</fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent>一星</fieldContent></fieldInfo><fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>曲靖</fieldContent></fieldInfo><fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>15187842259</fieldContent></fieldInfo><fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>云南大学滇池学院杨林校区</fieldContent></fieldInfo><fieldInfo><fieldChName>申告地</fieldChName><fieldEnName>mainComplaintPlace</fieldEnName><fieldContent>昆明</fieldContent></fieldInfo><fieldInfo><fieldChName>申告区县</fieldChName><fieldEnName>mainPlaceCountry</fieldEnName><fieldContent>嵩明县</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM工单号</fieldChName><fieldEnName>crmSheetId</fieldEnName><fieldContent>20191105153056X565083640</fieldContent></fieldInfo><fieldInfo><fieldChName>客服中心派单时间</fieldChName><fieldEnName>mainCustomerSerTime</fieldEnName><fieldContent>2019-11-05 15:35:20</fieldContent></fieldInfo><fieldInfo><fieldChName>CCH系统定界处理时间</fieldChName><fieldEnName>mainFaultCchTime</fieldEnName><fieldContent>2019-11-05 15:41:06</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议1</fieldChName><fieldEnName>mainFaultCchTypeOne</fieldEnName><fieldContent>&gt;cause: 无线原因-频繁跟踪区更新, NE type: CGISAI;MSISDN, NE: CGISAI=嵩明县滇池学院景观灯杆-LDHX-021,嵩明县滇池学院景观灯杆-LDHX-024;MSISDN=15187842259, suggestion: 检查用户投诉地点是否为4G弱覆盖或无稳定主服务小区, other: 用户在小区嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-LDHX-021 上频繁跟踪区更新; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议2</fieldChName><fieldEnName>mainFaultCchTypeTwo</fieldEnName><fieldContent>次cause: 无线原因-下行RTT超长-4G, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰;若未发现无线问题  用户可能对网速期望过高或用户终端可能存在问题 建议用户换机换卡尝试, other: 用户所在小区 46000AD22784; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议3</fieldChName><fieldEnName>mainFaultCchTypeThree</fieldEnName><fieldContent>次cause: 无线原因-下行时延超长OTT-4G, NE type: CGISAI;MSISDN, NE: CGISAI=嵩明县云大滇池学院2号景观灯杆-LDHX-003,46000AD22784,46000AD22783,嵩明县云大滇池学院2号景观灯杆-FZHX-133,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-FZHN-141,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-131,嵩明县滇池学院景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-004,46000ADD6587;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰 若未发现无线问题 用户可能对网速期望过高 或终端存在问题 建议用户换机换卡尝试, other: 用户所在小区 46000AD22783,46000AD22784,嵩明县滇池学院景观灯杆-LDHX-003; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议4</fieldChName><fieldEnName>mainFaultCchTypeFour</fieldEnName><fieldContent>次cause: 无线原因-TCP三次握手时延超长-OTT, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-LDHX-003,46000ADD6587,嵩明县滇池学院景观灯杆-LDHX-004;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰若未发现无线问题  用户可能对网速期望过高或用户终端可能存在问题 建议用户换机换卡尝试, other: 用户在小区嵩明县滇池学院景观灯杆-LDHX-024,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-004; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议5</fieldChName><fieldEnName>mainFaultCchTypeFive</fieldEnName><fieldContent>次cause: 用户原因-未见网络异常, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784,嵩明县滇池学院-LDSX-153,嵩明县云大滇池学院2号景观灯杆-FZHN-143,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-023,46000AD22783,46000CA981EB,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-FZHN-141,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-131,46000ADD6588,嵩明县师大商学院皮卡方舱-LDHX-131,嵩明县滇池学院景观灯杆-LDHX-003,46000ADD6587,嵩明县滇池学院景观灯杆-LDHX-004,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-021,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-133,嵩明县云大滇池学院2号景观灯杆-FZHX-133,嵩明县滇池学院-LDSX-015,46000AD22780,嵩明县云大滇池学院2号景观灯杆-LDHX-063,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-021,嵩明县滇池学院-LDSX-017;MSISDN=15187842259, suggestion: 如果有大量业务请确认 1.用户业务是否已经恢复正常 2.用户是否对网速期望过高或抱怨的其他非网络问题 3.是否终端突发异常 4.是否用户抱怨的是高制式网络没有覆盖  如果业务量非常少 疑似无线侧存在覆盖或性能故障问题, other: 用户投诉前在小区 嵩明县滇池学院景观灯杆-LDHX-004,46000AD22784,46000AD22783有web业务共6.000次 在小区 嵩明县滇池学院景观灯杆-LDHX-003,46000AD22784,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-023有OTT业务共790.000次; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo></recordInfo></opDetail>\n";
//        eomsService.getComMsg(sql, "");


        String sql="<opDetail><recordInfo><fieldInfo><fieldChName>网优投诉T1处理组（操作人）</fieldChName><fieldEnName>operateUser</fieldEnName><fieldContent>贾蓉</fieldContent></fieldInfo><fieldInfo><fieldChName>EOMS工单流水号</fieldChName><fieldEnName>sheetId</fieldEnName><fieldContent>YN-052-191105-00164</fieldContent></fieldInfo><fieldInfo><fieldChName>工单主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>移动业务→网络质量→手机上网（4G）→网速慢或掉线→功能使用→网速慢或网页无法打开→室内</fieldContent></fieldInfo><fieldInfo><fieldChName>派单时间</fieldChName><fieldEnName>sendTime</fieldEnName><fieldContent>2019-11-05 15:35:19</fieldContent></fieldInfo><fieldInfo><fieldChName>工单处理时限</fieldChName><fieldEnName>sheetCompleteLimit</fieldEnName><fieldContent>2019-11-07 09:35:22</fieldContent></fieldInfo><fieldInfo><fieldChName>紧急程度</fieldChName><fieldEnName>urgentDegree</fieldEnName><fieldContent>一般</fieldContent></fieldInfo><fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2019-11-05 15:33:47</fieldContent></fieldInfo><fieldInfo><fieldChName>客户电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>15187842259</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>#同步要素#联系要求:联系本机\n" +
                "客户要求:用户联系在线客服反映在云南大学滇池学院杨林校区4G网络慢，要求尽快处理，谢谢！\n" +
                "处于室内还是室外:室内\n" +
                "（室内）处于该场景下的具体位置:窗边\n" +
                "（数据）访问哪个网站出现问题:新浪\n" +
                "周围其他用户是否有同样情况:有\n" +
                "（室内）处于哪个场景:校园\n" +
                "（室外）处于哪个场景:校园\n" +
                "手机显示的网络标识:G\n" +
                "无信号出现的频率:经常\n" +
                "开关机情况:情况依旧#同步要素#</fieldContent></fieldInfo><fieldInfo><fieldChName>是否大面积投诉</fieldChName><fieldEnName>isWideComplaint</fieldEnName><fieldContent>否</fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent>一星</fieldContent></fieldInfo><fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>曲靖</fieldContent></fieldInfo><fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>15187842259</fieldContent></fieldInfo><fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>云南大学滇池学院杨林校区</fieldContent></fieldInfo><fieldInfo><fieldChName>申告地</fieldChName><fieldEnName>mainComplaintPlace</fieldEnName><fieldContent>昆明</fieldContent></fieldInfo><fieldInfo><fieldChName>申告区县</fieldChName><fieldEnName>mainPlaceCountry</fieldEnName><fieldContent>嵩明县</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM工单号</fieldChName><fieldEnName>crmSheetId</fieldEnName><fieldContent>20191105153056X565083640</fieldContent></fieldInfo><fieldInfo><fieldChName>客服中心派单时间</fieldChName><fieldEnName>mainCustomerSerTime</fieldEnName><fieldContent>2019-11-05 15:35:20</fieldContent></fieldInfo><fieldInfo><fieldChName>CCH系统定界处理时间</fieldChName><fieldEnName>mainFaultCchTime</fieldEnName><fieldContent>2019-11-05 15:41:06</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议1</fieldChName><fieldEnName>mainFaultCchTypeOne</fieldEnName><fieldContent>&gt;cause: 无线原因-频繁跟踪区更新, NE type: CGISAI;MSISDN, NE: CGISAI=嵩明县滇池学院景观灯杆-LDHX-021,嵩明县滇池学院景观灯杆-LDHX-024;MSISDN=15187842259, suggestion: 检查用户投诉地点是否为4G弱覆盖或无稳定主服务小区, other: 用户在小区嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-LDHX-021 上频繁跟踪区更新; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议2</fieldChName><fieldEnName>mainFaultCchTypeTwo</fieldEnName><fieldContent>次cause: 无线原因-下行RTT超长-4G, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰;若未发现无线问题  用户可能对网速期望过高或用户终端可能存在问题 建议用户换机换卡尝试, other: 用户所在小区 46000AD22784; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议3</fieldChName><fieldEnName>mainFaultCchTypeThree</fieldEnName><fieldContent>次cause: 无线原因-下行时延超长OTT-4G, NE type: CGISAI;MSISDN, NE: CGISAI=嵩明县云大滇池学院2号景观灯杆-LDHX-003,46000AD22784,46000AD22783,嵩明县云大滇池学院2号景观灯杆-FZHX-133,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-FZHN-141,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-131,嵩明县滇池学院景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-004,46000ADD6587;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰 若未发现无线问题 用户可能对网速期望过高 或终端存在问题 建议用户换机换卡尝试, other: 用户所在小区 46000AD22783,46000AD22784,嵩明县滇池学院景观灯杆-LDHX-003; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议4</fieldChName><fieldEnName>mainFaultCchTypeFour</fieldEnName><fieldContent>次cause: 无线原因-TCP三次握手时延超长-OTT, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-LDHX-003,46000ADD6587,嵩明县滇池学院景观灯杆-LDHX-004;MSISDN=15187842259, suggestion: 请检查用户投诉地点是否为弱覆盖或存在干扰若未发现无线问题  用户可能对网速期望过高或用户终端可能存在问题 建议用户换机换卡尝试, other: 用户在小区嵩明县滇池学院景观灯杆-LDHX-024,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-004; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议5</fieldChName><fieldEnName>mainFaultCchTypeFive</fieldEnName><fieldContent>次cause: 用户原因-未见网络异常, NE type: CGISAI;MSISDN, NE: CGISAI=46000AD22784,嵩明县滇池学院-LDSX-153,嵩明县云大滇池学院2号景观灯杆-FZHN-143,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-023,46000AD22783,46000CA981EB,嵩明县滇池学院景观灯杆-LDHX-024,嵩明县滇池学院景观灯杆-FZHN-141,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-131,46000ADD6588,嵩明县师大商学院皮卡方舱-LDHX-131,嵩明县滇池学院景观灯杆-LDHX-003,46000ADD6587,嵩明县滇池学院景观灯杆-LDHX-004,嵩明县云大滇池学院2号景观灯杆-LDHX-003,嵩明县滇池学院景观灯杆-LDHX-021,嵩明县滇池学院文博路与文轩路交叉口灯杆-LDHX-133,嵩明县云大滇池学院2号景观灯杆-FZHX-133,嵩明县滇池学院-LDSX-015,46000AD22780,嵩明县云大滇池学院2号景观灯杆-LDHX-063,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-021,嵩明县滇池学院-LDSX-017;MSISDN=15187842259, suggestion: 如果有大量业务请确认 1.用户业务是否已经恢复正常 2.用户是否对网速期望过高或抱怨的其他非网络问题 3.是否终端突发异常 4.是否用户抱怨的是高制式网络没有覆盖  如果业务量非常少 疑似无线侧存在覆盖或性能故障问题, other: 用户投诉前在小区 嵩明县滇池学院景观灯杆-LDHX-004,46000AD22784,46000AD22783有web业务共6.000次 在小区 嵩明县滇池学院景观灯杆-LDHX-003,46000AD22784,嵩明县云大滇池学院2号景观灯杆(D)-LDHX-023有OTT业务共790.000次; 用户终端型号 Unknown Unknown; 4G OTT业务790.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo></recordInfo></opDetail>";

//        eomsService.getComMsg(sql, "");



    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("**************************EOMS流转平台启动*************************");
        log.info("**************************EOMS流转平台启动*************************");
        Task task=new Task();
        task.startTask();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
