package org.flitter.app.currency.etc.model;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "x_profit_etc")
public class ProfitEtc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at")
    private Date createdat;

    private String currency;

    @Column(name = "x_sum")
    private Double xsum;

    @Column(name = "z_sum")
    private Double zsum;

    @Column(name = "buy_id")
    private Long buyid;

    @Column(name = "sale_id")
    private Long saleid;

    @Column(name = "is_sheduled")
    private Integer issheduled;

    @Column(name = "is_close_external")
    private Integer iscloseexternal;
    @Column(name = "xuser_id", nullable = false)
    private Long userid;
    private String xuuid;

    public ProfitEtc() {
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
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

    public Integer getIssheduled() {
        return issheduled;
    }

    public void setIssheduled(Integer issheduled) {
        this.issheduled = issheduled;
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

    public Double getXsum() {
        return xsum;
    }

    public void setXsum(Double xsum) {
        this.xsum = xsum;
    }

    public Long getBuyid() {
        return buyid;
    }

    public void setBuyid(Long buyid) {
        this.buyid = buyid;
    }

    public Long getSaleid() {
        return saleid;
    }

    public void setSaleid(Long saleid) {
        this.saleid = saleid;
    }

    public Double getZsum() {
        return zsum;
    }

    public void setZsum(Double zsum) {
        this.zsum = zsum;
    }

    @Override
    public String toString() {
        return "Profit{" +
                "id=" + id +
                ", createdat=" + createdat +
                ", currency='" + currency + '\'' +
                ", xsum=" + xsum +
                ", zsum=" + zsum +
                ", buyid=" + buyid +
                ", saleid=" + saleid +
                ", issheduled=" + issheduled +
                ", iscloseexternal=" + iscloseexternal +
                ", xuuid='" + xuuid + '\'' +
                '}';
    }


}
