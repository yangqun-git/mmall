package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yangqun on 2017/12/26.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
