package com.axis2.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Description excel数据读取
* @Author junwei
* @Date 10:31 2019/9/30
**/
public class ExcelUtil {

    public static final Logger logger=Logger.getLogger(ExcelUtil.class);
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel


    /**
     * Description 获取excel数据
     *
     * @param
     * @param
     * @Author junwei
     * @Date 16:15 2019/7/8
     *
     * @return*/
    public static List<List<Object>> getExeclData(String  filePath) {
//        LinkedList<String> list = new LinkedList<>();
        InputStream in = null;
        List<List<Object>> listExcel = null;
        String fileName=filePath.substring(filePath.lastIndexOf(File.separator)+1);
        try {
            //读取文件的数据。
            in = new FileInputStream(filePath);
            listExcel = new ExcelUtil().getExcelTitle(in, fileName,false);
            in.close();
            if (listExcel.size() == 0) {
                return null;
            }
            return listExcel;
        } catch (Exception e) {
            logger.error("错误信息：", e);
            return null;
        }

    }



    /**
    * Description 获取所有列包括xlsx表头
    * @param
    * @param
    * @Author junwei
    * @Date 10:34 2019/9/30
    **/
    public  List<List<Object>> getExcelTitle(InputStream in,String fileName,boolean ifAllSheet) throws Exception{
        List<List<Object>> list = null;

        //创建Excel工作薄
        Workbook work = this.getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        try {

            for (int i = 0; i < work.getNumberOfSheets(); i++) {
                if(!ifAllSheet){
                    if(i>0){
                        continue;
                    }
                }
                sheet = work.getSheetAt(i);
                if(sheet==null){continue;}

                //遍历当前sheet中的所有行
                for (int j = sheet.getFirstRowNum(); j < sheet.getLastRowNum()+1; j++) {
                    row = sheet.getRow(j);
                    if(row==null || "".equals(row)){continue;}
                    //遍历所有的列
                    List<Object> li = new ArrayList<Object>();
                    for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                        cell = row.getCell(y);
                        li.add(this.getCellValue(cell));
                    }
                    list.add(li);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("导入异常》》》》》》》",e);
            System.err.println(e);
        }finally{
            in.close();
        }
        return list;
    }




    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public  Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }


    /**
     * 根据excel单元格类型获取excel单元格值
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC: {
                    short format = cell.getCellStyle().getDataFormat();
                    if(format == 14 || format == 31 || format == 57 || format == 58){ 	//excel中的时间格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        double value = cell.getNumericCellValue();
                        Date date = DateUtil.getJavaDate(value);
                        cellvalue = sdf.format(date);
                    }
                    // 判断当前的cell是否为Date
                    else if (HSSFDateUtil.isCellDateFormatted(cell)) {  //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。
                        // 如果是Date类型则，取得该Cell的Date值           // 对2014-02-02格式识别不出是日期格式
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        cellvalue= formater.format(date);
                    } else { // 如果是纯数字
                        // 取得当前Cell的数值
                        cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());

                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getStringCellValue().replaceAll("'", "''");
                    break;
                case  HSSFCell.CELL_TYPE_BLANK:
                    cellvalue = null;
                    break;
                // 默认的Cell值
                default:{
                    cellvalue = " ";
                }
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }


}
