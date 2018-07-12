package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManagerService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

//左侧属性列表页面增加controller
@Controller
public class AttrManageController {

    @Reference
    private ManagerService managerService;

    @Reference
    private ListService listService;

    /**
     * 获得一级分类
     * 因为一级分类的数据是从页面上传递的，所以需要提供说明参数传递的方法是POST
     * @return
     */
    @RequestMapping(value = "getCatalog1",method = RequestMethod.POST)
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){
        List<BaseCatalog1> catalog1 = managerService.getCatalog1();
        return catalog1;
    }

    /**
     *获得二级分类
     * @return
     */
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return managerService.getCatalog2(catalog1Id);
    }

    /**
     * 获得三级分类
     * @return
     */
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
       return managerService.getCatalog3(catalog2Id);
    }

    /**
     * 获得属性
     * 方法名attrInfoList是因为调用页面中的$('#dg').datagrid({url: 'attrInfoList?catalog3Id='+ctg3val});中的方法，必须一致
     * @return
     */
    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return managerService.getAttrList(catalog3Id);
    }


    @RequestMapping("attrListPage")
    public String getAttrListPage(){
        return "attrListPage";
    }

    //保存属性 dialog会话窗口点击保存按钮
    @RequestMapping(value = "saveAttrInfo",method = RequestMethod.POST)
    @ResponseBody
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo){
        managerService.saveAttrInfo(baseAttrInfo);
    }

    //编辑属性值,先查询出来需要编辑得属性，在针对其进行编辑属性值
    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<BaseAttrValue> getAttrValueList(String attrId){
        //根据attrId查询
        BaseAttrInfo attrInfo = managerService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();
    }

    //根据skuIn将商品skuInfo保存到es中定义的skuLsInfo
    @RequestMapping(value = "onSale" ,method = RequestMethod.GET)
    @ResponseBody
    public void onSale(String skuId){
        SkuInfo skuInfo = managerService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //目标文件是 skuLsInfo，需要将查找出来的skuInfo保存到es中定义好的skuLsInfo中
        try {
            BeanUtils.copyProperties(skuLsInfo,skuInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //保存，将商品销售属性保存到es中 需要上架的商品销售属性
        listService.saveSkuInfo(skuLsInfo);
    }
}
