package com.axis2.test;


import com.axis2.ftp.Connection;
import com.axis2.ftp.XFtp;

/**
 * Description 测试FTP下载
 *
 * @Author junwei
 * @Date 16:05 2019/9/29
 **/
public class TestFtpDown {

    public static void main(String[] args) {
             XFtp ftp = null;
             Connection conn = null;
        try {
//            String host="192.168.9.179";
//            String user="spdbFTP";
//            String pass="dtkd_123321";


            String host="10.174.239.221";
            String user="boco2";
            String pass="B!oco221*";

            ftp = new XFtp(host, user,pass, 21);
            conn = ftp.createConnection();
            conn.openxFtpChannel(Connection.xftpChannel.FTP);
           if(conn.connectServer()){
               System.out.println("连接成功");
           }
            String path="/export/eoms35/uploadfile/accessories/uploadfile/sheet/complaint/";
            String fileName="20190905153715118.xls";
//            String fileName="tes.txt";
            String localhost="D:\\complaintLogs\\";
            Boolean b= ftp.receiveFile(path, fileName, localhost);
            System.out.println(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (conn != null) {
                conn.disConnectServer();
            }
        }

    }

}
