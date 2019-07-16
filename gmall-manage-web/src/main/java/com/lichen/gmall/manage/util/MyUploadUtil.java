package com.lichen.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 李琛
 * 2019/7/12 - 14:41
 */
public class MyUploadUtil {


    public static String uploadImage(MultipartFile file){
        //配置fdfs全局信息
        String path = MyUploadUtil.class.getClassLoader().getResource("tracker.conf").getFile();
        try {
            ClientGlobal.init(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        //获得tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = null;
        try {
            connection = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //通过tracker获得storage
        StorageClient storageClient = new StorageClient(connection, null);

        //通过storage上传文件
        String[] jpgs = new String[0];
        try {
            //通过最后一个.获取扩展名
            String originalFilename = file.getOriginalFilename();
            int i = originalFilename.lastIndexOf(".");
            String fileExtName = originalFilename.substring(i+1);
            jpgs = storageClient.upload_file(file.getBytes(), fileExtName, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        //拼接url
        String temp = "";
        for (int i = 0; i < jpgs.length; i++) {
            if (i != 0) {
                temp = jpgs[i-1];
                temp = temp+"/"+ jpgs[i];
            }
        }
        return temp;

    }
}
