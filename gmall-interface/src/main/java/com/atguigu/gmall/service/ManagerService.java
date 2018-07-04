package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;

import java.util.List;

public interface ManagerService {
    //获取一级，二级，三级 分类数据
    List<BaseCatalog1> getCatalog1();
    List<BaseCatalog2> getCatalog2(String catalog1Id);
    List<BaseCatalog3> getCatalog3(String catalog2Id);
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    //保存编辑属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    BaseAttrInfo getAttrInfo(String attrId);

}