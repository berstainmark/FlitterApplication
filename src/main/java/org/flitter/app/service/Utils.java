package org.flitter.app.service;

import org.flitter.app.PrivateConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public Date getCurrentUtcTime() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return localDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (Exception e) {

            return new Date();
        }
    }

    public Double calcProfit(double price, double maxPrice, double lotSize) {
        double r = lotSize / price;
        double k = maxPrice * r;
        return k - (lotSize + 2 * (0.001 * lotSize));
    }

    public void saveLogs(String level, String name, String description) {
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

    public ImageIcon readImage(String name) {
        BufferedImage image = null;
        try (InputStream in = getClass().getResourceAsStream("/" + name)) {
            image = ImageIO.read(in);
        } catch (IOException e) {

        }


        ImageIcon imageIcon = null;
        if (image != null) {
            imageIcon = new ImageIcon(image);
        }

        return imageIcon;
    }

    public Double findDiffPrice(Double price) {

        for (double i = 1; i < 10000; i++) {
            double nextPrice = price + i;
            double diff = calcProfit(price, nextPrice, 1000);
            if (diff > 0)
                return i;
        }
        return 80.0;
    }

    public Double findSpreadDelta(double price, double lotSize) {
        double d = findDelta(price);
        return findSpotDelta(price * d, lotSize) / d;
    }

    public int findDelta(Double num) {
        String text = Double.toString(Math.abs(num));
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        return (int) Math.pow(10, decimalPlaces - 1);
    }

    public Double findSpotDelta(Double price, Double lotSize) {

        for (double i = 1; i < 10000; i++) {
            double nextPrice = price + i;
            double diff = calcProfit(price, nextPrice, lotSize);
            if (diff > 0)
                return i;
        }
        return 0.0;
    }
}
