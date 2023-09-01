package org.flitter.app;


import org.flitter.app.currency.ada.AdaService;
import org.flitter.app.currency.btc.BtcService;
import org.flitter.app.currency.dog.DogService;
import org.flitter.app.currency.etc.EtcService;
import org.flitter.app.currency.eth.EthService;
import org.flitter.app.currency.lnk.LnkService;
import org.flitter.app.currency.ltc.LtcService;
import org.flitter.app.currency.mat.MatService;
import org.flitter.app.currency.sol.SolService;
import org.flitter.app.currency.xrp.XrpService;
import org.flitter.app.dao.UserRepository;
import org.flitter.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.swing.*;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class FlitterApplication extends JFrame {
    public static ConfigurableApplicationContext context;
    private final UserRepository userRepository;

    @Autowired
    public FlitterApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        context = SpringApplication.run(FlitterApplication.class, args);

        SwingUtilities.invokeLater(() -> {
            Environment env = context.getBean(Environment.class);
            UserRepository userRepository = context.getBean(UserRepository.class);
            WebSocketConnection webSocketConnection = context.getBean(WebSocketConnection.class);
            AdaService adaService = context.getBean(AdaService.class);
            BtcService btcService = context.getBean(BtcService.class);
            DogService dogService = context.getBean(DogService.class);
            EtcService etcService = context.getBean(EtcService.class);
            EthService ethService = context.getBean(EthService.class);
            LnkService lnkService = context.getBean(LnkService.class);
            LtcService ltcService = context.getBean(LtcService.class);
            MatService matService = context.getBean(MatService.class);
            SolService solService = context.getBean(SolService.class);
            XrpService xrpService = context.getBean(XrpService.class);


            PrivateConfig.LOG_URL = env.getProperty("app.home");
            Flitter flitter = new Flitter(userRepository, env.getProperty("app.home"), webSocketConnection);
            flitter.setVisible(true);

            webSocketConnection.setFlitter(flitter);
            webSocketConnection.setEnv(env);
            webSocketConnection.registerObserver(flitter);
            webSocketConnection.start();

            adaService.registerObserver(flitter);
            btcService.registerObserver(flitter);
            dogService.registerObserver(flitter);
            etcService.registerObserver(flitter);
            ethService.registerObserver(flitter);
            lnkService.registerObserver(flitter);
            ltcService.registerObserver(flitter);
            matService.registerObserver(flitter);
            solService.registerObserver(flitter);
            xrpService.registerObserver(flitter);
        });
    }

    @PostConstruct
    public void initData() {
        if (!userRepository.existsById(1L)) {
            User user = new User();
            user.setId(1L);
            user.setName("Flitter");
            user.setHisskey("");
            user.setApikey("");
            user.setSecretkey("");
            user.setDealamount(25.0);
            user.setBalance(2000.0);
            user.setMargincall(6.0);
            user.setTradingtime("All");
            user.setTradingrisk("Low");
            user.setProfit(0.0);
            user.setDeals(0);
            user.setAdaBalance(0.0);
            user.setAdaLimit(20);
            user.setBtcBalance(0.0);
            user.setBtcLimit(20);
            user.setDogBalance(0.0);
            user.setDogLimit(20);
            user.setEtcBalance(0.0);
            user.setEtcLimit(20);
            user.setEthBalance(0.0);
            user.setEthLimit(20);
            user.setLnkBalance(0.0);
            user.setLnkLimit(20);
            user.setLtcBalance(0.0);
            user.setLtcLimit(20);
            user.setMatBalance(0.0);
            user.setMatLimit(20);
            user.setSolBalance(0.0);
            user.setSolLimit(20);
            user.setXrpBalance(0.0);
            user.setXrpLimit(20);
            userRepository.save(user);
        }
    }

}

