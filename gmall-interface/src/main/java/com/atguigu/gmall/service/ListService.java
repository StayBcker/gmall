package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {

    //skuLsInfo 代表是检索出来的skuInfo保存到skunInfo中，保存到es中
    void saveSkuInfo(SkuLsInfo skuLsInfo);

    //从es中查询数据 根据查询参数
    SkuLsResult search(SkuLsParams skuLsParams);
}
