package org.flitter.app.currency.lnk.model;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "x_buy_lnk")
public class BuyLnk {
    @Column(name = "diff_price")
    Double diffprice;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "created_at")
    private Date createdat;


    @Column(name = "lotsize")
    private Double lotsize;
    private String xerror;

    @Column(name = "orderid")
    private String orderid;

    private Double price;

    private Double tradeprice;

    private Double maxprice;

    private Double quantity;


    @Column(name = "is_close")
    private Integer isclose;

    @Column(name = "is_close_external")
    private Integer iscloseexternal;

    @Column(name = "xuser_id", nullable = false)
    private Long userid;
    @Column(name = "is_sheduled")
    private Integer issheduled;
    private String xuuid;

    public BuyLnk() {
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getIscloseexternal() {
        return iscloseexternal;
    }

    public void setIscloseexternal(Integer iscloseexternal) {
        this.iscloseexternal = iscloseexternal;
    }

    public String getXerror() {
        return xerror;
    }

    public void setXerror(String xerror) {
        this.xerror = xerror;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Double getTradeprice() {
        return tradeprice;
    }

    public void setTradeprice(Double tradeprice) {
        this.tradeprice = tradeprice;
    }

    public Integer getIssheduled() {
        return issheduled;
    }

    public void setIssheduled(Integer issheduled) {
        this.issheduled = issheduled;
    }

    public String getXuuid() {
        return xuuid;
    }

    public void setXuuid(String xuuid) {
        this.xuuid = xuuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Date createdat) {
        this.createdat = createdat;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(Double maxprice) {
        this.maxprice = maxprice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }


    public Integer getIsclose() {
        return isclose;
    }

    public void setIsclose(Integer isclose) {
        this.isclose = isclose;
    }

    public Double getLotsize() {
        return lotsize;
    }

    public void setLotsize(Double lotsize) {
        this.lotsize = lotsize;
    }


    public Double getDiffprice() {
        return diffprice;
    }

    public void setDiffprice(Double diffprice) {
        this.diffprice = diffprice;
    }


}
