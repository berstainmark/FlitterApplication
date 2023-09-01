package org.flitter.app.model;


import jakarta.persistence.*;

@Entity
@Table(name = "x_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String hisskey;

    private String apikey;

    private String secretkey;

    private String tradingtime;

    private Double dealamount;

    private String tradingrisk;

    private Double margincall;

    private Double balance;

    private Double profit;

    private Integer deals;

    private Integer adaLimit;
    private Double adaBalance;

    private Integer btcLimit;
    private Double btcBalance;

    private Integer dogLimit;
    private Double dogBalance;

    private Integer etcLimit;
    private Double etcBalance;

    private Integer ethLimit;
    private Double ethBalance;

    private Integer lnkLimit;
    private Double lnkBalance;

    private Integer ltcLimit;
    private Double ltcBalance;

    private Integer matLimit;
    private Double matBalance;

    private Integer solLimit;
    private Double solBalance;

    private Integer xrpLimit;
    private Double xrpBalance;

    private Double spreadAda;
    private Double spreadBtc;
    private Double spreadDog;
    private Double spreadEtc;
    private Double spreadEth;
    private Double spreadLnk;
    private Double spreadLtc;
    private Double spreadMat;
    private Double spreadSol;
    private Double spreadXrp;

    public User() {
    }

    public Double getSpreadBtc() {
        return spreadBtc;
    }

    public void setSpreadBtc(Double spreadBtc) {
        this.spreadBtc = spreadBtc;
    }

    public Double getSpreadDog() {
        return spreadDog;
    }

    public void setSpreadDog(Double spreadDog) {
        this.spreadDog = spreadDog;
    }

    public Double getSpreadEtc() {
        return spreadEtc;
    }

    public void setSpreadEtc(Double spreadEtc) {
        this.spreadEtc = spreadEtc;
    }

    public Double getSpreadEth() {
        return spreadEth;
    }

    public void setSpreadEth(Double spreadEth) {
        this.spreadEth = spreadEth;
    }

    public Double getSpreadLnk() {
        return spreadLnk;
    }

    public void setSpreadLnk(Double spreadLnk) {
        this.spreadLnk = spreadLnk;
    }

    public Double getSpreadLtc() {
        return spreadLtc;
    }

    public void setSpreadLtc(Double spreadLtc) {
        this.spreadLtc = spreadLtc;
    }

    public Double getSpreadMat() {
        return spreadMat;
    }

    public void setSpreadMat(Double spreadMat) {
        this.spreadMat = spreadMat;
    }

    public Double getSpreadSol() {
        return spreadSol;
    }

    public void setSpreadSol(Double spreadSol) {
        this.spreadSol = spreadSol;
    }

    public Double getSpreadXrp() {
        return spreadXrp;
    }

    public void setSpreadXrp(Double spreadXrp) {
        this.spreadXrp = spreadXrp;
    }

    public Double getSpreadAda() {
        return spreadAda;
    }

    public void setSpreadAda(Double spreadAda) {
        this.spreadAda = spreadAda;
    }

    public Integer getDogLimit() {
        return dogLimit;
    }

    public void setDogLimit(Integer dogLimit) {
        this.dogLimit = dogLimit;
    }

    public Double getDogBalance() {
        return dogBalance;
    }

    public void setDogBalance(Double dogBalance) {
        this.dogBalance = dogBalance;
    }

    public Integer getBtcLimit() {
        return btcLimit;
    }

    public void setBtcLimit(Integer btcLimit) {
        this.btcLimit = btcLimit;
    }

    public Double getBtcBalance() {
        return btcBalance;
    }

    public void setBtcBalance(Double btcBalance) {
        this.btcBalance = btcBalance;
    }

    public String getHisskey() {
        return hisskey;
    }

    public void setHisskey(String hisskey) {
        this.hisskey = hisskey;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getTradingtime() {
        return tradingtime;
    }

    public void setTradingtime(String tradingtime) {
        this.tradingtime = tradingtime;
    }

    public Double getDealamount() {
        return dealamount;
    }

    public void setDealamount(Double dealamount) {
        this.dealamount = dealamount;
    }

    public String getTradingrisk() {
        return tradingrisk;
    }

    public void setTradingrisk(String tradingrisk) {
        this.tradingrisk = tradingrisk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMargincall() {
        return margincall;
    }

    public void setMargincall(Double margincall) {
        this.margincall = margincall;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Integer getDeals() {
        return deals;
    }

    public void setDeals(Integer deals) {
        this.deals = deals;
    }

    public Integer getAdaLimit() {
        return adaLimit;
    }

    public void setAdaLimit(Integer adaLimit) {
        this.adaLimit = adaLimit;
    }

    public Double getAdaBalance() {
        return adaBalance;
    }

    public void setAdaBalance(Double adaBalance) {
        this.adaBalance = adaBalance;
    }

    public Integer getEtcLimit() {
        return etcLimit;
    }

    public void setEtcLimit(Integer etcLimit) {
        this.etcLimit = etcLimit;
    }

    public Double getEtcBalance() {
        return etcBalance;
    }

    public void setEtcBalance(Double etcBalance) {
        this.etcBalance = etcBalance;
    }

    public Integer getEthLimit() {
        return ethLimit;
    }

    public void setEthLimit(Integer ethLimit) {
        this.ethLimit = ethLimit;
    }

    public Double getEthBalance() {
        return ethBalance;
    }

    public void setEthBalance(Double ethBalance) {
        this.ethBalance = ethBalance;
    }

    public Integer getLnkLimit() {
        return lnkLimit;
    }

    public void setLnkLimit(Integer lnkLimit) {
        this.lnkLimit = lnkLimit;
    }

    public Double getLnkBalance() {
        return lnkBalance;
    }

    public void setLnkBalance(Double lnkBalance) {
        this.lnkBalance = lnkBalance;
    }

    public Integer getLtcLimit() {
        return ltcLimit;
    }

    public void setLtcLimit(Integer ltcLimit) {
        this.ltcLimit = ltcLimit;
    }

    public Double getLtcBalance() {
        return ltcBalance;
    }

    public void setLtcBalance(Double ltcBalance) {
        this.ltcBalance = ltcBalance;
    }

    public Integer getMatLimit() {
        return matLimit;
    }

    public void setMatLimit(Integer matLimit) {
        this.matLimit = matLimit;
    }

    public Double getMatBalance() {
        return matBalance;
    }

    public void setMatBalance(Double matBalance) {
        this.matBalance = matBalance;
    }

    public Integer getSolLimit() {
        return solLimit;
    }

    public void setSolLimit(Integer solLimit) {
        this.solLimit = solLimit;
    }

    public Double getSolBalance() {
        return solBalance;
    }

    public void setSolBalance(Double solBalance) {
        this.solBalance = solBalance;
    }

    public Integer getXrpLimit() {
        return xrpLimit;
    }

    public void setXrpLimit(Integer xrpLimit) {
        this.xrpLimit = xrpLimit;
    }

    public Double getXrpBalance() {
        return xrpBalance;
    }

    public void setXrpBalance(Double xrpBalance) {
        this.xrpBalance = xrpBalance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hisskey='" + hisskey + '\'' +
                ", apikey='" + apikey + '\'' +
                ", secretkey='" + secretkey + '\'' +
                ", tradingtime='" + tradingtime + '\'' +
                ", dealamount=" + dealamount +
                ", tradingrisk='" + tradingrisk + '\'' +
                ", margincall=" + margincall +
                ", balance=" + balance +
                '}';
    }
}
