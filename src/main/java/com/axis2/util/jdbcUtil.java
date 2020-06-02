package com.axis2.util;

import java.sql.*;

/**
* Description 数据库工具类
* @param
* @Author junwei
* @Date 14:19 2020/6/2
**/
public class jdbcUtil {

    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String url = ConfigReaderUtils.getProperty("url");
    private static String user = ConfigReaderUtils.getProperty("username");
    private static String password = ConfigReaderUtils.getProperty("password");

    static{
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void release(Connection conn,Statement st,ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                rs = null;   //----> Java GC
            }
        }
        if(st != null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                st = null;
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                conn = null;
            }
        }
    }
}

