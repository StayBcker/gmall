package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manager.constant.ManageConst;
import com.atguigu.gmall.manager.mapper.*;
import com.atguigu.gmall.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

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

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private RedisUtil redisUtil;
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
//        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//        //设置属性列表为选中三级分类数据指定ID的属性列表
//        baseAttrInfo.setCatalog3Id(catalog3Id);
//        //查询
//        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);
//        return baseAttrInfos;
        return baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(Long.parseLong(catalog3Id));
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

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrList(Long.parseLong(spuId));
        return spuSaleAttrs;
    }

    //保存Sku信息
    @Override
    public void saveSku(SkuInfo skuInfo) {
        //添加SkuInfo数据
        if (skuInfo.getId()==null || skuInfo.getId().length()==0){
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        } else {
            skuInfoMapper.updateByPrimaryKey(skuInfo);
        }

        // 先删除，再添加
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        // SkuId = SkuInfo.id
        skuAttrValue.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue);

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            // 坑！
            attrValue.setSkuId(skuInfo.getId());
            if (attrValue.getId()!=null&& attrValue.getId().length()==0){
                attrValue.setId(null);
            }
            skuAttrValueMapper.insertSelective(attrValue);
        }
        // 属性值添加
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuInfo.getId());
            if (saleAttrValue.getId()!=null && saleAttrValue.getId().length()==0){
                saleAttrValue.setSkuId(null);
            }
            skuSaleAttrValueMapper.insertSelective(saleAttrValue);
        }
        // 图片添加
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        skuImageMapper.delete(skuImage);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage image : skuImageList) {
            image.setSkuId(skuInfo.getId());
            if (image.getId()!=null && image.getId().length()==0){
                image.setId(null);
            }
            skuImageMapper.insertSelective(image);
        }

    }

//======================================前台页面商品详情==========================================
    //根据skuId查询商品信息，图片需要获得，根据skuId
    @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo=null;
        Jedis jedis = redisUtil.getJedis();
        //根据传入的skuId获取唯一的key
        String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
        //判断key在不在redis中
        if(jedis.exists("skuInfoKey")){
            //如果存在，则从redis中获取
            String skuInfoJson = jedis.get("SkuInfoKey");
            //判断获取到的skuInfoJson是不是空
            if (skuInfoJson!=null && !"".equals(skuInfoJson)){
                //不是的话则将字符串转换为对象
                skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                return skuInfo;
            }
        }else{
            //不存在就从数据库中获取
            skuInfo = getSkuInfoDB(skuId);
            //将对象转换为JSON字符串
            jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,JSON.toJSONString(skuInfo));
            return skuInfo;
        }
        return skuInfo;
    }

    //方法提取之后
    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //创建skuImage对象，
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        //获取sku图片列表
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        //将图片列表放到选中的商品信息中
        skuInfo.setSkuImageList(skuImageList);
        //给skuInfo添加销售属性
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        //给skuInfo添加属性值
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueMapper.select(skuSaleAttrValue));

        return skuInfo;
    }

    //根据spuId，skuId查询一致对应的销售属性以及销售属性值方法的实现
    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(Long.parseLong(skuInfo.getId()), Long.parseLong(skuInfo.getSpuId()));
        return spuSaleAttrs;
    }

    //根据spuId拼接属性值方法实现
    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return skuSaleAttrValues;
    }

}
