package org.flitter.app.currency.eth.model;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "x_sale_eth")
public class SellEth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at")
    private Date createdat;

    private String currency;

    private Double price;

    private Double quantity;
    private Double maxprice;

    @Column(name = "x_interval")
    private Integer interval;

    @Column(name = "is_close")
    private Integer isclose;
    @Column(name = "orderid")
    private String orderid;
    @Column(name = "buy_id")
    private Long buyid;
    private String xuuid;
    private String xerror;
    @Column(name = "xuser_id", nullable = false)
    private Long userid;
    @Column(name = "is_close_external")
    private Integer iscloseexternal;
    public SellEth() {
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getXerror() {
        return xerror;
    }

    public void setXerror(String xerror) {
        this.xerror = xerror;
    }

    public String getXuuid() {
        return xuuid;
    }

    public void setXuuid(String xuuid) {
        this.xuuid = xuuid;
    }

    public Integer getIscloseexternal() {
        return iscloseexternal;
    }

    public void setIscloseexternal(Integer iscloseexternal) {
        this.iscloseexternal = iscloseexternal;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getIsclose() {
        return isclose;
    }

    public void setIsclose(Integer isclose) {
        this.isclose = isclose;
    }

    public Long getBuyid() {
        return buyid;
    }

    public void setBuyid(Long buyid) {
        this.buyid = buyid;
    }

    public Double getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(Double maxprice) {
        this.maxprice = maxprice;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", createdat=" + createdat +
                ", currency='" + currency + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", maxprice=" + maxprice +
                ", interval=" + interval +
                ", isclose=" + isclose +
                ", orderid='" + orderid + '\'' +
                ", buyid=" + buyid +
                ", xuuid='" + xuuid + '\'' +
                ", xerror='" + xerror + '\'' +
                ", iscloseexternal=" + iscloseexternal +
                '}';
    }
}
