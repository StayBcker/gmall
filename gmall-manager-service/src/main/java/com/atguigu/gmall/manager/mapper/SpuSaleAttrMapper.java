package com.atguigu.gmall.manager.mapper;

import com.atguigu.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    //根据spuId查询销售属性值列表
    List<SpuSaleAttr> selectSpuSaleAttrList(long spuId);
    //根据spuId，skuId 查询属性 属性值列表  spuId 为skuInfo表里的spuId
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(long skuId,long spuId);

}
