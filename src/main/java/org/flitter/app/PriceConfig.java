package org.flitter.app;

import java.util.Date;


public class PriceConfig {

    private static Double adaPrice = 0.0;
    private static Double btcPrice = 0.0;
    private static Double dogPrice = 0.0;
    private static Double etcPrice = 0.0;
    private static Double ethPrice = 0.0;
    private static Double lnkPrice = 0.0;
    private static Double ltcPrice = 0.0;
    private static Double matPrice = 0.0;
    private static Double solPrice = 0.0;
    private static Double xrpPrice = 0.0;


    private static Date adaPriceDate = new Date();
    private static Date btcPriceDate = new Date();
    private static Date dogPriceDate = new Date();
    private static Date etcPriceDate = new Date();
    private static Date ethPriceDate = new Date();
    private static Date lnkPriceDate = new Date();
    private static Date ltcPriceDate = new Date();
    private static Date matPriceDate = new Date();
    private static Date solPriceDate = new Date();
    private static Date xrpPriceDate = new Date();

    private static String selectedRisk;

    private static Double dealAmount = 25.0;

    private static Double marginCallPercentage = 6.0;

    public static Double getEtcPrice() {
        return etcPrice;
    }

    public static void setEtcPrice(Double etcPrice) {
        PriceConfig.etcPrice = etcPrice;
    }


    public static Date getEtcPriceDate() {
        return etcPriceDate;
    }

    public static void setEtcPriceDate(Date etcPriceDate) {
        PriceConfig.etcPriceDate = etcPriceDate;
    }


    public static Double getDogPrice() {
        return dogPrice;
    }

    public static void setDogPrice(Double dogPrice) {
        PriceConfig.dogPrice = dogPrice;
    }

    public static Date getDogPriceDate() {
        return dogPriceDate;
    }

    public static void setDogPriceDate(Date dogPriceDate) {
        PriceConfig.dogPriceDate = dogPriceDate;
    }


    public static Double getAdaPrice() {
        return adaPrice;
    }

    public static void setAdaPrice(Double adaPrice) {
        PriceConfig.adaPrice = adaPrice;
    }

    public static Date getAdaPriceDate() {
        return adaPriceDate;
    }

    public static void setAdaPriceDate(Date adaPriceDate) {
        PriceConfig.adaPriceDate = adaPriceDate;
    }


    public static Double getBtcPrice() {
        return btcPrice;
    }

    public static void setBtcPrice(Double btcPrice) {
        PriceConfig.btcPrice = btcPrice;
    }

    public static Double getDealAmount() {
        return dealAmount;
    }

    public static void setDealAmount(Double dealAmount) {
        PriceConfig.dealAmount = dealAmount;
    }

    public static Date getBtcPriceDate() {
        return btcPriceDate;
    }

    public static void setBtcPriceDate(Date btcPriceDate) {
        PriceConfig.btcPriceDate = btcPriceDate;
    }

    public static Double getMarginCallPercentage() {
        return marginCallPercentage;
    }

    public static void setMarginCallPercentage(Double marginCallPercentage) {
        PriceConfig.marginCallPercentage = marginCallPercentage;
    }

    public static String getSelectedRisk() {
        return selectedRisk;
    }

    public static void setSelectedRisk(String selectedRisk) {
        PriceConfig.selectedRisk = selectedRisk;
    }

    public static Double getEthPrice() {
        return ethPrice;
    }

    public static void setEthPrice(Double ethPrice) {
        PriceConfig.ethPrice = ethPrice;
    }


    public static Date getEthPriceDate() {
        return ethPriceDate;
    }

    public static void setEthPriceDate(Date ethPriceDate) {
        PriceConfig.ethPriceDate = ethPriceDate;
    }

    public static Double getLnkPrice() {
        return lnkPrice;
    }

    public static void setLnkPrice(Double lnkPrice) {
        PriceConfig.lnkPrice = lnkPrice;
    }


    public static Date getLnkPriceDate() {
        return lnkPriceDate;
    }

    public static void setLnkPriceDate(Date lnkPriceDate) {
        PriceConfig.lnkPriceDate = lnkPriceDate;
    }

    public static Double getLtcPrice() {
        return ltcPrice;
    }

    public static void setLtcPrice(Double ltcPrice) {
        PriceConfig.ltcPrice = ltcPrice;
    }


    public static Date getLtcPriceDate() {
        return ltcPriceDate;
    }

    public static void setLtcPriceDate(Date ltcPriceDate) {
        PriceConfig.ltcPriceDate = ltcPriceDate;
    }

    public static Double getMatPrice() {
        return matPrice;
    }

    public static void setMatPrice(Double matPrice) {
        PriceConfig.matPrice = matPrice;
    }

    public static Double getSolPrice() {
        return solPrice;
    }

    public static void setSolPrice(Double solPrice) {
        PriceConfig.solPrice = solPrice;
    }

    public static Double getXrpPrice() {
        return xrpPrice;
    }

    public static void setXrpPrice(Double xrpPrice) {
        PriceConfig.xrpPrice = xrpPrice;
    }


    public static Date getMatPriceDate() {
        return matPriceDate;
    }

    public static void setMatPriceDate(Date matPriceDate) {
        PriceConfig.matPriceDate = matPriceDate;
    }

    public static Date getSolPriceDate() {
        return solPriceDate;
    }

    public static void setSolPriceDate(Date solPriceDate) {
        PriceConfig.solPriceDate = solPriceDate;
    }

    public static Date getXrpPriceDate() {
        return xrpPriceDate;
    }

    public static void setXrpPriceDate(Date xrpPriceDate) {
        PriceConfig.xrpPriceDate = xrpPriceDate;
    }
}
