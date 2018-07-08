package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SpuManageController {

    @Reference
    private ManagerService managerService;

    //跳转到spu商品列表页面
    @RequestMapping("spuListPage")
    public String spuListPage(){
        return "spuListPage";
    }

    //通过spuListPage页面，三级分类的时候刷新spuList,需要跳转到spuList页面
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> getSpuList(String catalog3Id){
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> spuInfoList = managerService.getSpuInfoList(spuInfo);
        return spuInfoList;
    }

    //查询销售属性列表，供选择
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> getBaseAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = managerService.getBaseSaleAttrList();
        return baseSaleAttrList;
    }

    //保存的时候是前台传的值 所以是POST请求
    @RequestMapping(value = "saveSpuInfo" ,method = RequestMethod.POST)
    @ResponseBody
    public void saveSpuInfo(SpuInfo spuInfo){
        managerService.saveSpuInfo(spuInfo);
    }

    //根据SpuId,从SpuImage中查询SkuImage
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> skuImageList(String spuId){
        return managerService.getSpuImageList(spuId);
    }

//    根据SpuID查询销售属性
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> getspuSaleAttrList(String spuId){
//        先获取销售属性的集合
        return managerService.getSpuSaleAttrList(spuId);

}
}
