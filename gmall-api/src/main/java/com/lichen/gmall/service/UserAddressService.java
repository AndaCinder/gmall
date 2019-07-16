package com.lichen.gmall.service;

import com.lichen.gmall.bean.UserAddress;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/9 - 13:55
 */
public interface UserAddressService {
    List<UserAddress> getUserAddressList(String userId);

    UserAddress getUserAddressByAddressId(String deliveryAddress);
}
