package com.lichen.gmall.user.service.impl;

import com.lichen.gmall.bean.UserAddress;
import com.lichen.gmall.service.UserAddressService;
import com.lichen.gmall.user.mapper.UserAddressMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/9 - 13:54
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserAddress getUserAddressByAddressId(String deliveryAddress) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(deliveryAddress);
        return userAddressMapper.selectOne(userAddress);
    }
}
