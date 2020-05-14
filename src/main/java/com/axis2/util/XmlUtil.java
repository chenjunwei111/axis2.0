package com.axis2.util;

import org.apache.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description 报文工具类
 *
 * @Author junwei
 * @Date 16:29 2019/9/26
 **/
public class XmlUtil {


    private static final Logger log = Logger.getLogger(XmlUtil.class.getClass());


    /**
     * 解析xml字符串返回一个Map
     *
     * @param xmlDoc
     * @return Map
     */
    public static Map<String, Object> xmlElementsMap(String xmlDoc) {

        Map<String, Object> map = new HashMap();
        // 创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 得到根元素所有子元素的集合
            List jiedian = root.getChildren();
            // 获得XML中的命名空间（XML中未定义可不写）
            Element et = null;
            for (int i = 0; i < jiedian.size(); i++) {
                // 循环依次得到子元素
                et = (Element) jiedian.get(i);
                List<Content> et2 = et.getContent();
                String name = ((Element) et2.get(1)).getContent().get(0).getValue();
                String value = ((Element) et2.get(2)).getContent().get(0).getValue();
                map.put(name, value);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
            System.out.println("出错");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("出错");
        }
        read.close();
        return map;
    }


    /**
     * Description 解析多个xml字符串返回一个List
     *
     * @param
     * @Author junwei
     * @Date 9:33 2019/9/29
     **/
    public static List<Map<String, Object>> xmlElementsList(String xmlDoc) {

        List<Map<String, Object>> list = new LinkedList<>();

        // 创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 得到根元素所有子元素的集合
            List jiedian = root.getChildren();
            // 获得XML中的命名空间（XML中未定义可不写）
            Element et = null;
            for (int i = 0; i < jiedian.size(); i++) {
                Map<String, Object> map = new HashMap();
                // 循环依次得到子元素
                et = (Element) jiedian.get(i);
                List<Element> et2 = et.getChildren();
                for (Element element : et2) {
                    String name = element.getContent().get(1).getValue();
                    String value = element.getContent().get(2).getValue();
                    map.put(name, value);
                }
                list.add(map);
            }
        } catch (JDOMException e) {
            log.error("************************报文转换异常：",e);
            log.info("************************异常报文内容："+xmlDoc);
        } catch (IOException e) {
            log.error("************************IO报文转换异常：",e);
            log.info("************************异常报文内容："+xmlDoc);
        }
        read.close();
        return list;
    }


    /**
     * Description 解析多个xml字符串返回一个List
     *
     * @param
     * @Author junwei
     * @Date 9:33 2019/9/29
     **/
    public static List<Map<String, Object>> xmlElementListFtp(String xmlDoc) {

        List<Map<String, Object>> list = new LinkedList<>();

        // 创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        // 创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            // 通过输入源构造一个Document
            Document doc = sb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();
            // 得到根元素所有子元素的集合
            List jiedian = root.getChildren();
            // 获得XML中的命名空间（XML中未定义可不写）
            Element et = null;
            for (int i = 0; i < jiedian.size(); i++) {
                Map<String, Object> map = new HashMap();
                // 循环依次得到子元素
                et = (Element) jiedian.get(i);
                List<Element> et2 = et.getChildren();
                for (Element element : et2) {
                    String name = element.getName();
                    String value = element.getContent().get(0).getValue();
                    map.put(name, value);
                }
                list.add(map);
            }
        } catch (JDOMException e) {
            log.error("************************1",e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("************************2",e);
            e.printStackTrace();
        }
        read.close();
        return list;
    }


    /**
     * Description 封装工单报文
     *
     * @param linkedList
     * @Author junwei
     * @Date 15:58 2019/9/30
     **/
    public static String xmlOrderMsg(LinkedList<Map<String,Object>> linkedList) {
        try {
            String xml="<opDetail>";
            for(Map<String,Object> map:linkedList){
                String emosId=map.get("EOMS_ORDERNUM")==null?"":map.get("EOMS_ORDERNUM").toString();
                String orderNum= map.get("ORDERNUM")==null?"":map.get("ORDERNUM").toString();
                String city= map.get("CITY")==null?"":map.get("CITY").toString();
                String property= map.get("PROPERTY")==null?"":map.get("PROPERTY").toString();
                String operaTime= map.get("EXPORT_TIME")==null?"":map.get("EXPORT_TIME").toString();
                String detail=map.get("DETAIL")==null?"无":map.get("DETAIL").toString();

                xml+="<recordInfo>";
                xml+="<fieldInfo>" +
                        "<fieldChName>EMOS工单号</fieldChName>" +
                        "<fieldEnName>sheetId</fieldEnName>" +
                        "<fieldContent>"+emosId+"</fieldContent>" +
                        "</fieldInfo>" ;

                xml+="<fieldInfo>" +
                        "<fieldChName>CRM工单号</fieldChName>" +
                        "<fieldEnName>crmSheetId</fieldEnName>" +
                        "<fieldContent>"+orderNum+"</fieldContent>" +
                        "</fieldInfo>" ;

                xml+="<fieldInfo>" +
                        "<fieldChName>地市</fieldChName>" +
                        "<fieldEnName>area</fieldEnName>" +
                        "<fieldContent>"+city+"</fieldContent>" +
                        "</fieldInfo>";

                xml+="<fieldInfo>" +
                        "<fieldChName>区县</fieldChName>" +
                        "<fieldEnName>cnty</fieldEnName>" +
                        "<fieldContent>"+property+"</fieldContent>" +
                        "</fieldInfo>";

//                xml+="<fieldInfo>" +
//                        "<fieldChName>区县处理时限</fieldChName>" +
//                        "<fieldEnName>cntyCompleteLimit</fieldEnName>" +
//                        "<fieldContent> </fieldContent>" +
//                        "</fieldInfo>";

                xml+="<fieldInfo>" +
                        "<fieldChName>区县处理时限</fieldChName>" +
                        "<fieldEnName>cntyCompleteLimit</fieldEnName>" +
                        "<fieldContent>空</fieldContent>" +
                        "</fieldInfo>";

                xml+="<fieldInfo>" +
                        "<fieldChName>是否转派</fieldChName>" +
                        "<fieldEnName>IsTransCnty</fieldEnName>" +
                        "<fieldContent>否</fieldContent>" +
                        "</fieldInfo>";


                xml+="<fieldInfo>" +
                        "<fieldChName>转派说明</fieldChName>" +
                        "<fieldEnName>linkTransmitReason</fieldEnName>" +
                        "<fieldContent>"+detail+"</fieldContent>" +
                        "</fieldInfo>";
                xml+="</recordInfo>";
            }
            xml+="</opDetail>";
            return xml;
        } catch (Exception e) {
            log.error("************************报文封装报错",e);
            e.printStackTrace();
            return  "error";
        }
    }

    ;


    /**
     * Description 调用webservice路口
     *
     * @param endpoint   地址
     * @param methodName 调用的方法
     * @param xmlStr     传递的xml字符串参数
     * @Author junwei
     * @Date 15:41 2019/9/30
     **/
    public static String invoke(String endpoint, String methodName, String xmlStr) {
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        QName qn = new QName(methodName);
        call.setOperationName(qn);
        call.setTargetEndpointAddress(endpoint);
        call.setUseSOAPAction(true);
        String result = "";
        try {
            // 给方法传递参数，并且调用方法
            result = (String) call.invoke(new Object[]{null,null,null,null,xmlStr});
            return result;
        } catch (RemoteException e) {
            log.error("************************axis1.4报文发送失败:",e);
            e.printStackTrace();
            return null;
        }
    }

//

    public static void main(String[] args) {
        String sendSheet = "<opDetail>" +
                "<recordInfo>" +
                "<fieldInfo><fieldChName>网优投诉T1处理组（操作人）</fieldChName><fieldEnName>operateUser</fieldEnName><fieldContent>贾蓉</fieldContent></fieldInfo><fieldInfo><fieldChName>EOMS工单流水号</fieldChName><fieldEnName>sheetId</fieldEnName><fieldContent>YN-052-190923-00086</fieldContent></fieldInfo><fieldInfo><fieldChName>工单主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>移动业务→网络质量→手机上网（4G）→网速慢或掉线→功能使用→网速慢或网页无法打开→室外</fieldContent></fieldInfo><fieldInfo><fieldChName>派单时间</fieldChName><fieldEnName>sendTime</fieldEnName><fieldContent>2019-09-23 20:50:24</fieldContent></fieldInfo><fieldInfo><fieldChName>工单处理时限</fieldChName><fieldEnName>sheetCompleteLimit</fieldEnName><fieldContent>2019-09-27 14:50:32</fieldContent></fieldInfo><fieldInfo><fieldChName>紧急程度</fieldChName><fieldEnName>urgentDegree</fieldEnName><fieldContent>一般</fieldContent></fieldInfo><fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent>2019-09-23 20:44:40</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2019-09-23 20:48:55</fieldContent></fieldInfo><fieldInfo><fieldChName>客户姓名</fieldChName><fieldEnName>customerName</fieldEnName><fieldContent>王**</fieldContent></fieldInfo><fieldInfo><fieldChName>客户电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>13638815511</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>#同步要素#联系要求:本机 客户要求:客户来电反映，本机无法使用4G网络，网速慢，要求查询原因，并且尽快处理，谢谢 处于室内还是室外:室内 （室内）处于该场景下的具体位置:窗边 （数据）访问哪个网站出现问题:新浪 周围其他用户是否有同样情况:无 （室内）处于哪个场景:商业区（包括：酒店、休闲娱乐场所、大型场馆等） （室外）处于哪个场景:商业区（包括：酒店、大型场馆、休闲娱乐场馆等） 手机显示的网络标识:E 无信号出现的频率:总是 开关机情况:情况依旧#同步要素#</fieldContent></fieldInfo><fieldInfo><fieldChName>是否大面积投诉</fieldChName><fieldEnName>isWideComplaint</fieldEnName><fieldContent>否</fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent>三星</fieldContent></fieldInfo><fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>昭通</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类一级类别</fieldChName><fieldEnName>complaintType1</fieldEnName><fieldContent>移动业务</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类二级类别</fieldChName><fieldEnName>complaintType2</fieldEnName><fieldContent>网络质量</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类三级类别</fieldChName><fieldEnName>complaintType3</fieldEnName><fieldContent>手机上网（4G）</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类四级类别</fieldChName><fieldEnName>complaintType4</fieldEnName><fieldContent>网速慢或掉线</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类五级类别</fieldChName><fieldEnName>complaintType5</fieldEnName><fieldContent>功能使用</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类六级类别</fieldChName><fieldEnName>complaintType6</fieldEnName><fieldContent>网速慢或网页无法打开</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类七级类别</fieldChName><fieldEnName>complaintType7</fieldEnName><fieldContent>室外</fieldContent></fieldInfo><fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>13638815511</fieldContent></fieldInfo><fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>公园路168号</fieldContent></fieldInfo><fieldInfo><fieldChName>申告地</fieldChName><fieldEnName>mainComplaintPlace</fieldEnName><fieldContent>昭通</fieldContent></fieldInfo><fieldInfo><fieldChName>申告区县</fieldChName><fieldEnName>mainPlaceCountry</fieldEnName><fieldContent>昭阳区</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM工单号</fieldChName><fieldEnName>crmSheetId</fieldEnName><fieldContent>20190923204423X663036206</fieldContent></fieldInfo><fieldInfo><fieldChName>客服中心派单时间</fieldChName><fieldEnName>mainCustomerSerTime</fieldEnName><fieldContent>2019-09-23 20:50:25.0</fieldContent></fieldInfo><fieldInfo><fieldChName>CCH系统定界处理时间</fieldChName><fieldEnName>mainFaultCchTime</fieldEnName><fieldContent>2019-09-23 21:22:30.0</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议1</fieldChName><fieldEnName>mainFaultCchTypeOne</fieldEnName><fieldContent>&gt;cause: 用户原因-未见网络异常, NE type: CGISAI;MSISDN, NE: CGISAI=泸水县上橄榄-LHHN-002,泸水县新寨-LHHN-002;MSISDN=15008850290, suggestion: 如果有大量业务请确认 1.用户业务是否已经恢复正常 2.用户是否对网速期望过高或抱怨的其他非网络问题 3.是否终端突发异常 4.是否用户抱怨的是高制式网络没有覆盖 如果业务量非常少 疑似无线侧存在覆盖或性能故障问题, other: 用户投诉前在小区 泸水县新寨-LHHN-002有web业务共6.000次 在小区 泸水县上橄榄-LHHN-002,泸水县新寨-LHHN-002有OTT业务共38.000次; 用户终端型号 VIVO VIVO Y51A; 4G OTT业务38.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo>" +
                "</recordInfo>" +
//                "<recordInfo>" +
//                "<fieldInfo><fieldChName>网优投诉T1处理组（操作人）</fieldChName><fieldEnName>operateUser</fieldEnName><fieldContent>贾蓉</fieldContent></fieldInfo><fieldInfo><fieldChName>EOMS工单流水号</fieldChName><fieldEnName>sheetId</fieldEnName><fieldContent>YN-052-190923-00086</fieldContent></fieldInfo><fieldInfo><fieldChName>工单主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>移动业务→网络质量→手机上网（4G）→网速慢或掉线→功能使用→网速慢或网页无法打开→室外</fieldContent></fieldInfo><fieldInfo><fieldChName>派单时间</fieldChName><fieldEnName>sendTime</fieldEnName><fieldContent>2019-09-23 20:50:24</fieldContent></fieldInfo><fieldInfo><fieldChName>工单处理时限</fieldChName><fieldEnName>sheetCompleteLimit</fieldEnName><fieldContent>2019-09-27 14:50:32</fieldContent></fieldInfo><fieldInfo><fieldChName>紧急程度</fieldChName><fieldEnName>urgentDegree</fieldEnName><fieldContent>一般</fieldContent></fieldInfo><fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent>2019-09-23 20:44:40</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2019-09-23 20:48:55</fieldContent></fieldInfo><fieldInfo><fieldChName>客户姓名</fieldChName><fieldEnName>customerName</fieldEnName><fieldContent>王**</fieldContent></fieldInfo><fieldInfo><fieldChName>客户电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>13638815511</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>#同步要素#联系要求:本机 客户要求:客户来电反映，本机无法使用4G网络，网速慢，要求查询原因，并且尽快处理，谢谢 处于室内还是室外:室内 （室内）处于该场景下的具体位置:窗边 （数据）访问哪个网站出现问题:新浪 周围其他用户是否有同样情况:无 （室内）处于哪个场景:商业区（包括：酒店、休闲娱乐场所、大型场馆等） （室外）处于哪个场景:商业区（包括：酒店、大型场馆、休闲娱乐场馆等） 手机显示的网络标识:E 无信号出现的频率:总是 开关机情况:情况依旧#同步要素#</fieldContent></fieldInfo><fieldInfo><fieldChName>是否大面积投诉</fieldChName><fieldEnName>isWideComplaint</fieldEnName><fieldContent>否</fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent>三星</fieldContent></fieldInfo><fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>昭通</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类一级类别</fieldChName><fieldEnName>complaintType1</fieldEnName><fieldContent>移动业务</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类二级类别</fieldChName><fieldEnName>complaintType2</fieldEnName><fieldContent>网络质量</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类三级类别</fieldChName><fieldEnName>complaintType3</fieldEnName><fieldContent>手机上网（4G）</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类四级类别</fieldChName><fieldEnName>complaintType4</fieldEnName><fieldContent>网速慢或掉线</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类五级类别</fieldChName><fieldEnName>complaintType5</fieldEnName><fieldContent>功能使用</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类六级类别</fieldChName><fieldEnName>complaintType6</fieldEnName><fieldContent>网速慢或网页无法打开</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类七级类别</fieldChName><fieldEnName>complaintType7</fieldEnName><fieldContent>室外</fieldContent></fieldInfo><fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>13638815511</fieldContent></fieldInfo><fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>公园路168号</fieldContent></fieldInfo><fieldInfo><fieldChName>申告地</fieldChName><fieldEnName>mainComplaintPlace</fieldEnName><fieldContent>昭通</fieldContent></fieldInfo><fieldInfo><fieldChName>申告区县</fieldChName><fieldEnName>mainPlaceCountry</fieldEnName><fieldContent>昭阳区</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM工单号</fieldChName><fieldEnName>crmSheetId</fieldEnName><fieldContent>20190923204423X663036206</fieldContent></fieldInfo><fieldInfo><fieldChName>客服中心派单时间</fieldChName><fieldEnName>mainCustomerSerTime</fieldEnName><fieldContent>2019-09-23 20:50:25.0</fieldContent></fieldInfo><fieldInfo><fieldChName>CCH系统定界处理时间</fieldChName><fieldEnName>mainFaultCchTime</fieldEnName><fieldContent>2019-09-23 21:22:30.0</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉定位建议1</fieldChName><fieldEnName>mainFaultCchTypeOne</fieldEnName><fieldContent>&gt;cause: 用户原因-未见网络异常, NE type: CGISAI;MSISDN, NE: CGISAI=泸水县上橄榄-LHHN-002,泸水县新寨-LHHN-002;MSISDN=15008850290, suggestion: 如果有大量业务请确认 1.用户业务是否已经恢复正常 2.用户是否对网速期望过高或抱怨的其他非网络问题 3.是否终端突发异常 4.是否用户抱怨的是高制式网络没有覆盖 如果业务量非常少 疑似无线侧存在覆盖或性能故障问题, other: 用户投诉前在小区 泸水县新寨-LHHN-002有web业务共6.000次 在小区 泸水县上橄榄-LHHN-002,泸水县新寨-LHHN-002有OTT业务共38.000次; 用户终端型号 VIVO VIVO Y51A; 4G OTT业务38.000次 ; 4G Web业务6.000次; 3G OTT业务0.000次; 3G Web业务0.000次; 2G OTT业务 0.000次 ; 2G Web业务0.000次</fieldContent></fieldInfo>" +
//                "</recordInfo>" +
                "</opDetail>";

        xmlElementsList(sendSheet);

    }
}
