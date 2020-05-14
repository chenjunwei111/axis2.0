package com.axis2.util;

/**
* Description jw公共方法
* @Author junwei
* @Date 15:06 2019/9/27
**/
public class CjwCommonUtils {

    /**
     * Description 判断字符串 非null非空非undefined
     * @param str 判断字符串
     * @Author junwei
     * @Date 16:37 2019/7/4
     **/
    public static Boolean strIsNotNull(Object str){
        String nullStr="null";
        String undefStr="undefined";
        if(str != null && !nullStr.equals(str) && !undefStr.equals(str) && !"".equals(str)){
            return true;
        }else{
            return false;
        }
    }
}
