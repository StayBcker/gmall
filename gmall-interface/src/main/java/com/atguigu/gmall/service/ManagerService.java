package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

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

    //记载spu属性列表
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    //查找销售属性
    List<BaseSaleAttr> getBaseSaleAttrList();

    //保存商品信息
    void saveSpuInfo(SpuInfo spuInfo);

    //根据spuId获取Spu图片
    List<SpuImage> getSpuImageList(String spuId);

    //根据spuId获取销售属性集合
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    //保存信息
    void saveSku(SkuInfo skuInfo);

    //========================================前台页面商品详情===============================================

    //根据skuId查找商品的信息，
    SkuInfo getSkuInfo(String skuId);

    //根据spuId，skuId查询一致对应的销售属性以及销售属性值
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    //根据spiId拼接属性值
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
