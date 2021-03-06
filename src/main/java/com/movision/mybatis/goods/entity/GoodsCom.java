package com.movision.mybatis.goods.entity;


import java.io.Serializable;

/**
 * @Author zhanglei
 * @Date 2017/3/4 17:48
 */
public class GoodsCom implements Serializable {
    public Integer getComboid() {
        return comboid;
    }

    public void setComboid(Integer comboid) {
        this.comboid = comboid;
    }

    public Integer getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Integer goodsid) {
        this.goodsid = goodsid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getOrigprice() {
        return origprice;
    }

    public void setOrigprice(Double origprice) {
        this.origprice = origprice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    private Integer comboid;
    private Integer goodsid;
    private String name;
    private Double origprice;
    private Double price;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    private Integer stock;
    private Integer sales;
    private String attribute;
    private Integer id;
    private String typename;
    private String brandname;
    private Integer isdel;

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }


    private Double sumorigprice;//总价格
    private Double sumprice;//总折扣价
    private Integer sumsales;//总销量
    private Integer sumstock;//总库存

    public Double getSumorigprice() {
        return sumorigprice;
    }

    public void setSumorigprice(Double sumorigprice) {
        this.sumorigprice = sumorigprice;
    }

    public Double getSumprice() {
        return sumprice;
    }

    public void setSumprice(Double sumprice) {
        this.sumprice = sumprice;
    }

    public Integer getSumsales() {
        return sumsales;
    }

    public void setSumsales(Integer sumsales) {
        this.sumsales = sumsales;
    }

    public Integer getSumstock() {
        return sumstock;
    }

    public void setSumstock(Integer sumstock) {
        this.sumstock = sumstock;
    }
}
