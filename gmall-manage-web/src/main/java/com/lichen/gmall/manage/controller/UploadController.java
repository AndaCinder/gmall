package com.lichen.gmall.manage.controller;

import com.lichen.gmall.manage.util.MyUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传
 * @author 李琛
 * 2019/7/12 - 14:56
 */
@Controller
public class UploadController {

    @Value("${fileServer.url}")
    String fileUrl;

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){
        String image = MyUploadUtil.uploadImage(file);
        String tempFileUrl = fileUrl;
        tempFileUrl = tempFileUrl +"/"+ image;
        return tempFileUrl;
    }

}
