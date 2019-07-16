package com.lichen.gmall.service;

import com.lichen.gmall.bean.UserAddress;
import com.lichen.gmall.bean.UserInfo;

import java.util.List;

/**
 * @author 李琛
 * 2019/6/27 - 11:08
 */
public interface UserInfoService {
    List<UserInfo> selectAll();
}
