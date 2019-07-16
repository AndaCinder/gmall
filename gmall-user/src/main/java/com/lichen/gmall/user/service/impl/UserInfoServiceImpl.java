package com.lichen.gmall.user.service.impl;

import com.lichen.gmall.bean.UserAddress;
import com.lichen.gmall.bean.UserInfo;
import com.lichen.gmall.service.UserInfoService;
import com.lichen.gmall.user.mapper.UserAddressMapper;
import com.lichen.gmall.user.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 李琛
 * 2019/6/27 - 11:08
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;


    @Override
    public List<UserInfo> selectAll() {
        return userInfoMapper.selectAll();
    }




}
