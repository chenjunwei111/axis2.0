package com.axis2.test;

import com.axis2.pojo.BaiduPoint;
import com.axis2.pojo.WebDeCoding;
import com.axis2.util.JdbcServer;
import com.axis2.util.MapUtil;
import com.axis2.util.PositionUtil;
import com.axis2.util.XmlUtil;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.spi.LoggerFactory;

/**
 * Description 测试
 *
 * @Author junwei
 * @Date 16:11 2019/9/26
 **/
public class TestClass {

    private static final Logger log = Logger.getLogger("FILE1");
    private static final Logger log2 = Logger.getLogger("FILE2");


    public static void main(String[] args) {


//        String ss="<opDetail><recordInfo><fieldInfo><fieldChName>网优投诉T1处理组（操作人）</fieldChName><fieldEnName>operateUser</fieldEnName><fieldContent>李海燕</fieldContent></fieldInfo><fieldInfo><fieldChName>EOMS工单流水号</fieldChName><fieldEnName>sheetId</fieldEnName><fieldContent>YN-052-200318-00083</fieldContent></fieldInfo><fieldInfo><fieldChName>工单主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>本地类型→4G上网预警查询→无网络信号</fieldContent></fieldInfo><fieldInfo><fieldChName>派单时间</fieldChName><fieldEnName>sendTime</fieldEnName><fieldContent>2020-03-18 11:12:59</fieldContent></fieldInfo><fieldInfo><fieldChName>工单处理时限</fieldChName><fieldEnName>sheetCompleteLimit</fieldEnName><fieldContent>2020-03-18 17:13:02</fieldContent></fieldInfo><fieldInfo><fieldChName>紧急程度</fieldChName><fieldEnName>urgentDegree</fieldEnName><fieldContent>一般</fieldContent></fieldInfo><fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2020-03-18 11:08:44</fieldContent></fieldInfo><fieldInfo><fieldChName>客户电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>13808758349</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>用户反映从开始在彩云北路路馨小区2栋2单元401号（室内），GPRS功能开通，手机上显示的网络标识是E网,信号时有时无，无法正常通话和上网，周围用户也有同样情况，开关机情况依旧,用户要求我方尽快核查处理，谢谢</fieldContent></fieldInfo><fieldInfo><fieldChName>是否大面积投诉</fieldChName><fieldEnName>isWideComplaint</fieldEnName><fieldContent>否</fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent>五星</fieldContent></fieldInfo><fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>昆明</fieldContent></fieldInfo><fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>13808758349</fieldContent></fieldInfo><fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>彩云北路路馨小区2栋2单元401号</fieldContent></fieldInfo><fieldInfo><fieldChName>申告地</fieldChName><fieldEnName>mainComplaintPlace</fieldEnName><fieldContent>昆明</fieldContent></fieldInfo><fieldInfo><fieldChName>申告区县</fieldChName><fieldEnName>mainPlaceCountry</fieldEnName><fieldContent>官渡区</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM工���号</fieldChName><fieldEnName>crmSheetId</fieldEnName><fieldContent>20200318110636X796259985</fieldContent></fieldInfo><fieldInfo><fieldChName>客服中心派单时间</fieldChName><fieldEnName>mainCustomerSerTime</fieldEnName><fieldContent>2020-03-18 11:13:01</fieldContent></fieldInfo><fieldInfo><fieldChName>CCH系统定界处理时间</fieldChName><fieldEnName>mainFaultCchTime</fieldEnName><fieldContent>2020-03-18 11:16:16</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议1</fieldChName><fieldEnName>mainFaultCchTypeOne</fieldEnName><fieldContent>&gt;cause: 用户原因-未见网络异常, NE type: CGISAI;MSISDN, NE: CGISAI=官渡区云通驾校-FHHQ-135,呈贡区经开区倪家营灯杆-LZHQ-023,官渡区小板桥彼岸小区-LHHQ-022,呈贡区倪家营社区回迁房C-LZSQ-153,官渡区邮电学校西北-LZHQ-131,官渡区新螺蛳湾三期11号地块灯杆-LZHQ-143,呈贡县集装箱中心-LZHN-062,呈贡区集装箱中心-LZHN-031,官渡区官小路口灯杆-FHHQ-141,呈贡大西洋电焊厂-LZHN-001,官渡区六谷新村-FHHQ-142,官渡区六谷村-LHHQ-021,呈贡区集装箱中心-FHHN-142,昆明官渡_跑马山水塔-LZHZ-001,官渡区官小路口灯杆-LZHQ-004,官渡区中国国电-LHSQ-011,官渡区官渡广卫新村2-LZHQ-023;MSISDN=13808758349, suggestion: 如果有大量业务请确认 1.用户业务是否已经恢复正常 2.用户是否对网速期望过高或抱怨的其他非网络问题 3.是否终端突发异常 4.是否用户抱怨的是高制式网络没有覆盖  如果业务量非常少 疑似无线侧存在覆盖或性能故障问题, other: 用户投诉前在小区 呈贡大西洋电焊厂-LZHN-001有web业务共2.000次 在小区 呈贡大西洋电焊厂-LZHN-001,官渡区中国国电-LHSQ-011,官渡区官渡广卫新村2-LZHQ-023有OTT业务共409.000次; 用户终端型号 Unknown Unknown; 4G OTT业务409.000次 ; 4G Web业务2.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议2</fieldChName><fieldEnName>mainFaultCchTypeTwo</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议3</fieldChName><fieldEnName>mainFaultCchTypeThree</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议4</fieldChName><fieldEnName>mainFaultCchTypeFour</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议5</fieldChName><fieldEnName>mainFaultCchTypeFive</fieldEnName><fieldContent></fieldContent></fieldInfo></recordInfo></opDetail>";

//        ThreadRun.getComMsg(ss,null);
    }


    public static void test1() {
        try {
            String insertSql = "insert into TEST_JDBC(ORDERNUM) values(?)";
            LinkedList<LinkedHashMap<String, Object>> tempList = new LinkedList<>();
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("ORDERNUM", "12345");
            tempList.add(map);
            Boolean bl = JdbcServer.inserts(insertSql, tempList);
            if (bl) {
                System.out.println("插入成功");
                log.info("插入成功");
            } else {
                System.out.println("插入失败");
                log.info("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void test2() {
        try {

            List<WebDeCoding> list= MapUtil.getBaiDuPoint("公园路168号","昭通",null);

            WebDeCoding web=list.get(0);
            System.out.println(web);
            System.out.println(web.getPoint());
            BaiduPoint point= PositionUtil.bd09_To_BaiduPoint84(web.getPoint().getLat(),web.getPoint().getLng());
            System.out.println(point.getLat()+point.getLng());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  static  void test3(){
        String xml="<attachRef>\n" +
                "  <attachInfo>\n" +
                "    <attachName>20190905153715118.xls</attachName>\n" +
                "    <attachURL>ftp://10.174.239.221/export/eoms35/uploadfile/accessories/uploadfile/sheet/complaint/20190905153715118.xls</attachURL>\n" +
                "    <attachLength>70656</attachLength>\n" +
                "  </attachInfo>\n" +
                "</attachRef>";
        List<Map<String,Object>> listTotal= XmlUtil.xmlElementListFtp(xml);

        System.out.println(listTotal);
    }

}
