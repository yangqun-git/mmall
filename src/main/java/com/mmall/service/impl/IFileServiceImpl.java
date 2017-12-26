package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by yangqun on 2017/12/26.
 */
@Service("iFileService")
public class IFileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(IFileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        //获取文件的原始文件名
        String fileName = file.getOriginalFilename();
        //获取文件的后缀名,
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        //上传后的文件名
        String uploadFileName = UUID.randomUUID().toString() + fileExtensionName;

        logger.info("开始上传文件,原文件名称为:{},上传路径为:{},上传后的新文件名为:{}",fileName,path,uploadFileName);

        //创建文件保存需要的目录
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);      //赋予linux下写权限,以创建目录
            fileDir.mkdirs();
        }

        //封装上传文件的路径及文件名称
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);        //文件已经上传成功
            //上传到FTP服务器
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //删除掉upload目录
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传异常!",e);
            return null;
        }

        //返回处理后文件的文件名
        return targetFile.getName();
    }
}
