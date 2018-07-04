package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class OrderController {

    //远程调用  跨项目调用ServiceImpl,所以需要将@Autowried换成@Reference   消费端 去调用提供者提供的方法
    @Reference
    private UserAddressService userAddressService;
    @RequestMapping("trade")
    public List<UserAddress> trade(HttpServletRequest request){
        String userId = request.getParameter("userId");
        List<UserAddress> userAddressList = userAddressService.getUserAddressList(userId);
        return userAddressList;
    }
}
