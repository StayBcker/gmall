package com.atguigu.gmall.manager.mapper;

import com.atguigu.gmall.bean.SkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<SkuInfo> {

//根据SpuId查询SkuInfo
    public List<SkuInfo> selectSkuInfoListBySpu(long spuId);

}
