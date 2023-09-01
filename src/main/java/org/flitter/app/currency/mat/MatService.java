package org.flitter.app.currency.mat;

import com.binance.connector.client.impl.SpotClientImpl;
import org.flitter.app.MarketObserver;
import org.flitter.app.PriceConfig;
import org.flitter.app.PrivateConfig;
import org.flitter.app.currency.mat.dao.BuyMatRepository;
import org.flitter.app.currency.mat.dao.ProfitMatRepository;
import org.flitter.app.currency.mat.dao.SellMatRepository;
import org.flitter.app.currency.mat.model.BuyMat;
import org.flitter.app.currency.mat.model.ProfitMat;
import org.flitter.app.currency.mat.model.SellMat;
import org.flitter.app.dao.UserRepository;
import org.flitter.app.model.User;
import org.flitter.app.service.PriceService;
import org.flitter.app.service.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MatService {
    private final List<MarketObserver> observers = new ArrayList<>();
    @Autowired
    BuyMatRepository buyMatRepository;
    @Autowired
    SellMatRepository sellMatRepository;
    @Autowired
    ProfitMatRepository profitMatRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PriceService priceService;

    public void registerObserver(MarketObserver observer) {
        observers.add(observer);
    }

    public void notifyMessageReceived(String message) {
        for (MarketObserver observer : observers) {
            observer.onMessageReceived(message);
        }
    }

    public void createOrder(String message) {
        Double dealAmount = PriceConfig.getDealAmount();

        boolean canBuy = false;
        Optional<User> userOptional = userRepository.findById(1L);
        if (userOptional.isPresent()) {
            User usr = userOptional.get();
            double balance = usr.getBalance();
            int limit = usr.getMatLimit();

            if (dealAmount > 20 && dealAmount < balance && limit > 0) {
                canBuy = true;
            }
        }

        if (canBuy) {
            Double price = PriceConfig.getMatPrice();

            Date expiredat = PriceConfig.getMatPriceDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiredat);
            cal.add(Calendar.MINUTE, 1);
            expiredat = cal.getTime();
            Date actualPrice = new Date();
            if (price != 0) {

                Utils utils = new Utils();
                String priceTradeFormat = String.format(Locale.ROOT, "%.4f", price);
                Double spreadMatPrice = utils.findSpreadDelta(Double.parseDouble(priceTradeFormat), dealAmount);

                if (actualPrice.before(expiredat)) {
                    Double quantity = dealAmount / price;
                    String quantityFormat = String.format(Locale.ROOT, "%.1f", quantity);

                    BuyMat buyMat = saveOrder(spreadMatPrice, dealAmount, priceTradeFormat, quantityFormat);
                    String info = sendOrder(quantityFormat, priceTradeFormat);
                    parseOrderResponse(buyMat, info);

                } else {
                    saveLogs("Error", "BUY Mat", "Delay in price when creating an order");
                    updateCurrentPrice();
                }
            } else {
                saveLogs("Error", "BUY Mat", "The price is zero when creating an order");
                updateCurrentPrice();
            }
        } else {
            saveLogs("Error", "BUY Mat", "The limit or balance is zero");
        }

    }

    private void updateCurrentPrice() {
        try {
            List<Double> tradePrice = priceService.getTradePrice("MATIC");
            double bidsAverageTradePrice = tradePrice.get(1);
            if (bidsAverageTradePrice > 0) {
                PriceConfig.setMatPrice(bidsAverageTradePrice);
                PriceConfig.setMatPriceDate(new Date());
            }
        } catch (Exception e) {
        }
    }

    public BuyMat saveOrder(Double spreadMatPrice, Double dealAmount, String priceTradeFormat, String quantityFormat) {

        String xUuid = UUID.randomUUID().toString();

        Utils utils = new Utils();
        Date now = utils.getCurrentUtcTime();

        BuyMat buy = new BuyMat();
        buy.setUserid(1L);
        buy.setDiffprice(spreadMatPrice);
        buy.setLotsize(dealAmount);
        buy.setTradeprice(Double.parseDouble(priceTradeFormat));
        buy.setMaxprice(Double.parseDouble(priceTradeFormat) + spreadMatPrice);
        buy.setQuantity(Double.parseDouble(quantityFormat));
        buy.setCreatedat(now);

        buy.setIsclose(2);
        buy.setIscloseexternal(0);

        buy.setIssheduled(0);
        buy.setXuuid(xUuid);

        return buyMatRepository.save(buy);

    }

    public String sendOrder(String quantityFormat, String priceTradeFormat) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);
        parameters.put("symbol", "MATICUSDT");
        parameters.put("side", "BUY");
        parameters.put("type", "LIMIT");
        parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantityFormat);
        parameters.put("price", priceTradeFormat);

        try {
            String result = client.createTrade().newOrder(parameters);
            saveLogs("Info", "BUY Mat", "A response from the service was received " + result);

            return result;
        } catch (Exception e) {

            saveLogs("Error", "BUY Mat", "An error occurred when submitting the order " + e.getMessage());

        }
        return null;
    }


    public void parseOrderResponse(BuyMat buy, String info) {
        try {

            Double realPrice = 0.0;
            Double realQuantity = 0.0;

            JSONObject jsonObject = new JSONObject(info);

            if (jsonObject.has("orderId")) {
                String orderId = String.valueOf(jsonObject.getBigInteger("orderId"));
                buy.setOrderid(orderId);
            }

            if (jsonObject.has("clientOrderId")) {
                String clientOrderId = jsonObject.getString("clientOrderId");
            }

            if (jsonObject.has("origQty")) {
                String origQty = jsonObject.getString("origQty");
            }

            if (jsonObject.has("executedQty")) {
                String executedQty = jsonObject.getString("executedQty");
                try {
                    realQuantity = Double.parseDouble(executedQty);
                } catch (Exception e) {
                }
            }
            Double lotsize = 0.0;

            if (jsonObject.has("cummulativeQuoteQty")) {
                String cummulativeQuoteQty = jsonObject.getString("cummulativeQuoteQty");
                try {
                    lotsize = Double.parseDouble(cummulativeQuoteQty);
                } catch (Exception e) {

                }
            }
            if (jsonObject.has("fills")) {
                JSONArray fills = jsonObject.getJSONArray("fills");
                Double priceAvr = 0.0;
                int avr = 0;
                for (int key = 0; key < fills.length(); key++) {
                    if (fills.get(key) instanceof JSONObject) {
                        JSONObject fillsObject = fills.getJSONObject(key);
                        if (fillsObject.has("price")) {
                            String priceStr = fillsObject.getString("price");
                            Double pr = Double.parseDouble(priceStr);
                            if (pr > 0) {
                                priceAvr = priceAvr + pr;
                                avr++;
                            }
                        }
                    }
                }
            }
            if (jsonObject.has("status")) {
                String status = jsonObject.getString("status");
                switch (status) {
                    case "FILLED":

                        if (realQuantity > 0) {
                            buy.setQuantity(realQuantity);
                        }
                        if (realPrice > 0) {
                            buy.setTradeprice(realPrice);
                            if (lotsize > 0) {
                                buy.setMaxprice(realPrice + buy.getDiffprice());
                                buy.setLotsize(lotsize);
                            }
                        }
                        buy.setIscloseexternal(1);
                        buy.setIsclose(0);
                        buy.setXerror("FILLED");
                        JSONObject notifyObject = new JSONObject();
                        notifyObject.put("currency", "mat");
                        notifyObject.put("action", "buy");

                        Optional<User> userOptional = userRepository.findById(1L);
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            double balanceMat = user.getMatBalance() + buy.getQuantity();
                            user.setMatBalance(balanceMat);
                            notifyObject.put("MatBalance", balanceMat);
                            int limit = user.getMatLimit() - 1;
                            if (limit < 0) limit = 0;
                            user.setMatLimit(limit);
                            user.setBalance(user.getBalance() - buy.getLotsize());
                            userRepository.save(user);
                        }
                        notifyMessageReceived(notifyObject.toString());
                        break;
                    case "NEW":
                        buy.setIscloseexternal(5);
                        buy.setIsclose(2);
                        buy.setXerror("NEW");
                        break;
                    case "PARTIALLY_FILLED":
                        buy.setIsclose(0);
                        buy.setIscloseexternal(2);
                        buy.setXerror("PARTIALLY_FILLED");
                        break;
                    case "CANCELED":
                        buy.setIsclose(2);
                        buy.setIscloseexternal(2);
                        buy.setXerror("CANCELED");

                        break;
                    case "PENDING_CANCEL":
                        buy.setIsclose(2);
                        buy.setIscloseexternal(2);
                        buy.setXerror("PENDING_CANCEL");
                        break;
                    case "REJECTED":
                        buy.setIsclose(2);
                        buy.setIscloseexternal(2);
                        buy.setXerror("REJECTED");
                        break;
                    case "EXPIRED":
                        buy.setIsclose(2);
                        buy.setIscloseexternal(2);
                        buy.setXerror("EXPIRED");
                        break;
                }
                buyMatRepository.save(buy);
            }
        } catch (Exception e) {

            saveLogs("Error", "BUY Mat", "An error occurred when parse order response " + e.getMessage());

        }
    }

    @Scheduled(fixedDelay = 14000, initialDelay = 20000)
    public void getBuyStatusByCloseExternal() {
        Utils utils = new Utils();
        Date endDate = utils.getCurrentUtcTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.MINUTE, -1);
        Date startDate = cal.getTime();


        List<BuyMat> buyList = buyMatRepository.findByCloseExternal(5);
        for (BuyMat buy : buyList) {
            if (buy.getCreatedat().before(startDate)) {
                cancelOrder("" + buy.getUserid(), buy.getOrderid());
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }

            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            try {
                SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);

                Thread.sleep(100);
                parameters.put("symbol", "MATICUSDT");
                parameters.put("orderId", buy.getOrderid());


                String result = client.createTrade().getOrder(parameters);

                JSONObject jsonObject = new JSONObject(result);

                Double realPrice = 0.0;
                Double realQuantity = 0.0;

                if (jsonObject.has("clientOrderId")) {
                    String clientOrderId = jsonObject.getString("clientOrderId");

                }

                if (jsonObject.has("origQty")) {
                    String origQty = jsonObject.getString("origQty");

                }

                if (jsonObject.has("executedQty")) {
                    String executedQty = jsonObject.getString("executedQty");

                    try {
                        realQuantity = Double.parseDouble(executedQty);
                    } catch (Exception e) {
                    }
                }

                Double lotsize = 0.0;

                if (jsonObject.has("cummulativeQuoteQty")) {
                    String cummulativeQuoteQty = jsonObject.getString("cummulativeQuoteQty");

                    try {
                        lotsize = Double.parseDouble(cummulativeQuoteQty);
                    } catch (Exception e) {

                    }
                }

                if (jsonObject.has("status")) {
                    String status = jsonObject.getString("status");

                }

                if (jsonObject.has("type")) {
                    String type = jsonObject.getString("type");

                }

                if (jsonObject.has("side")) {
                    String side = jsonObject.getString("side");

                }

                if (jsonObject.has("price")) {
                    String priceJson = jsonObject.getString("price");
                    try {
                        realPrice = Double.parseDouble(priceJson);

                    } catch (Exception e) {
                    }

                }
                if (jsonObject.has("fills")) {
                    JSONArray fills = jsonObject.getJSONArray("fills");
                    Double priceAvr = 0.0;

                    int avr = 0;
                    for (int key = 0; key < fills.length(); key++) {
                        if (fills.get(key) instanceof JSONObject) {
                            JSONObject fillsObject = fills.getJSONObject(key);
                            if (fillsObject.has("price")) {
                                String priceStr = fillsObject.getString("price");
                                Double pr = Double.parseDouble(priceStr);
                                if (pr > 0) {
                                    priceAvr = priceAvr + pr;
                                    avr++;
                                }
                            }
                        }
                    }
                }

                if (jsonObject.has("orderId")) {
                    String orderIdR = String.valueOf(jsonObject.getBigInteger("orderId"));

                }


                if (jsonObject.has("status")) {
                    String status = jsonObject.getString("status");
                    switch (status) {
                        case "FILLED":
                            if (realQuantity > 0) {
                                buy.setQuantity(realQuantity);
                            }
                            if (realPrice > 0) {
                                buy.setTradeprice(realPrice);
                                if (lotsize > 0) {
                                    buy.setMaxprice(realPrice + buy.getDiffprice());
                                    buy.setLotsize(lotsize);
                                }
                            }
                            buy.setIscloseexternal(1);
                            buy.setIsclose(0);
                            buy.setXerror("FILLED");
                            JSONObject notifyObject = new JSONObject();
                            notifyObject.put("currency", "mat");
                            notifyObject.put("action", "buy");
                            Optional<User> userOptional = userRepository.findById(1L);
                            if (userOptional.isPresent()) {
                                User user = userOptional.get();
                                double balanceMat = user.getMatBalance() + buy.getQuantity();
                                user.setMatBalance(balanceMat);
                                notifyObject.put("MatBalance", balanceMat);
                                int limit = user.getMatLimit() - 1;
                                if (limit < 0) limit = 0;
                                user.setMatLimit(limit);
                                user.setBalance(user.getBalance() - buy.getLotsize());
                                userRepository.save(user);
                            }
                            notifyMessageReceived(notifyObject.toString());
                            break;
                        case "NEW":
                            buy.setIscloseexternal(5);
                            buy.setIsclose(2);
                            buy.setXerror("NEW");

                            break;
                        case "PARTIALLY_FILLED":
                            buy.setIscloseexternal(2);
                            buy.setIsclose(0);
                            buy.setXerror("PARTIALLY_FILLED");
                            break;
                        case "CANCELED":
                            buy.setIscloseexternal(2);
                            buy.setIsclose(2);
                            buy.setXerror("CANCELED");

                            break;
                        case "PENDING_CANCEL":
                            buy.setIscloseexternal(2);
                            buy.setIsclose(2);
                            buy.setXerror("PENDING_CANCEL");
                            break;
                        case "REJECTED":
                            buy.setIscloseexternal(2);
                            buy.setIsclose(2);
                            buy.setXerror("REJECTED");
                            break;
                        case "EXPIRED":
                            buy.setIscloseexternal(2);
                            buy.setIsclose(2);
                            buy.setXerror("EXPIRED");
                            break;
                    }
                    buyMatRepository.save(buy);

                }

            } catch (Exception e) {

                saveLogs("Error", "BUY Mat", "An error occurred when parse order response " + e.getMessage());

            }

        }


    }

    private void cancelOrder(String userid, String orderid) {
        try {

            SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);
            Thread.sleep(100);
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("symbol", "MATICUSDT");
            parameters.put("orderId", orderid);

            String result = client.createTrade().cancelOrder(parameters);

            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.has("orderId")) {
                String orderIdR = "" + jsonObject.getBigInteger("orderId");
            }
            if (jsonObject.has("status")) {
                String status = jsonObject.getString("status");
            }

        } catch (Exception e) {

            saveLogs("Error", "BUY Mat", "An error occurred when cancel order " + e.getMessage());

        }
    }


    @Scheduled(fixedDelay = 5000, initialDelay = 20000)
    @Async
    public void sellScheduler() {
        try {
            Double price = PriceConfig.getMatPrice();
            Date expiredat = PriceConfig.getMatPriceDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiredat);
            cal.add(Calendar.MINUTE, 2);
            expiredat = cal.getTime();
            Date actualPrice = new Date();
            if (price != 0) {
                if (actualPrice.before(expiredat)) {
                    buyHoldMaxPrice(price);
                } else {
                    saveLogs("Error", "SELL Mat", "Delay in price when creating an order");
                    updateCurrentPrice();
                }
            } else {
                saveLogs("Error", "SELL Mat", "The price is zero when creating an order");
                updateCurrentPrice();

            }

        } catch (Exception e) {

            saveLogs("Error", "SELL Mat", "An error occurred when searching for the order " + e.getMessage());
        }
    }

    public void buyHoldMaxPrice(Double asksAverageTradePrice) {
        try {
            List<BuyMat> buyList = buyMatRepository.findBuyMaxPrice(0, asksAverageTradePrice);
            if (buyList != null)
                for (BuyMat buy : buyList) {
                    if (asksAverageTradePrice > 0) {
                        createTradeSell(buy);
                    }
                }
        } catch (Exception e) {

            saveLogs("Error", "SELL Mat", "An error occurred when searching for the order " + e.getMessage());
        }

    }

    public void createTradeSell(BuyMat buy) {
        try {
            Utils utils = new Utils();
            Date createdat = utils.getCurrentUtcTime();
            Double asksAverageTradePrice = buy.getMaxprice();
            String quantityStr = String.format(Locale.ROOT, "%.1f", buy.getQuantity());
            String priceFormat = String.format(Locale.ROOT, "%.4f", asksAverageTradePrice);

            buy.setIsclose(5);
            buyMatRepository.save(buy);

            SellMat sell = new SellMat();
            Optional<SellMat> saleOptional = sellMatRepository.findSaleId(buy.getXuuid());

            if (saleOptional.isPresent()) {
                sell = saleOptional.get();
            }
            sell.setCreatedat(createdat);
            sell.setUserid(buy.getUserid());
            sell.setBuyid(buy.getId());
            sell.setXuuid(buy.getXuuid());
            sell.setPrice(buy.getTradeprice());
            sell.setMaxprice(asksAverageTradePrice);
            sell.setIsclose(2);
            sell.setIscloseexternal(0);
            sell.setQuantity(buy.getQuantity());
            SellMat saleCreated = sellMatRepository.save(sell);

            sendSell(quantityStr, priceFormat, saleCreated, buy);

        } catch (Exception e) {

            saveLogs("Error", "SELL Mat", "An error occurred when submitting the order " + e.getMessage());
        }
    }

    public void sendSell(String quantityStr, String priceFormat, SellMat sell, BuyMat buy) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        try {
            SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);
            parameters.put("symbol", "MATICUSDT");
            parameters.put("side", "SELL");
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("quantity", quantityStr);
            parameters.put("price", priceFormat);
            String result = client.createTrade().newOrder(parameters);
            saveLogs("Info", "SELL Mat", "A response from the service was received " + result);


            Double realPrice = 0.0;
            Double fillPrice = 0.0;
            Double realQuantity = 0.0;

            JSONObject jsonObject = new JSONObject(result);


            boolean canSend = false;
            boolean canInform = false;
            if (jsonObject.has("orderId")) {
                String orderId = String.valueOf(jsonObject.getBigInteger("orderId"));
                sell.setOrderid(orderId);
            }

            if (jsonObject.has("clientOrderId")) {
                String clientOrderId = jsonObject.getString("clientOrderId");
            }

            if (jsonObject.has("origQty")) {
                String origQty = jsonObject.getString("origQty");
            }

            if (jsonObject.has("executedQty")) {
                String executedQty = jsonObject.getString("executedQty");
                try {
                    realQuantity = Double.parseDouble(executedQty);
                } catch (Exception e) {
                }
            }

            if (jsonObject.has("cummulativeQuoteQty")) {
                String cummulativeQuoteQty = jsonObject.getString("cummulativeQuoteQty");
            }

            if (jsonObject.has("status")) {
                String status = jsonObject.getString("status");
            }

            if (jsonObject.has("type")) {
                String type = jsonObject.getString("type");
            }

            if (jsonObject.has("side")) {
                String side = jsonObject.getString("side");
            }


            if (jsonObject.has("price")) {
                String pricePr = jsonObject.getString("price");
                Double pr = Double.parseDouble(pricePr);
                realPrice = Double.parseDouble(pricePr);
            }

            if (jsonObject.has("fills")) {
                JSONArray fills = jsonObject.getJSONArray("fills");
                Double priceAvr = 0.0;

                Double quantityAvr = 0.0;
                int avr = 0;
                for (int key = 0; key < fills.length(); key++) {
                    if (fills.get(key) instanceof JSONObject) {
                        JSONObject fillsObject = fills.getJSONObject(key);
                        if (fillsObject.has("price")) {
                            String priceStr = fillsObject.getString("price");
                            Double pr = Double.parseDouble(priceStr);
                            if (pr > 0) {
                                priceAvr = priceAvr + pr;
                                avr++;
                            }
                        }
                        if (fillsObject.has("qty")) {
                            String qtyStr = fillsObject.getString("qty");
                            Double qty = Double.parseDouble(qtyStr);
                            quantityAvr = quantityAvr + qty;
                        }
                    }
                }
            }

            if (jsonObject.has("status")) {
                Utils utils = new Utils();
                Double lotTradeprice = buy.getTradeprice();
                Double lotMaxprice = buy.getMaxprice();
                double commission = utils.calcProfit(lotTradeprice, lotMaxprice, buy.getLotsize());

                String status = jsonObject.getString("status");
                switch (status) {
                    case "FILLED":
                        if (realQuantity > 0) {
                            sell.setQuantity(realQuantity);
                        }
                        if (realPrice > 0) {
                            sell.setMaxprice(realPrice);
                            buy.setMaxprice(realPrice);
                            commission = utils.calcProfit(lotTradeprice, realPrice, buy.getLotsize());
                        } else if (fillPrice > 0) {

                            sell.setMaxprice(fillPrice);
                            buy.setMaxprice(fillPrice);
                            commission = utils.calcProfit(lotTradeprice, fillPrice, buy.getLotsize());
                        }
                        try {
                            commission = utils.calcProfit(buy.getTradeprice(), sell.getMaxprice(), buy.getLotsize());
                        } catch (Exception e) {
                        }

                        JSONObject notifyObject = new JSONObject();
                        notifyObject.put("currency", "mat");
                        notifyObject.put("action", "sell");
                        Optional<User> userOptional = userRepository.findById(1L);
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            double balanceMat = user.getMatBalance() - buy.getQuantity();
                            user.setMatBalance(balanceMat);
                            notifyObject.put("MatBalance", balanceMat);
                            int deals = user.getDeals() + 1;
                            user.setDeals(deals);
                            notifyObject.put("deals", deals);
                            double profit = user.getProfit() + commission;

                            user.setProfit(profit);
                            notifyObject.put("profit", profit);

                            int limit = user.getMatLimit() + 1;
                            if (limit < 0) limit = 0;
                            user.setMatLimit(limit);

                            user.setBalance(user.getBalance() - buy.getLotsize());
                            userRepository.save(user);
                        }
                        notifyMessageReceived(notifyObject.toString());


                        buy.setIscloseexternal(1);
                        buy.setIsclose(1);
                        sell.setIsclose(1);
                        sell.setIscloseexternal(1);
                        sell.setXerror("FILLED");


                        Date createdat = utils.getCurrentUtcTime();
                        double asksAverageTradePrice = sell.getMaxprice();
                        ProfitMat profit = new ProfitMat();
                        profit.setCreatedat(createdat);
                        profit.setBuyid(buy.getId());
                        profit.setSaleid(buy.getId());
                        profit.setXsum(asksAverageTradePrice - buy.getTradeprice());
                        profit.setIssheduled(0);
                        profit.setIscloseexternal(0);
                        profit.setXuuid(buy.getXuuid());
                        profit.setUserid(buy.getUserid());


                        profit.setZsum(commission);
                        profitMatRepository.save(profit);

                        break;
                    case "NEW":
                        buy.setIsclose(2);
                        buy.setIscloseexternal(1);

                        sell.setIscloseexternal(5);
                        sell.setIsclose(2);
                        sell.setXerror("NEW");
                        break;
                    case "PARTIALLY_FILLED":
                        sell.setIscloseexternal(8);
                        buy.setIsclose(8);
                        sell.setXerror("PARTIALLY_FILLED");
                        break;
                    case "CANCELED":
                        sell.setIscloseexternal(2);
                        buy.setIsclose(0);
                        sell.setXerror("CANCELED");
                        break;
                    case "PENDING_CANCEL":
                        sell.setIscloseexternal(2);
                        buy.setIsclose(0);
                        sell.setXerror("PENDING_CANCEL");
                        break;
                    case "REJECTED":
                        sell.setIscloseexternal(2);
                        buy.setIsclose(0);
                        sell.setXerror("REJECTED");
                        break;
                    case "EXPIRED":
                        sell.setIscloseexternal(2);
                        buy.setIsclose(0);
                        sell.setXerror("EXPIRED");
                        break;
                }
                buyMatRepository.save(buy);


                sellMatRepository.save(sell);

            }


        } catch (Exception e) {

            saveLogs("Error", "SELL Mat", "An error occurred when parse order response " + e.getMessage());

        }

    }


    @Scheduled(fixedDelay = 15000, initialDelay = 20000)
    public void getSellStatus() {
        List<SellMat> saleList = sellMatRepository.findByCloseExternal(5);
        for (SellMat sale : saleList) {

            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            try {
                SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);

                Thread.sleep(100);
                parameters.put("symbol", "MATICUSDT");
                parameters.put("orderId", sale.getOrderid());
                Utils utils = new Utils();
                boolean canSend = false;
                String result = client.createTrade().getOrder(parameters);

                JSONObject jsonObject = new JSONObject(result);

                Double realPrice = 0.0;
                Double realQuantity = 0.0;

                if (jsonObject.has("clientOrderId")) {
                    String clientOrderId = jsonObject.getString("clientOrderId");

                }

                if (jsonObject.has("origQty")) {
                    String origQty = jsonObject.getString("origQty");

                }

                if (jsonObject.has("executedQty")) {
                    String executedQty = jsonObject.getString("executedQty");

                    try {
                        realQuantity = Double.parseDouble(executedQty);
                    } catch (Exception e) {
                    }
                }

                if (jsonObject.has("cummulativeQuoteQty")) {
                    String cummulativeQuoteQty = jsonObject.getString("cummulativeQuoteQty");

                }

                if (jsonObject.has("status")) {
                    String status = jsonObject.getString("status");

                }

                if (jsonObject.has("type")) {
                    String type = jsonObject.getString("type");

                }

                if (jsonObject.has("side")) {
                    String side = jsonObject.getString("side");

                }


                if (jsonObject.has("orderId")) {
                    String orderIdR = String.valueOf(jsonObject.getBigInteger("orderId"));

                }

                if (jsonObject.has("price")) {
                    String pricePr = jsonObject.getString("price");
                    Double pr = Double.parseDouble(pricePr);

                }

                if (jsonObject.has("fills")) {
                    JSONArray fills = jsonObject.getJSONArray("fills");


                    Double priceAvr = 0.0;
                    Double quantityAvr = 0.0;
                    int avr = 0;
                    for (int key = 0; key < fills.length(); key++) {
                        if (fills.get(key) instanceof JSONObject) {
                            JSONObject fillsObject = fills.getJSONObject(key);
                            if (fillsObject.has("price")) {
                                String priceStr = fillsObject.getString("price");
                                Double pr = Double.parseDouble(priceStr);
                                if (pr > 0) {
                                    priceAvr = priceAvr + pr;
                                    avr++;
                                }
                            }
                            if (fillsObject.has("qty")) {
                                String qtyStr = fillsObject.getString("qty");
                                Double qty = Double.parseDouble(qtyStr);
                                quantityAvr = quantityAvr + qty;
                            }
                        }
                    }
                }

                if (jsonObject.has("status")) {

                    String status = jsonObject.getString("status");
                    Optional<BuyMat> buyMatOptional = buyMatRepository.findBuyId(sale.getXuuid());

                    if (buyMatOptional.isPresent()) {

                        BuyMat buy = buyMatOptional.get();
                        Double lotTradeprice = buy.getTradeprice();
                        Double lotMaxprice = buy.getMaxprice();
                        double commission = utils.calcProfit(lotTradeprice, lotMaxprice, buy.getLotsize());


                        switch (status) {

                            case "FILLED":
                                saveLogs("Info", "SELL Mat", "A response from the service was received " + result);

                                if (realQuantity > 0) {
                                    sale.setQuantity(realQuantity);
                                }
                                if (realPrice > 0) {
                                    sale.setMaxprice(realPrice);
                                    buy.setMaxprice(realPrice);
                                    commission = utils.calcProfit(lotTradeprice, realPrice, buy.getLotsize());
                                }
                                try {
                                    commission = utils.calcProfit(buy.getTradeprice(), sale.getMaxprice(), buy.getLotsize());
                                } catch (Exception e) {
                                }

                                buy.setIscloseexternal(1);
                                buy.setIsclose(1);
                                sale.setIsclose(1);
                                sale.setIscloseexternal(1);
                                sale.setXerror("FILLED");


                                Date createdat = utils.getCurrentUtcTime();
                                double asksAverageTradePrice = sale.getMaxprice();
                                ProfitMat profit = new ProfitMat();
                                profit.setCreatedat(createdat);
                                profit.setBuyid(buy.getId());
                                profit.setSaleid(buy.getId());
                                profit.setXsum(asksAverageTradePrice - buy.getTradeprice());
                                profit.setIssheduled(0);
                                profit.setIscloseexternal(0);
                                profit.setXuuid(buy.getXuuid());
                                profit.setUserid(buy.getUserid());
                                profit.setZsum(commission);

                                JSONObject notifyObject = new JSONObject();
                                notifyObject.put("currency", "mat");
                                notifyObject.put("action", "sell");
                                Optional<User> userOptional = userRepository.findById(1L);
                                if (userOptional.isPresent()) {
                                    User user = userOptional.get();
                                    double balanceMat = user.getMatBalance() - buy.getQuantity();
                                    user.setMatBalance(balanceMat);
                                    notifyObject.put("MatBalance", balanceMat);

                                    int deals = user.getDeals() + 1;
                                    user.setDeals(deals);
                                    notifyObject.put("deals", deals);
                                    double profitDeals = user.getProfit() + commission;

                                    user.setProfit(profitDeals);
                                    notifyObject.put("profit", profitDeals);

                                    int limit = user.getMatLimit() + 1;
                                    if (limit < 0) limit = 0;
                                    user.setMatLimit(limit);
                                    user.setBalance(user.getBalance() - buy.getLotsize());
                                    userRepository.save(user);
                                }
                                notifyMessageReceived(notifyObject.toString());
                                profitMatRepository.save(profit);
                                break;
                            case "NEW":
                                buy.setIsclose(2);
                                buy.setIscloseexternal(1);

                                sale.setIscloseexternal(5);
                                sale.setIsclose(2);
                                sale.setXerror("NEW");

                                break;
                            case "PARTIALLY_FILLED":
                                sale.setIscloseexternal(8);
                                buy.setIsclose(8);
                                sale.setXerror("PARTIALLY_FILLED");
                                break;
                            case "CANCELED":
                                sale.setIscloseexternal(2);
                                buy.setIsclose(0);
                                sale.setXerror("CANCELED");
                                break;
                            case "PENDING_CANCEL":
                                sale.setIscloseexternal(2);
                                buy.setIsclose(0);
                                sale.setXerror("PENDING_CANCEL");
                                break;
                            case "REJECTED":
                                sale.setIscloseexternal(2);
                                buy.setIsclose(0);
                                sale.setXerror("REJECTED");
                                break;
                            case "EXPIRED":
                                sale.setIscloseexternal(2);
                                buy.setIsclose(0);
                                sale.setXerror("EXPIRED");
                                break;
                        }
                        buyMatRepository.save(buy);
                        sellMatRepository.save(sale);

                    }
                }

            } catch (Exception e) {

                saveLogs("Error", "SELL Mat", "An error occurred when submitting the order " + e.getMessage());

            }

        }


    }


    @Scheduled(fixedDelay = 55000, initialDelay = 20000)
    @Async
    public void findMarginCallScheduler() {
        try {
            Double price = PriceConfig.getMatPrice();
            Date expiredat = PriceConfig.getMatPriceDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiredat);
            cal.add(Calendar.MINUTE, 2);
            expiredat = cal.getTime();
            Date actualPrice = new Date();
            if (price != 0) {
                if (actualPrice.before(expiredat)) {
                    double discount = price * (PriceConfig.getMarginCallPercentage() / 100);
                    double newPrice = price - discount;
                    double margin = price - newPrice;
                    marginCallPrice(price, margin);
                } else {

                    saveLogs("Error", "MRG Mat", "Delay in price when creating an order");
                    updateCurrentPrice();
                }
            } else {

                saveLogs("Error", "MRG Mat", "The price is zero when creating an order");
                updateCurrentPrice();
            }

        } catch (Exception e) {

            saveLogs("Error", "MRG Mat", "An error occurred when searching for the order " + e.getMessage());
        }
    }

    public void marginCallPrice(Double price, Double margin) {
        try {

            List<BuyMat> buyList = buyMatRepository.findMarginPrice(0, price, margin);
            Utils utils = new Utils();
            if (buyList != null)
                for (BuyMat buy : buyList) {
                    if (price > 0) {
                        Double profitDeals = utils.calcProfit(buy.getTradeprice(), price, buy.getLotsize());
                        Double dealAmount = (buy.getLotsize() + profitDeals);
                        Double quantity = dealAmount / price;

                        buy.setQuantity(quantity);
                        buy.setMaxprice(price);
                        createTradeSell(buy);

                        JSONObject notifyObject = new JSONObject();
                        notifyObject.put("currency", "mat");
                        notifyObject.put("action", "margin");

                        notifyMessageReceived(notifyObject.toString());
                        saveLogs("Error", "MRG Mat", "Margin call is executed");
                    }
                }
        } catch (Exception e) {

            saveLogs("Error", "MRG Mat", "An error occurred during the margin call  " + e.getMessage());

        }

    }

    void saveLogs(String level, String name, String description) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String strDate = formatter.format(date);
            JSONObject jsonNew = new JSONObject();
            jsonNew.put("time", strDate);
            jsonNew.put("level", level);
            jsonNew.put("name", name);
            jsonNew.put("description", description);

            String filePathStr = PrivateConfig.LOG_URL + "/flitter-log.json";
            Path filePath = Paths.get(filePathStr);
            try {
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
            } catch (IOException e) {

            }
            if (Files.exists(filePath)) {
                String content = new String(Files.readAllBytes(filePath));
                JSONArray logsArray;
                try {
                    logsArray = new JSONArray(content);

                } catch (JSONException e) {
                    logsArray = new JSONArray();
                }
                logsArray.put(jsonNew);
                try (FileWriter fileWriter = new FileWriter(filePathStr)) {
                    fileWriter.write(logsArray.toString());
                    fileWriter.flush();
                } catch (IOException e) {

                }
            }

        } catch (IOException e) {

        }
    }

}
