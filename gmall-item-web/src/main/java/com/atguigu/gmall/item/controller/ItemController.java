package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManagerService managerService;

        // {skuId}.html 是用户页面选中的Sku传入的skuId值，动态获取
    @RequestMapping("{skuId}.html")
    public String skuInfoPage (@PathVariable(value = "skuId") String skuId , Model model){
        SkuInfo skuInfo = managerService.getSkuInfo(skuId);
        //将skuInfo存入到Model中，后面需要从Model中取值 键值对
        model.addAttribute("skuInfo",skuInfo);

        //显示销售属性，销售属性值
        List<SpuSaleAttr> saleAttrList = managerService.selectSpuSaleAttrListCheckBySku(skuInfo);
        model.addAttribute("saleAttrList",saleAttrList);
        // 组装后台传递到前台的json字符串
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = managerService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        // "1|20" : "17"  "2|10" : "17" valueIdsKey="1|20"  map.put(valueIdsKey,skuId);
        // 先声明一个字符串
        String valueIdsKey = "";
        // 需要定一个map集合
        HashMap<String, String> map = new HashMap<>();
        // 循环拼接
        for (int i = 0; i <skuSaleAttrValueListBySpu.size() ; i++) {
            // 取得第一个值
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
            // 什么时候加|
            if (valueIdsKey.length()>0){
                valueIdsKey+="|";
            }
            valueIdsKey+=skuSaleAttrValue.getSaleAttrValueId();

            // 什么时候停止拼接
            if ((i+1)==skuSaleAttrValueListBySpu.size()|| !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){
                map.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey="";
            }
        }
        // 将map 转换成json字符串
        String valueJson = JSON.toJSONString(map);
        System.out.println("valueJson:="+valueJson);
        // 放到前台使用！
        model.addAttribute("valuesSkuJson",valueJson);
        return "item";
    }
}