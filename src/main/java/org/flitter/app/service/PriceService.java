package org.flitter.app.service;

import com.binance.connector.client.impl.SpotClientImpl;
import org.flitter.app.PrivateConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class PriceService {

    @Autowired
    UserService userService;


    public List<Double> getTradePrice(String symbol) {
        List<Double> resultList = new ArrayList<>();
        try {
            SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY, PrivateConfig.BASE_URL);
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("symbol", symbol + "USDT");

            switch (symbol) {
                case "SOL":
                case "ETC":
                case "ADA":
                case "XRP":
                case "LTC":
                case "DOGE":
                case "ETH":
                case "LINK":
                case "MATIC":
                    parameters.put("limit", 4);
                    break;
                default:
                    parameters.put("limit", 10);
                    break;
            }

            String result = client.createMarket().depth(parameters);

            double asksMin = 0.0;
            double asksMax = 0.0;


            double bidsMin = 0.0;
            double bidsMax = 0.0;

            double asksAverageTradePrice;
            double bidsAverageTradePrice;


            if (!isEmptyString(result)) {
                JSONObject jsonObject = new JSONObject(result);


                if (jsonObject.has("asks")) {
                    if (jsonObject.get("asks") instanceof JSONArray) {
                        JSONArray asksArray = jsonObject.getJSONArray("asks");
                        for (int i = 0; i < asksArray.length(); i++) {
                            JSONArray bidObject = asksArray.getJSONArray(i);
                            String price = String.valueOf(bidObject.get(0));
                            double dPrice = Double.parseDouble(price);
                            if (i == 0) asksMin = dPrice;
                            if (asksMin > dPrice) asksMin = dPrice;
                            if (asksMax < dPrice) asksMax = dPrice;
                        }
                    }
                }

                if (jsonObject.has("bids")) {
                    if (jsonObject.get("bids") instanceof JSONArray) {
                        JSONArray bidsArray = jsonObject.getJSONArray("bids");
                        for (int i = 0; i < bidsArray.length(); i++) {
                            JSONArray bidObject = bidsArray.getJSONArray(i);
                            String price = String.valueOf(bidObject.get(0));
                            double dPrice = Double.parseDouble(price);
                            if (i == 0) bidsMin = dPrice;
                            if (bidsMin > dPrice) bidsMin = dPrice;
                            if (bidsMax < dPrice) bidsMax = dPrice;

                        }
                    }
                }
            }

            asksAverageTradePrice = (asksMax + asksMin) / 2;
            bidsAverageTradePrice = (bidsMax + bidsMin) / 2;

            resultList.add(asksAverageTradePrice);
            resultList.add(bidsAverageTradePrice);
            return resultList;
        } catch (Exception e) {
            resultList.add((double) 0);
            resultList.add((double) 0);
            return resultList;
        }
    }

    boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }


}
