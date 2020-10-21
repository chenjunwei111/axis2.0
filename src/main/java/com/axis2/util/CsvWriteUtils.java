package com.axis2.util;

import com.alibaba.fastjson.JSONObject;
import com.axis2.ftp.Connection;
import com.axis2.ftp.XFtp;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**Created by scorpio on 2020/08/26
 * 下面的这段程序是Java将数据写入CSV文件中；
 * */

public class CsvWriteUtils
{
    private static final Logger log = Logger.getLogger(httpUtil.class.getClass());
    public  JSONObject createCsvFile(List rows, String filePath, String fileName,String dayId)
    {
        //标记文件生成是否成功；
        boolean flag = true;

        //文件输出流
        BufferedWriter fileOutputStream = null;
        String fullPath = filePath+ File.separator+dayId+"_"+fileName+".csv";
        Connection conn = null;

        try{
            //含文件名的全路径
//            String fullPath = filePath+ File.separator+fileName+".csv";
            File file = new File(fullPath);
            if (!file.getParentFile().exists())     //如果父目录不存在，创建父目录
            {
                file.getParentFile().mkdirs();
            }
            if (file.exists())     //如果该文件已经存在，删除旧文件
            {
                file.delete();
            }
            file = new File(fullPath);
            file.createNewFile();

            //格式化浮点数据
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMaximumFractionDigits(10);     //设置最大小数位为10；

            //格式化日期数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            //实例化文件输出流
            fileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"GB2312"),1024);

            //遍历输出每行
            Iterator  ite = rows.iterator();

            while (ite.hasNext())
            {
                Object[] rowData = (Object[])ite.next();
                for(int i=0;i<rowData.length;i++)
                {
                    Object obj = rowData[i];   //当前字段
                    //格式化数据
                    String field = "";
                    if (null != obj)
                    {
                        if (obj.getClass() == String.class)     //如果是字符串
                        {
                            field = (String)obj;
                        }else if (obj.getClass() == Double.class || obj.getClass() == Float.class)   //如果是浮点型
                        {
                            field = formatter.format(obj);   //格式化浮点数，使浮点数不以科学计数法输出
                        }else if (obj.getClass() == Integer.class || obj.getClass() == Long.class | obj.getClass() == Short.class || obj.getClass() == Byte.class)
                        {
                            //如果是整型
                            field += obj;
                        }else if (obj.getClass() == Date.class)   //如果是日期类型
                        {
                            field = sdf.format(obj);
                        }else {
                            field = " ";   //null时给一个空格占位
                        }
                        //拼接所有字段为一行数据
                        if (i<rowData.length-1)     //不是最后一个元素
                        {
//                            System.out.print("\""+field+"\""+",");
                            fileOutputStream.write("\""+field+"\""+",");
                        }else {
                            //最后一个元素
                            fileOutputStream.write("\""+field+"\"");
                        }
                    }
                    //创建一个新行
                    if (ite.hasNext())
                    {
                        //fileOutputStream.newLine();
                    }
                }
                fileOutputStream.newLine();     //换行，创建一个新行；
            }
            fileOutputStream.flush();
            //先把流文件关闭
            try{
                fileOutputStream.close();

            }catch (IOException e)
            {
                log.info("CSV文件写入后关闭异常，具体原因:"+e.toString());
            }

            long csvFileCheck =getTotalLines(file);
            //这个地方要做调整等于53不表明等于空文件
            if(csvFileCheck==1){
                log.info("查询无SEQ小区占用，删除空csv文件");
                file.delete();
            }

            //从本地把文件上传到FTP服务器(131)
            log.info("测试把文件从本地上传到服务器");
            XFtp ftp= new XFtp("10.174.238.10","spdb","accountPassw0rd",49161);
            conn = ftp.createConnection();
            conn.openxFtpChannel(Connection.xftpChannel.FTP);
            boolean res = conn.connectServer();
            log.info("连接FTP完成。。(" + res + ")");

            boolean ftpIsSuc=ftp.pushFile("//xiaotijun//complain//seq_detail//",fullPath);
            if(ftpIsSuc==true){
                //创建一个集合存储需要的数据
                JSONObject al=new JSONObject(new LinkedHashMap());
                //获取文件生成日期
                long FileCreateTime=file.lastModified();
                Date date = new Date(FileCreateTime);
                //获取文件大小
                DecimalFormat df = new DecimalFormat("#.00");
                long fileS = file.length();
                String size = df.format((double) fileS / 1024) + "KB";
                log.info("CSV文件传输到服务器正常");
                al.put("name","SEQ占用小区CSV文件");
                al.put("create_time",sdf.format(date));
                al.put("file_size",size);
                file.delete();
                return al;
            }else{
                log.info("CSV文件传输到服务器失败");
                return null;
            }

        }catch (Exception e)
        {
            flag = false;
            log.info("写入SEQ占用小区到CSV发生异常:"+e.toString());
        }
        return null;
    }


    //快速获取文件行数
    public long getTotalLines(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        long endTime = System.currentTimeMillis();
//        System.out.println("统计文件行数运行时间： " + (endTime - startTime) + "ms");
        return lines;
    }
}