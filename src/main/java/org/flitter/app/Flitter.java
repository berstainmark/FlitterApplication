package org.flitter.app;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.binance.connector.client.exceptions.BinanceServerException;
import com.binance.connector.client.impl.SpotClientImpl;
import org.flitter.app.dao.UserRepository;
import org.flitter.app.model.User;
import org.flitter.app.service.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

enum ConnectionStatus {
    CONNECTED("Connected "),
    CONNECTING("Connecting "),

    CLOSED("Closed "),
    CONNECTION_ERROR("Connection error ");


    private final String status;

    ConnectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

enum SubscriptionStatus {
    ACTIVE("Hiss Subscription: Active"),
    INACTIVE("Hiss Subscription: Inactive");

    private final String status;

    SubscriptionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

enum StrategyLevel {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String level;

    StrategyLevel(String level) {
        this.level = level;
    }

}

enum TradingRisk {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String level;

    TradingRisk(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}

enum TradingTime {
    DAY("Day"),
    NONE("None"),
    ALL("All"),
    NIGHT("Night");


    private final String time;

    TradingTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

}

public class Flitter extends JFrame implements ConnectionObserver, MarketObserver {

    private final SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;
    private final StrategyLevel strategyStatus = StrategyLevel.MEDIUM;
    private final String ethValue = "🔹 ETH 0.0";
    private final String xrpValue = " ▪  XRP 0.0";
    private final String riskStatus = "Trading Risk:";
    private final String amountStatus = "Trading Amount:";
    private final String timeStatus = "Trading Time:";
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField secretField;
    private final WebSocketConnection webSocketConnection;
    private final String hissKey;
    private final String binanceKey;
    private final String secretKey;
    private final UserRepository userRepository;
    private final String home;
    JPanel greenCirclePanel;
    private ConnectionStatus connectedStatus;
    private Color circleColor = Color.GRAY;
    private Double adaBalance = 0.0;
    private Double btcBalance = 0.0;
    private Double dogBalance = 0.0;
    private Double etcBalance = 0.0;
    private Double ethBalance = 0.0;
    private Double lnkBalance = 0.0;
    private Double ltcBalance = 0.0;
    private Double matBalance = 0.0;
    private Double solBalance = 0.0;
    private Double xrpBalance = 0.0;
    private double profit = 0.0;
    private int deals = 0;
    private JLabel statusLabel;
    private JLabel connectedLabel = new JLabel();
    private JPanel circlePanel;
    private JPanel infoPanel;
    private JPanel strategyPanel;
    private TradingTime selectedTradingTime;
    private TradingRisk selectedTradingRisk;
    private int selectedAmount;
    private int selectedMargin;

    public Flitter(UserRepository userRepository, String home, WebSocketConnection webSocketConnection) {
        this.home = home;
        this.userRepository = userRepository;
        this.webSocketConnection = webSocketConnection;

        connectedStatus = ConnectionStatus.CONNECTING;
        setTitle("Flitter");
        Utils utils = new Utils();
        ImageIcon icon = utils.readImage("flitterRounded.png");
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height / 2;
        int width = height / 2;
        setSize(width, height);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Hiss Key:");
        usernameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        usernameField = new JPasswordField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel passwordLabel = new JLabel("Binance Key:");
        passwordLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        secretField = new JPasswordField();
        secretField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel secretLabel = new JLabel("Secret Key:");
        secretLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        Optional<User> userOptional = userRepository.findById(1L);
        if (userOptional.isEmpty()) {
            User user = new User();
            user.setId(1L);
            user.setName("Flitter");
            user.setHisskey("");
            user.setApikey("");
            user.setSecretkey("");
            user.setDealamount(25.0);
            user.setBalance(1000.0);
            user.setAdaBalance(0.0);
            user.setAdaLimit(3);
            user.setBtcBalance(0.0);
            user.setBtcLimit(3);
            user.setDogBalance(0.0);
            user.setDogLimit(3);
            user.setEtcBalance(0.0);
            user.setEtcLimit(3);
            user.setEthBalance(0.0);
            user.setEthLimit(3);
            user.setLnkBalance(0.0);
            user.setLnkLimit(3);
            user.setLtcBalance(0.0);
            user.setLtcLimit(3);
            user.setMatBalance(0.0);
            user.setMatLimit(3);
            user.setSolBalance(0.0);
            user.setSolLimit(3);
            user.setXrpBalance(0.0);
            user.setXrpLimit(3);
            user.setProfit(0.0);
            user.setMargincall(6.0);
            user.setDeals(0);
            user.setProfit(0.0);
            user.setBtcBalance(0.0);
            user.setBtcLimit(5);

            selectedMargin = 6;
            TradingTime[] tradingTimes = TradingTime.values();
            TradingRisk[] tradingRisks = TradingRisk.values();
            TradingTime randomTradingTime = tradingTimes[new Random().nextInt(tradingTimes.length)];
            TradingRisk randomTradingRisk = tradingRisks[new Random().nextInt(tradingRisks.length)];
            user.setTradingtime(randomTradingTime.name().toLowerCase());
            user.setTradingrisk(randomTradingRisk.name().toLowerCase());
            userRepository.save(user);
            hissKey = "";
            binanceKey = "";
            secretKey = "";
        } else {

            hissKey = userOptional.get().getHisskey();
            binanceKey = userOptional.get().getApikey();
            secretKey = userOptional.get().getSecretkey();
            profit = userOptional.get().getProfit();
            adaBalance = userOptional.get().getAdaBalance();
            btcBalance = userOptional.get().getBtcBalance();
            dogBalance = userOptional.get().getDogBalance();
            etcBalance = userOptional.get().getEtcBalance();
            ethBalance = userOptional.get().getEthBalance();
            lnkBalance = userOptional.get().getLnkBalance();
            ltcBalance = userOptional.get().getLtcBalance();
            matBalance = userOptional.get().getMatBalance();
            solBalance = userOptional.get().getSolBalance();
            xrpBalance = userOptional.get().getXrpBalance();

            deals = userOptional.get().getDeals();

            if (userOptional.get().getDealamount() != null) {
                selectedAmount = userOptional.get().getDealamount().intValue();
                PriceConfig.setDealAmount((double) selectedAmount);
            }

            if (userOptional.get().getTradingtime() != null) {
                selectedTradingTime = TradingTime.valueOf(userOptional.get().getTradingtime().toUpperCase());
            }

            if (userOptional.get().getTradingrisk() != null) {
                selectedTradingRisk = TradingRisk.valueOf(userOptional.get().getTradingrisk().toUpperCase());
                PriceConfig.setSelectedRisk(userOptional.get().getTradingrisk().toUpperCase());
            }
            if (userOptional.get().getMargincall() != null) {
                selectedMargin = userOptional.get().getMargincall().intValue();
                PriceConfig.setMarginCallPercentage((double) selectedMargin);
            }


        }

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setText(hissKey);
        passwordField.setText(binanceKey);
        secretField.setText(secretKey);


        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(secretLabel);
        inputPanel.add(secretField);
        inputPanel.add(Box.createVerticalStrut(10));

        panel.add(inputPanel, BorderLayout.CENTER);

        JButton connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);


        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hissKey = usernameField.getText();
                String binanceKey = new String(passwordField.getPassword());
                String secretKey = new String(secretField.getPassword());
                try {
                    Object[] result = new Object[2];
                    result = checkAccount(binanceKey, secretKey);

                    boolean verify = (boolean) result[0];
                    String verifyMessage = (String) result[1];
                    if (verify) {
                        clearFields();
                        remove(panel);
                        createStatusLabel();
                        Optional<User> userOptional = userRepository.findById(1L);
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            user.setHisskey(hissKey);
                            user.setApikey(binanceKey);
                            user.setSecretkey(secretKey);
                            userRepository.save(user);
                            PrivateConfig.HISS_KEY = hissKey;
                            PrivateConfig.API_KEY = binanceKey;
                            PrivateConfig.SECRET_KEY = secretKey;

                        }
                        startTimer();
                    } else {
                        saveLogs("Error", "Login", verifyMessage);
                        JOptionPane.showMessageDialog(Flitter.this, verifyMessage, "Error accessing Binance", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Flitter.this, "Invalid Binance Key or IP address not allowed", "Error", JOptionPane.ERROR_MESSAGE);

                }

            }
        });

        panel.add(connectButton, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
        if (binanceKey != null && secretKey != null && !binanceKey.isEmpty() && !secretKey.isEmpty()) {
            Object[] result = new Object[2];
            result = checkAccount(binanceKey, secretKey);

            boolean verify = (boolean) result[0];
            String verifyMessage = (String) result[1];
            if (verify) {
                saveLogs("Info", "Login", "Login success");
                PrivateConfig.API_KEY = binanceKey;
                PrivateConfig.SECRET_KEY = secretKey;
                PrivateConfig.HISS_KEY = hissKey;
                clearFields();
                remove(panel);
                createStatusLabel();
                startTimer();
            } else {
                JOptionPane.showMessageDialog(Flitter.this, verifyMessage, "Error accessing Binance", JOptionPane.ERROR_MESSAGE);
            }
        }


    }

    private Object[] checkAccount(String binanceKey, String secretKey) {
        Object[] result = new Object[2];
        result[0] = false;
        result[1] = "Binance error";
        if (binanceKey.isEmpty() && secretKey.isEmpty()) return result;
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        try {
            parameters = new LinkedHashMap<>();
            SpotClient client = new SpotClientImpl(binanceKey, secretKey, PrivateConfig.BASE_URL);
            String response = null;

            try {
                response = client.createTrade().account(parameters);
            } catch (BinanceClientException | BinanceConnectorException | BinanceServerException e) {

                result[1] = e.getMessage();
                saveLogs("Error", "Login", e.getMessage());
            }

            if (response != null) {
                JSONObject json = new JSONObject(response);
                if (json.has("canTrade")) {
                    result[0] = json.getBoolean("canTrade");
                    if (!json.getBoolean("canTrade"))
                        result[1] = "{\"code\":0,\"msg\":\"Enable Spot and Margin Trading in API enableSpotAndMarginTrading.\"}";
                    return result;
                }
                saveLogs("Error", "Login", response);
            }

        } catch (Exception e) {
            saveLogs("Error", "Login", "Not enableSpotAndMarginTrading");

        }
        return result;
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void createStatusLabel() {
        statusLabel = new JLabel("Connection successful");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.CENTER);
        revalidate();
    }

    private void startTimer() {
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(statusLabel);
                createCirclePanel();
                createInfoPanel();
                revalidate();
                repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void createCirclePanel() {
        circlePanel = new JPanel();
        circlePanel.setPreferredSize(new Dimension(16, 20));

        circlePanel.setOpaque(false);
        circlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        circlePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel bellLabel = new JLabel("\uD83D\uDD14");
        bellLabel.setOpaque(true);

        bellLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        circlePanel.add(bellLabel, BorderLayout.EAST);


        circlePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        connectedLabel = new JLabel(connectedStatus.getStatus());
        connectedLabel.setFont(new Font("Arial", Font.PLAIN, 8));
        circlePanel.add(connectedLabel);


        if (connectedStatus.equals(ConnectionStatus.CONNECTED)) circleColor = Color.GREEN;
        else if (connectedStatus.equals(ConnectionStatus.CONNECTING)) circleColor = Color.YELLOW;
        else if (connectedStatus.equals(ConnectionStatus.CLOSED)) circleColor = Color.RED;
        else if (connectedStatus.equals(ConnectionStatus.CONNECTION_ERROR)) circleColor = Color.RED;


        greenCirclePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(circleColor);
                g.fillOval(0, 0, 6, 6);
            }
        };

        greenCirclePanel.setPreferredSize(new Dimension(6, 6));
        circlePanel.add(greenCirclePanel);

        circlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LogViewer logViewer = new LogViewer();
                logViewer.loadLogs(home + "/flitter-log.json");
                logViewer.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });


        add(circlePanel, BorderLayout.NORTH);
    }

    private void createInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 0);

        JLabel subscriptionLabel = new JLabel(subscriptionStatus.getStatus());
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(subscriptionLabel, gbc);

        JLabel strategyLabel = new JLabel("🕹 Strategy: " + selectedTradingRisk.getLevel());
        gbc.gridy++;
        infoPanel.add(strategyLabel, gbc);

        JLabel portfolioLabel = new JLabel("💼 Portfolio:");
        gbc.gridy++;
        infoPanel.add(portfolioLabel, gbc);
        if (adaBalance <= 0) {
            adaBalance = 0.0;
        } else {
            String adaData = " ▪  ADA " + String.format(Locale.ROOT, "%.1f", adaBalance);
            JLabel adaLabel = new JLabel(adaData);
            gbc.gridy++;
            infoPanel.add(adaLabel, gbc);
        }
        if (btcBalance <= 0) {
            btcBalance = 0.0;
        } else {
            String btcData = " ▪  BTC " + String.format(Locale.ROOT, "%.4f", btcBalance);
            JLabel btcLabel = new JLabel(btcData);
            gbc.gridy++;
            infoPanel.add(btcLabel, gbc);
        }

        if (dogBalance <= 0) {
            dogBalance = 0.0;
        } else {
            String dogData = " ▪  DOG " + String.format(Locale.ROOT, "%.0f", dogBalance);
            JLabel dogLabel = new JLabel(dogData);
            gbc.gridy++;
            infoPanel.add(dogLabel, gbc);
        }
        if (etcBalance <= 0) {
            etcBalance = 0.0;
        } else {
            String etcData = " ▪  ETC " + String.format(Locale.ROOT, "%.2f", etcBalance);
            JLabel etcLabel = new JLabel(etcData);
            gbc.gridy++;
            infoPanel.add(etcLabel, gbc);
        }
        if (ethBalance <= 0) {
            ethBalance = 0.0;
        } else {
            String ethData = " ▪  ETH " + String.format(Locale.ROOT, "%.4f", ethBalance);
            JLabel ethLabel = new JLabel(ethData);
            gbc.gridy++;
            infoPanel.add(ethLabel, gbc);
        }
        if (lnkBalance <= 0) {
            lnkBalance = 0.0;
        } else {
            String lnkData = " ▪  LNK " + String.format(Locale.ROOT, "%.2f", lnkBalance);
            JLabel lnkLabel = new JLabel(lnkData);
            gbc.gridy++;
            infoPanel.add(lnkLabel, gbc);
        }
        if (ltcBalance <= 0) {
            ltcBalance = 0.0;
        } else {
            String ltcData = " ▪  LTC " + String.format(Locale.ROOT, "%.2f", ltcBalance);
            JLabel ltcLabel = new JLabel(ltcData);
            gbc.gridy++;
            infoPanel.add(ltcLabel, gbc);
        }
        if (matBalance <= 0) {
            matBalance = 0.0;
        } else {
            String matData = " ▪  MAT " + String.format(Locale.ROOT, "%.1f", matBalance);
            JLabel matLabel = new JLabel(matData);
            gbc.gridy++;
            infoPanel.add(matLabel, gbc);
        }
        if (solBalance <= 0) {
            solBalance = 0.0;
        } else {
            String solData = " ▪  SOL " + String.format(Locale.ROOT, "%.2f", solBalance);
            JLabel solLabel = new JLabel(solData);
            gbc.gridy++;
            infoPanel.add(solLabel, gbc);
        }
        if (xrpBalance <= 0) {
            xrpBalance = 0.0;
        } else {
            String xrpData = " ▪  XRP " + String.format(Locale.ROOT, "%.0f", xrpBalance);
            JLabel xrpLabel = new JLabel(xrpData);
            gbc.gridy++;
            infoPanel.add(xrpLabel, gbc);
        }


        JLabel dealsLabel = new JLabel("📝 Deals " + deals);
        gbc.gridy++;
        infoPanel.add(dealsLabel, gbc);

        JLabel profitLabel = new JLabel("🔝 Profit " + String.format(Locale.ROOT, "%.2f", profit) + "$");
        gbc.gridy++;
        gbc.weighty = 1.0;
        infoPanel.add(profitLabel, gbc);

        gbc.gridy++;
        gbc.weighty = 0.0;

        JButton setStrategyButton = new JButton("Set Strategy");
        setStrategyButton.setPreferredSize(new Dimension(profitLabel.getPreferredSize().width, setStrategyButton.getPreferredSize().height));
        gbc.fill = GridBagConstraints.HORIZONTAL;

        infoPanel.add(setStrategyButton, gbc);

        setStrategyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(infoPanel);
                createStrategyPanel();
                revalidate();
            }
        });

        add(infoPanel, BorderLayout.WEST);
        revalidate();
    }

    private void createStrategyPanel() {
        strategyPanel = new JPanel();
        strategyPanel.setLayout(new GridBagLayout());

        strategyPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 0);

        JLabel riskLabel = new JLabel(riskStatus);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        strategyPanel.add(riskLabel, gbc);

        JRadioButton lowRadioButton = new JRadioButton(TradingRisk.LOW.getLevel());
        JRadioButton mediumRadioButton = new JRadioButton(TradingRisk.MEDIUM.getLevel());
        JRadioButton highRadioButton = new JRadioButton(TradingRisk.HIGH.getLevel());

        ButtonGroup riskButtonGroup = new ButtonGroup();
        riskButtonGroup.add(lowRadioButton);
        riskButtonGroup.add(mediumRadioButton);
        riskButtonGroup.add(highRadioButton);

        if (selectedTradingRisk == TradingRisk.LOW) {
            lowRadioButton.setSelected(true);
        } else if (selectedTradingRisk == TradingRisk.MEDIUM) {
            mediumRadioButton.setSelected(true);
        } else if (selectedTradingRisk == TradingRisk.HIGH) {
            highRadioButton.setSelected(true);
        }

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS));
        radioButtonPanel.add(lowRadioButton);
        radioButtonPanel.add(mediumRadioButton);
        radioButtonPanel.add(highRadioButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 0, 10, 0);
        strategyPanel.add(radioButtonPanel, gbc);

        JLabel amountLabel = new JLabel(amountStatus + " " + PriceConfig.getDealAmount() + "$");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 0, 2, 0);
        strategyPanel.add(amountLabel, gbc);

        JSlider amountSlider = new JSlider(JSlider.HORIZONTAL, 25, 100, 25);
        amountSlider.setMajorTickSpacing(25);
        amountSlider.setMinorTickSpacing(5);
        amountSlider.setPaintTicks(true);
        amountSlider.setPaintLabels(true);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        amountSlider.setValue(selectedAmount);

        amountSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                amountLabel.setText(amountStatus + " " + source.getValue() + "$");
            }
        });
        strategyPanel.add(amountSlider, gbc);

        JLabel stopLossLabel = new JLabel("Stop loss:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        strategyPanel.add(stopLossLabel, gbc);

        String[] riskOptions = {"2%", "4%", "6%", "8%", "10%"};
        JComboBox<String> riskComboBox = new JComboBox<>(riskOptions);
        gbc.gridx = 0;
        gbc.gridy = 6;
        riskComboBox.setSelectedItem(selectedMargin + "%");
        strategyPanel.add(riskComboBox, gbc);

        JLabel timeLabel = new JLabel(timeStatus);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        strategyPanel.add(timeLabel, gbc);

        JCheckBox dayCheckBox = new JCheckBox(TradingTime.DAY.getTime());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        strategyPanel.add(dayCheckBox, gbc);

        JCheckBox nightCheckBox = new JCheckBox(TradingTime.NIGHT.getTime());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        strategyPanel.add(nightCheckBox, gbc);

        if (selectedTradingTime == TradingTime.DAY) {
            dayCheckBox.setSelected(true);
        }
        if (selectedTradingTime == TradingTime.NIGHT) {
            nightCheckBox.setSelected(true);
        }

        if (selectedTradingTime == TradingTime.ALL) {
            dayCheckBox.setSelected(true);
            nightCheckBox.setSelected(true);
        }


        gbc.gridy = 10;
        gbc.weighty = 1.0;
        strategyPanel.add(new JPanel(), gbc);

        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.5;

        JButton backButton = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 1;

        gbc.insets = new Insets(10, 0, 0, 5);
        strategyPanel.add(backButton, gbc);

        JButton applyButton = new JButton("Apply");
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, -35, 0, 0);
        strategyPanel.add(applyButton, gbc);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(strategyPanel);
                createInfoPanel();
                revalidate();
                repaint();
            }
        });

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRisk = "";
                if (lowRadioButton.isSelected()) {
                    selectedRisk = TradingRisk.LOW.name();
                } else if (mediumRadioButton.isSelected()) {
                    selectedRisk = TradingRisk.MEDIUM.name();
                } else if (highRadioButton.isSelected()) {
                    selectedRisk = TradingRisk.HIGH.name();
                }

                selectedTradingRisk = TradingRisk.valueOf(selectedRisk);

                String selectedTime = "";
                if (dayCheckBox.isSelected()) {
                    selectedTime = TradingTime.DAY.name();
                }
                if (nightCheckBox.isSelected()) {
                    selectedTime = TradingTime.NIGHT.name();
                }
                if (dayCheckBox.isSelected() && nightCheckBox.isSelected()) {
                    selectedTime = TradingTime.ALL.name();
                }
                if (!dayCheckBox.isSelected() && !nightCheckBox.isSelected()) {
                    selectedTime = TradingTime.NONE.name();
                }
                selectedTradingTime = TradingTime.valueOf(selectedTime.trim().toUpperCase());
                String selectedRiskComboBox = (String) riskComboBox.getSelectedItem();
                PriceConfig.setSelectedRisk(selectedRisk);
                PriceConfig.setDealAmount((double) amountSlider.getValue());
                selectedAmount = amountSlider.getValue();

                if (selectedRiskComboBox != null) {
                    selectedMargin = Integer.parseInt(selectedRiskComboBox.replace("%", ""));
                    PriceConfig.setMarginCallPercentage(Double.valueOf(selectedRiskComboBox.replace("%", "")));
                }

                remove(strategyPanel);
                createInfoPanel();
                revalidate();
                repaint();

                Optional<User> userOptional = userRepository.findById(1L);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setDealamount((double) amountSlider.getValue());

                    user.setTradingtime(selectedTradingTime.name().toLowerCase());
                    user.setTradingrisk(selectedTradingRisk.name().toLowerCase());
                    if (selectedRiskComboBox != null)
                        user.setMargincall(Double.valueOf(selectedRiskComboBox.replace("%", "")));
                    userRepository.save(user);

                    try {

                        webSocketConnection.sendMessage("config", selectedTradingRisk.name().toLowerCase());
                    } catch (IOException ex) {

                    }

                    saveLogs("Info", "Config", selectedTradingRisk.name().toLowerCase() + " " + selectedRiskComboBox + " " + amountSlider.getValue());
                }
            }
        });

        add(strategyPanel, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    @Override
    public void onConnectionEstablished() {
        connectedStatus = ConnectionStatus.CONNECTED;
        connectedLabel.setText(connectedStatus.getStatus());
        circleColor = Color.GREEN;
        greenCirclePanel.repaint();

    }

    @Override
    public void onConnectionClosed(CloseStatus status) {
        connectedStatus = ConnectionStatus.CLOSED;
        connectedLabel.setText(connectedStatus.getStatus());
        circleColor = Color.RED;
        greenCirclePanel.repaint();

    }

    @Override
    public void onTextMessageReceived(String message) {
        saveLogs("Info", "UI", "Informational notification " + message);
    }

    @Override
    public void onMessageReceived(String message) {

        try {
            if (message != null) {
                boolean needRepaint = false;
                boolean margin = false;
                String currency = "";
                String action = "";

                StringBuilder userMessage = new StringBuilder();


                JSONObject notifyObject = new JSONObject(message);
                if (notifyObject.has("currency")) {
                    currency = notifyObject.getString("currency");
                    userMessage.append(currency.toUpperCase());
                }

                if (notifyObject.has("action")) {
                    action = notifyObject.getString("action");
                }

                if (action.equals("buy")) {
                    userMessage.append(" purchased currency.");
                }
                if (action.equals("sell")) {
                    userMessage.append(" sold currency.");
                }
                if (action.equals("margin")) {
                    userMessage.append(" margin call is executed.");
                }

                if (notifyObject.has("AdaBalance")) {
                    adaBalance = notifyObject.getDouble("AdaBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("BtcBalance")) {
                    btcBalance = notifyObject.getDouble("BtcBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("DogBalance")) {
                    dogBalance = notifyObject.getDouble("DogBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("EtcBalance")) {
                    etcBalance = notifyObject.getDouble("EtcBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("EthBalance")) {
                    ethBalance = notifyObject.getDouble("EthBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("LnkBalance")) {
                    lnkBalance = notifyObject.getDouble("LnkBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("LtcBalance")) {
                    ltcBalance = notifyObject.getDouble("LtcBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("MatBalance")) {
                    matBalance = notifyObject.getDouble("MatBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("SolBalance")) {
                    solBalance = notifyObject.getDouble("SolBalance");
                    needRepaint = true;
                }
                if (notifyObject.has("XrpBalance")) {
                    xrpBalance = notifyObject.getDouble("XrpBalance");
                    needRepaint = true;
                }

                if (notifyObject.has("deals")) {
                    deals = notifyObject.getInt("deals");
                    needRepaint = true;
                    userMessage.append(" Total trades " + deals);
                }
                if (notifyObject.has("profit")) {
                    profit = notifyObject.getDouble("profit");
                    needRepaint = true;
                    userMessage.append(" 🔝 Profit " + String.format(Locale.ROOT, "%.2f", profit) + "$");
                }
                if (needRepaint) {
                    remove(strategyPanel);
                    remove(infoPanel);
                    createInfoPanel();
                    infoPanel.revalidate();
                    infoPanel.repaint();
                    saveLogs("Info", "Order", message);


                    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    CustomNotification notification = new CustomNotification(this, "", userMessage.toString());
                    notification.setVisible(true);
                }
            }
        } catch (Exception e) {
            saveLogs("Error", "Received", "Invalid JSON: missing required keys");
        }
    }

    @Override
    public void onError(Throwable ex) {
        connectedStatus = ConnectionStatus.CONNECTION_ERROR;
        connectedLabel.setText(connectedStatus.getStatus());
        circleColor = Color.RED;
        greenCirclePanel.repaint();
        saveLogs("Error", "Connect", connectedStatus.getStatus());
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

            String filePathStr = home + "/flitter-log.json";
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
