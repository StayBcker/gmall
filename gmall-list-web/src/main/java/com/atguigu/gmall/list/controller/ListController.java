package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    //从es中获取数据
    @RequestMapping("list")
    @ResponseBody
    public String getList(SkuLsParams skuLsParams){
        SkuLsResult search = listService.search(skuLsParams);
        return JSON.toJSONString(search);
    }
}
