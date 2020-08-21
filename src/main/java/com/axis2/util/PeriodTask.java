package com.axis2.util;

import com.axis2.impl.NsnServiceImpl;


public class PeriodTask extends NsnServiceImpl{
//    private static final Logger log = Logger.getLogger(NsnServiceImpl.class.getClass());
//    private static final Logger log2 = Logger.getLogger("FILE2");


    public static void re_post_ordernum(){
          String sql_redo ="select  a.NSN_PARENT_ID city,a.NSN_ID region, b.order_name,a.UE_X,a.UE_Y,b.order_theme," +
                "b.order_time,b.accept_time_limit,b.complain_time,b.customer_name,b.customer_type, b.customer_tel,b.complain_area," +
                "b.complain_content,b.large_area_complain,b.repeated_complaints,b.customer_level,b.home_subscriber,b.complaint_acceptance_province," +
                "b.fault_msisdn,b.fault_area,b.complain_city,a.PROPERTY appeal_area,a.crm_ordernum,b.terminal_description,b.td_supported," +
                "b.analysis_condition,b.customer_dispatch_time,b.demarcation_analysis_results,b.cch_sys_delimit_time,b.complain_suggestion1," +
                "b.complain_suggestion2,b.complain_suggestion3,b.complain_suggestion4,b.complain_suggestion5,a.cgi,a.cgi1,a.analysis_result from" +
                " (SELECT S1.CITY,S1.PROPERTY,S1.CRM_ORDERNUM,S1.ANALYSIS_RESULT,S.SPDB_PROPERTY,S.NSN_PROPERTY,S.NSN_ID,S.NSN_PARENT_ID,S1.CGI1,S1.CGI," +
                "S1.UE_X,S1.UE_Y FROM (select a1.CITY, CASE WHEN a1.CITY='大理' and a1.PROPERTY='大理古城区' THEN (case when a1.cgi is null then a3.area else a2.area end) else a1.PROPERTY end PROPERTY," +
                "a1.CRM_ORDERNUM, a1.ANALYSIS_RESULT,a1.cgi1,a1.CGI,a1.UE_X,a1.UE_Y  from ( select F.申告地 CITY, F.区县 PROPERTY,F.工单流水号 crm_ordernum,F.备注1 analysis_result," +
                "F.LTE关键小区 cgi,substr(F.NEAREST_CELL,0,instr(F.NEAREST_CELL,'(')-1) cgi1,F.经度 UE_X, F.纬度 UE_Y from  FINAL_ANALYSIS_TABLE_V2_1 F   WHERE   F.工单流水号 IN (select distinct 工单流水号 " +
                "from final_analysis_table_v2_1 where substr(工单流水号,0,8)>='20200817' and 工单流水号 not in (select  CRM_ORDERNUM from nsn_send_back where " +
                "substr(CRM_ORDERNUM,0,8)>='20200817')  and 工单流水号 in (select ORDERNUM from P_LTE_PARMETER_COMPLAINT where substr(ORDERNUM,0,8)>='20200817' and" +
                " EOMS_ORDERNUM is not null)  and 工单流水号 in  (select distinct ORDERNUM from nsn_response_failed_detail_table)" +
                ")) a1   left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a2 " +
                "on a1.cgi=a2.SECTOR_ID left join (select SECTOR_ID,AREA from p_lte_sector_bak p) a3  on a1.CGI1=a3.sector_id) S1,(SELECT SPDB_PROPERTY, " +
                "NSN_PROPERTY, NSN_ID, NSN_PARENT_ID FROM spdb_nsn_area_change) S WHERE S1.PROPERTY=S.SPDB_PROPERTY) a, (select eoms_ordernum as  order_name," +
                "businesstype as order_theme,  to_char(version_date,'yyyy-mm-dd hh24:mi:ss') as order_time,to_char(order_accept_limit_time,'yyyy-mm-dd hh24:mi:ss') as accept_time_limit,city," +
                "ue_property as region,ue_date as complain_time, customer_name,phonenum as customer_tel,complain_area,bussinesscontent as complain_content," +
                "if_big_area_complaint as large_area_complain, repeated_complaints,complaint_level as customer_level, home_subscriber,complaint_acceptance_province, " +
                "fault_msisdn,compliantplace as fault_area, complain_city,property as appeal_area,ordernum as crm_ordernum,terminal_description, td_supported, " +
                "analysis_condition,to_char(sendtime,'yyyy-mm-dd hh24:mi:ss') as customer_dispatch_time,demarcation_analysis_results, " +
                "to_char(cch_deal_time,'yyyy-mm-dd hh24:mi:ss') as cch_sys_delimit_time,complain_suggestion1,complain_suggestion2," +
                " complain_suggestion3,complain_suggestion4, complain_suggestion5,customer_type from p_lte_parmeter_complaint )b where" +
                " a.crm_ordernum=b.crm_ordernum";
        NsnServiceImpl.analyTurnToEoms(sql_redo);
    }

//    public static void main(String[] args) {
//        PeriodTask pt=new PeriodTask();
//        pt.re_post_ordernum(sql_redo);
//    }

}
