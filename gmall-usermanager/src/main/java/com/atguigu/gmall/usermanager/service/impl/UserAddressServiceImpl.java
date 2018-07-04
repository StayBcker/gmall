package com.atguigu.gmall.usermanager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import com.atguigu.gmall.usermanager.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


//远程调用 为提供者 被调用 将项目部署到阿里巴巴的dubbo中的zookeeper下
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        // 创建用户地址对象
        UserAddress userAddress = new UserAddress();
        // 将用户的id 传递给对象
        userAddress.setUserId(userId);
        // 使用通用mapper 查出信息
        List<UserAddress> listUserAddress = userAddressMapper.select(userAddress);
        // 将信息返回
        return listUserAddress;
    }
}
