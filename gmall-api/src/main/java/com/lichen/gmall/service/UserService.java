package com.lichen.gmall.service;

import com.lichen.gmall.bean.UserAddress;
import com.lichen.gmall.bean.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    List<UserInfo> userInfoList();

    UserInfo login(UserInfo userInfo);

    List<UserAddress> getUserAddressList(String userId);
}
