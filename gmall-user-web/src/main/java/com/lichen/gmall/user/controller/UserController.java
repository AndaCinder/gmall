package com.lichen.gmall.user.controller;

import com.lichen.gmall.bean.UserAddress;
import com.lichen.gmall.bean.UserInfo;
import com.lichen.gmall.service.UserAddressService;
import com.lichen.gmall.service.UserInfoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 李琛
 * 2019/6/27 - 11:04
 */
@RestController
public class UserController {

    @Reference
    UserInfoService userInfoService;

    @Reference
    UserAddressService userAddressService;

    @RequestMapping("userInfoList")
    public List<UserInfo> userInfoList(){
        return userInfoService.selectAll();
    }

    @RequestMapping("getUserAddressList")
    public List<UserAddress> getUserAddressList(String userId){
        return userAddressService.getUserAddressList(userId);
    }

    @RequestMapping("getUserAddressByAddressId")
    public UserAddress getUserAddressByAddressId(String deliveryAddress){
        return userAddressService.getUserAddressByAddressId(deliveryAddress);
    }
}
