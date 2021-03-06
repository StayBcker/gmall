package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class BaseCatalog2 implements Serializable {
    @Id
    @Column
    private String id;
    @Column
    private String name;
//    二级分类的数据需要根据一级分类的Id来选择加载
    @Column
    private String catalog1Id;

    public BaseCatalog2(String id, String name, String catalog1Id) {
        this.id = id;
        this.name = name;
        this.catalog1Id = catalog1Id;
    }

    public BaseCatalog2() {

    }

    @Override
    public String toString() {
        return "BaseCatalog2{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", catalog1Id='" + catalog1Id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog1Id = catalog1Id;
    }
}
