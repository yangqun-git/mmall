package com.mmall.utils;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by yangqun on 2017/12/26.
 */
public class FTPUtil {
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("连接FTP服务器,开始上传文件... ...");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("上传文件结束,上传结果:{}",result);
        return result;
    }
    private boolean uploadFile(String romatePath,List<File> fileList) throws IOException {
        boolean upload = true;
        FileInputStream fis = null;
        //链接 FTP 服务
        if(connectServer(this.getIp(),this.getPort(),this.getUser(),this.getPwd())){
            try {
                ftpClient.changeWorkingDirectory(romatePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);      //设置成二进制流
                ftpClient.enterLocalPassiveMode();                      //打开ftp的被动模式
                for (File fileItem :fileList ){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                upload = false;
                e.printStackTrace();
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return upload;
    }
    private boolean connectServer(String ip,int port,String user,String pwd){
        ftpClient = new FTPClient();
        boolean isSuccess = false;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("链接FTP服务器异常!",e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public FTPClient getFtpClient() {
        return ftpClient;
    }
    public void setFTPClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
