package com.lichen.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lichen.gmall.bean.UserInfo;
import com.lichen.gmall.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("/userInfoList")
    public ResponseEntity<List<UserInfo>> userInfoList() {
        List<UserInfo> userInfoList = userService.userInfoList();
        return ResponseEntity.ok(userInfoList);
    }

}
