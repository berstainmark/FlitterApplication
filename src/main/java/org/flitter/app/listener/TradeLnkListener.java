package org.flitter.app.listener;

import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import org.flitter.app.PriceConfig;
import org.flitter.app.service.Utils;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component

public class TradeLnkListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent events) {

        WebSocketStreamClientImpl client = new WebSocketStreamClientImpl();
        try {
            client.tradeStream("linkusdt", ((event) -> {

                try {
                    JSONObject json = new JSONObject(event);
                    if (json.has("p") && json.has("q") && json.has("m")) {
                        String priceStr = json.getString("p");
                        String quantityStr = json.getString("q");
                        boolean maker = json.getBoolean("m");
                        double price = Double.parseDouble(priceStr);
                        double quantity = Double.parseDouble(quantityStr);
                        if (maker) {
                            if (price * quantity > 50) {
                                PriceConfig.setLnkPrice(price);
                                PriceConfig.setLnkPriceDate(new Date());
                            }
                        }
                    }
                } catch (Exception e) {
                    Utils utils = new Utils();
                    utils.saveLogs("Error", "Lnk Stream", "Invalid JSON: missing required keys");
                }

            }));

        } catch (Exception e) {
            Utils utils = new Utils();
            utils.saveLogs("Error", "Lnk Stream", "Connection error");
        }
    }

}
