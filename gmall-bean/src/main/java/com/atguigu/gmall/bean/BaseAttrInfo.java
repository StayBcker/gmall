package com.atguigu.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class BaseAttrInfo implements Serializable{

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) //获取主键自增信息
    private String id;
    @Column
    private String attrName;
    //列表的属性显示需要根据三级分类选择的I的显示
    @Column
    private String catalog3Id;
    //属性的集合,如果实体类中没有，但是表中需要的时候，则需要添加@Transient注解
    @Transient
    private List<BaseAttrValue> attrValueList;

    public List<BaseAttrValue> getAttrValueList() {
        return attrValueList;
    }

    public void setAttrValueList(List<BaseAttrValue> attrValueList) {
        this.attrValueList = attrValueList;
    }

    public BaseAttrInfo() {
    }

    public BaseAttrInfo(String id, String attrName, String catalog3Id) {

        this.id = id;
        this.attrName = attrName;
        this.catalog3Id = catalog3Id;
    }

    @Override
    public String toString() {
        return "BaseAttrInfo{" +
                "id='" + id + '\'' +
                ", attrName='" + attrName + '\'' +
                ", catalog3Id='" + catalog3Id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }
}
