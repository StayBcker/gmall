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
        baseAttrValue.setAttrId(attrInfo.getId());
        List<BaseAttrValue> attrValues = baseAttrValueMapper.select(baseAttrValue);
        attrInfo.setAttrValueList(attrValues);
        return attrInfo;
    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }
}
