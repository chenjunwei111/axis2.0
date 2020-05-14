package com.axis2.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.axis2.pojo.Pixel;
import com.axis2.pojo.WebDeCoding;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Description 地图工具类
* @Author junwei
* @Date 14:58 2019/9/27
**/
public class MapUtil {

    private final static String URL_DECODE = "https://api.map.baidu.com/?qt=s&c=%s&wd=%s&rn=%s&ie=utf-8&oue=1&fromproduct=jsapi&res=api&ak=E4805d16520de693a3fe707cdc962045";

//    private final static String URL_DECODE = "https://183.232.231.99/?qt=s&c=%s&wd=%s&rn=%s&ie=utf-8&oue=1&fromproduct=jsapi&res=api&ak=E4805d16520de693a3fe707cdc962045";



    private static Logger log = Logger.getLogger(MapUtil.class.getClass());


    /**
     * 百度地图逆编码
     *
     * @param addr      地址
     * @param localcity 所属地市，可为null
     * @param maxnum    获取最大结果数,默认10
     * @return DeCoding集合 Point为BD09
     * @throws Exception
     */
    public static List<WebDeCoding> getBaiDuPoint(String addr, String localcity, Integer maxnum) throws Exception {
        URL url;
        BufferedReader in = null;
        if (!CjwCommonUtils.strIsNotNull(addr)) {
            return null;
        }
        if (maxnum == null) {
            maxnum = 10;
        }
        try {
            System.setProperty("proxySet", "true");
            System.setProperty("proxyHost", "192.168.20.170");
            System.setProperty("proxyPort", "65123");
            url = new URL(String.format(URL_DECODE, getcode(localcity), addr, maxnum));
            log.info("地址1："+url);

            in = new BufferedReader(new InputStreamReader(url.openStream(), "GB2312"));
//            log.info("地址2："+in);
            if (in == null) {
                log.info("地址2.1："+in);
                return null;
            }
            JSONObject json = JSONObject.parseObject(in.readLine());
//            log.info("地址3："+json);

            if (json == null || json.isEmpty()) {
//                log.info("地址3.1："+json);
                return null;
            }
            List<WebDeCoding> codes = new ArrayList<>();
            JSONArray jsons = json.getJSONArray("content");
//            log.info("地址4："+jsons);
            Transformation ts = new Transformation();
            for (int i = 0; i < jsons.size(); i++) {
                JSONObject js = jsons.getJSONObject(i);
                String x = js.getString("x");
                String y = js.getString("y");
                if (x.isEmpty() || y.isEmpty()) {
                    continue;
                }
                WebDeCoding co = new WebDeCoding();
                co.setName(js.getString("name"));
                co.setArea(js.getString("area_name"));
                co.setAddr(js.getString("addr"));
                co.setPoint(ts.mc2pt(new Pixel(Double.parseDouble(x) / 100, Double.parseDouble(y) / 100)));
                codes.add(co);
            }
            return codes;
        } catch (Exception e1) {
            e1.printStackTrace();
            log.error("************************百度地图加载错误："+e1);
            return null;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


    private static String getcode(String city) {
        if (city == null || city.isEmpty()) {
            return "";
        }
        final String code = "拉萨市 100;那曲地区 101;日喀则地区 102;阿里地区 103;昆明市 104;楚雄彝族自治州 105;玉溪市 106;红河哈尼族彝族自治州 107;普洱市 108;西双版纳傣族自治州 109;临沧市 110;大理白族自治州 111;保山市 112;怒江傈僳族自治州 113;丽江市 114;迪庆藏族自治州 115;德宏傣族景颇族自治州 116;张掖市 117;武威市 118;东莞市 119;东沙群岛 120;三亚市 121;鄂州市 122;乌海市 123;莱芜市 124;海口市 125;蚌埠市 126;河南省直辖县级行政单位 1277;济源市 1277;合肥市 127;阜阳市 128;" +
                "芜湖市 129;安庆市 130;北京市 131;重庆市 132;南平市 133;泉州市 134;庆阳市 135;定西市 136;韶关市 137;佛山市 138;茂名市 139;珠海市 140;梅州市 141;桂林市 142;河池市 143;崇左市 144;钦州市 145;贵阳市 146;六盘水市 147;秦皇岛市 148;沧州市 149;石家庄市 150;邯郸市 151;新乡市 152;洛阳市 153;商丘市 154;许昌市 155;襄樊市 156;荆州市 157;长沙市 158;衡阳市 159;镇江市 160;南通市 161;淮安市 162;南昌市 163;新余市 164;通化市 165;锦州市 166;大连市 167;乌兰察布市 168;" +
                "巴彦淖尔市 169;渭南市 170;宝鸡市 171;枣庄市 172;日照市 173;东营市 174;威海市 175;太原市 176;文山壮族苗族自治州 177;温州市 178;杭州市 179;宁波市 180;中卫市 181;临夏回族自治州 182;辽源市 183;抚顺市 184;阿坝藏族羌族自治州 185;宜宾市 186;中山市 187;亳州市 188;滁州市 189;宣城市 190;廊坊市 191;宁德市 192;龙岩市 193;厦门市 194;莆田市 195;天水市 196;清远市 197;湛江市 198;阳江市 199;河源市 200;潮州市 201;来宾市 202;百色市 203;防城港市 204;铜仁地区 205;毕节地区 206;" +
                "承德市 207;衡水市 208;濮阳市 209;开封市 210;焦作市 211;三门峡市 212;平顶山市 213;信阳市 214;鹤壁市 215;十堰市 216;荆门市 217;武汉市 218;常德市 219;岳阳市 220;娄底市 221;株洲市 222;盐城市 223;苏州市 224;景德镇市 225;抚州市 226;本溪市 227;盘锦市 228;包头市 229;阿拉善盟 230;榆林市 231;铜川市 232;西安市 233;临沂市 234;滨州市 235;青岛市 236;朔州市 237;晋中市 238;巴中市 239;绵阳市 240;广安市 241;资阳市 242;衢州市 243;台州市 244;舟山市 245;固原市 246;甘南藏族自治州 247;" +
                "内江市 248;曲靖市 249;淮南市 250;巢湖市 251;黄山市 252;淮北市 253;三明市 254;漳州市 255;陇南市 256;广州市 257;云浮市 258;揭阳市 259;贺州市 260;南宁市 261;遵义市 262;安顺市 263;张家口市 264;唐山市 265;邢台市 266;安阳市 267;郑州市 268;驻马店市 269;宜昌市 270;黄冈市 271;益阳市 272;邵阳市 273;湘西土家族苗族自治州 274;郴州市 275;泰州市 276;宿迁市 277;宜春市 278;鹰潭市 279;朝阳市 280;营口市 281;丹东市 282;鄂尔多斯市 283;延安市 284;商洛市 285;济宁市 286;潍坊市 287;济南市 288;" +
                "上海市 289;晋城市 290;澳门特别行政区 2911;香港特别行政区 2912;南充市 291;丽水市 292;绍兴市 293;湖州市 294;北海市 295;海南省直辖县级行政单位 296;赤峰市 297;六安市 298;池州市 299;福州市 300;惠州市 301;江门市 302;汕头市 303;梧州市 304;柳州市 305;黔南布依族苗族自治州 306;保定市 307;周口市 308;南阳市 309;孝感市 310;黄石市 311;张家界市 312;湘潭市 313;永州市 314;南京市 315;徐州市 316;无锡市 317;吉安市 318;葫芦岛市 319;鞍山市 320;呼和浩特市 321;吴忠市 322;咸阳市 323;安康市 324;" +
                "泰安市 325;烟台市 326;吕梁市 327;运城市 328;广元市 329;遂宁市 330;泸州市 331;天津市 332;金华市 333;嘉兴市 334;石嘴山市 335;昭通市 336;铜陵市 337;肇庆市 338;汕尾市 339;嘉峪关市 33;深圳市 340;贵港市 341;黔东南苗族侗族自治州 342;黔西南布依族苗族自治州 343;漯河市 344;湖北省直辖县级行政单位 345;扬州市 346;连云港市 347;常州市 348;九江市 349;金昌市 34;萍乡市 350;辽阳市 351;汉中市 352;菏泽市 353;淄博市 354;大同市 355;长治市 356;阳泉市 357;马鞍山市 358;平凉市 359;白银市 35;银川市 360;" +
                "玉林市 361;咸宁市 362;怀化市 363;上饶市 364;赣州市 365;聊城市 366;忻州市 367;临汾市 368;达州市 369;兰州市 36;宿州市 370;随州市 371;德州市 372;恩施土家族苗族自治州 373;酒泉市 37;大兴安岭地区 38;黑河市 39;伊春市 40;齐齐哈尔市 41;佳木斯市 42;鹤岗市 43;绥化市 44;双鸭山市 45;鸡西市 46;七台河市 47;哈尔滨市 48;牡丹江市 49;大庆市 50;白城市 51;松原市 52;长春市 53;延边朝鲜族自治州 54;吉林市 55;四平市 56;白山市 57;沈阳市 58;阜新市 59;铁岭市 60;呼伦贝尔市 61;兴安盟 62;锡林郭勒盟 63;通辽市 64;" +
                "海西蒙古族藏族自治州 65;西宁市 66;海北藏族自治州 67;海南藏族自治州 68;海东地区 69;黄南藏族自治州 70;玉树藏族自治州 71;果洛藏族自治州 72;甘孜藏族自治州 73;德阳市 74;成都市 75;雅安市 76;眉山市 77;自贡市 78;乐山市 79;凉山彝族自治州 80;攀枝花市 81;和田地区 82;喀什地区 83;克孜勒苏柯尔克孜自治州 84;阿克苏地区 85;巴音郭楞蒙古自治州 86;新疆直辖县级行政单位 87;新疆维吾尔自治区直辖县级行政单位 87;博尔塔拉蒙古自治州 88;吐鲁番地区 89;伊犁哈萨克自治州 90;哈密地区 91;乌鲁木齐市 92;昌吉回族自治州 93;塔城地区 94;" +
                "克拉玛依市 95;阿勒泰地区 96;山南地区 97;林芝地区 98;昌都地区 99";
        List<String> coders = new ArrayList<String>(Arrays.asList(code.split(";")));
        for (String c : coders) {
            if (c.equals(city) || c.startsWith(city)) {
                return c.split(" ")[1].trim();
            }
        }
        return "";
    }


}

