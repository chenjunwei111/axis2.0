package com.axis2.util;

import com.csvreader.CsvReader;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Description csv文件工具类
* @Author junwei
* @Date 11:17 2019/10/23
**/
public class CsvUtils {

    private static final Logger log = Logger.getLogger(CsvUtils.class.getClass());

    /**
     * Description 获取csv数据
     * @param filePath 例：(D:\test\20191022093947634\DETAIL_CDR_S1MME_20191022090549987.csv)
     * @Author junwei
     * @Date 16:15 2019/7/8
     **/
    public static ArrayList<List<Object>> getCsvData(String  filePath) throws IOException {
           InputStream in = null;
           CsvReader reader=null;
        try {
            in = new FileInputStream(filePath);
//             用来保存数据
            ArrayList<List<Object>> csvFileList = new ArrayList<>();
            // 定义一个CSV路径
            // 创建CSV读对象 例如:CsvReader(文件路径，分隔符，编码格式);
             reader = new CsvReader(in, ',', Charset.forName("GBK"));
            // 跳过表头 如果需要表头的话，这句可以忽略
            reader.readHeaders();
            // 逐行读入除表头的数据
            while (reader.readRecord()) {
                String arr[]=reader.getRawRecord().replace("\"","").split(",");
                List<Object> resultList= new ArrayList<Object>(Arrays.asList(arr));
                csvFileList.add(resultList);
            }
            reader.close();
            in.close();
            return csvFileList;
        } catch (IOException e) {
            log.error("************************",e);
            in.close();
            return null;
        }finally {

            reader.close();
            in.close();
            reader=null;
            in=null;

        }
    }


    public static void main(String[] args) throws IOException {

        String filePath="D:\\test\\20191022093947634\\DETAIL_CDR_S1MME_20191022090549987.csv";
//        String filePath="D:\\test\\20191022093947634\\DETAIL_CDR_GbIuPS_20191022090401190.csv";
        ArrayList<List<Object>> list=getCsvData(filePath);
        System.out.println(list);
    }
}
