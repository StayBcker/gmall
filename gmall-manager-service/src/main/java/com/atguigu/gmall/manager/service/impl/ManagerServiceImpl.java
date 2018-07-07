package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manager.mapper.*;
import com.atguigu.gmall.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private  SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private  SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    //根据二级分类中的一级分类选中数据的ID查询二级分类数据
    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> baseCatalog2List = baseCatalog2Mapper.select(baseCatalog2);
        return baseCatalog2List;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> baseCatalog3List = baseCatalog3Mapper.select(baseCatalog3);
        return baseCatalog3List;
    }

    //获取属性，根据属性中三级分类的数据的ID
    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        //设置属性列表为选中三级分类数据指定ID的属性列表
        baseAttrInfo.setCatalog3Id(catalog3Id);
        //查询
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);
        return baseAttrInfos;
    }

    //dialog会话窗口点击保存，属性与属性值分开保存
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断是否有主键，有就是编辑 没有就是保存
        if (baseAttrInfo.getId()!=null&&baseAttrInfo.getId().length()>0){
            //有主键，修改
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }else{
            //如果没有主键的话，则需要设置ID为null  保存属性
            if(baseAttrInfo.getId().length()==0){
                baseAttrInfo.setId(null);
            }
            //开始插入数据 选择性插入
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        //操作属性值，先将属性值清空  保存属性值
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue);

        //开始操作属性值列表
        if (baseAttrInfo.getAttrValueList()!=null&&baseAttrInfo.getAttrValueList().size()>0){
            //循环baseAttrValueList
            for (BaseAttrValue attrValue :baseAttrInfo.getAttrValueList()) {
                //插入,先判断attrValue属性值ID有没有，没有的话则赋值为null
                if(attrValue.getId().length()==0){
                    attrValue.setId(null);
                }
                //属性值列表中的属性ID
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
    }

    //编辑属性值
    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        //获取选中得属性根据属性ID参数
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //获取属性值对象
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        //设置属性值对象得属性ID，使该属性值对象为选中得那个
        baseAttrValue.setAttrId(attrInfo.getId());  //ID必须为attrInfo的attr_ID，否则报错！！！
        List<BaseAttrValue> attrValues = baseAttrValueMapper.select(baseAttrValue);
        attrInfo.setAttrValueList(attrValues);
        return attrInfo;
    }

    //获取spu商品列表
    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    //获取销售属性List
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    //保存！！！！！
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存spuInfo字段 ，编辑 保存一起操作
        //先判断spuInfo是编辑，ID存在
        if (spuInfo.getId()!=null&&spuInfo.getId().length()>0){
            spuInfoMapper.updateByPrimaryKey(spuInfo);
        }else{
            //判断spuInfo是保存，ID不存在  选择性插入
            if (spuInfo.getId()!=null&&spuInfo.getId().length()==0){   //担心出现 "" 种情况

                spuInfo.setId(null);
            }
            spuInfoMapper.insertSelective(spuInfo);
        }

        //保存图片.先删除，再保存
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuInfo.getId());
        spuImageMapper.delete(spuImage);
        //保存之前先判断
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage image : spuImageList) {
            if(image.getId()!=null&&image.getId().length()==0){
                image.setId(null);
            }
            // 因为前台页面传递的数据没有spuId 所以设置 spuId
            image.setSpuId(spuInfo.getId());
            spuImageMapper.insertSelective(image);
        }

        //保存销售属性与值,先删除，再保存
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuInfo.getId());
        spuSaleAttrMapper.delete(spuSaleAttr);

        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(spuInfo.getId());
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);

        //获取spuInfo下的spuAttrList
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        //判断
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            if (saleAttr.getId()!=null&&saleAttr.getId().length()==0){
                saleAttr.setId(null);
            }
            saleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insertSelective(saleAttr);

            //获取每一个spuSaleAttr的spuAttrValueList
            List<SpuSaleAttrValue> spuSaleAttrValueList = saleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue saleAttrValue : spuSaleAttrValueList) {
                if(saleAttrValue.getId()!=null&&saleAttrValue.getId().length()==0){
                    saleAttrValue.setId(null);
                }
                saleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
        }
    }
}
