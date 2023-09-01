package org.flitter.app.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MarketService {
    private final ExecutorService executor;


    @Autowired
    private AdaService adaService;

    @Autowired
    private BtcService btcService;

    @Autowired
    private DogService dogService;

    @Autowired
    private EtcService etcService;

    @Autowired
    private EthService ethService;

    @Autowired
    private LnkService lnkService;

    @Autowired
    private LtcService ltcService;

    @Autowired
    private MatService matService;

    @Autowired
    private SolService solService;

    @Autowired
    private XrpService xrpService;


    public MarketService() {
        executor = Executors.newFixedThreadPool(5);
    }

    public void processMessage(String message, String text) {

        executor.submit(new MessageProcessor(message, text));
    }


    private class MessageProcessor implements Runnable {
        private final String message;
        private final String text;

        public MessageProcessor(String message, String text) {
            this.message = message;
            this.text = text;
        }

        @Override
        public void run() {
            switch (text) {
                case "ada" -> adaService.createOrder(message);
                case "btc" -> btcService.createOrder(message);
                case "dog" -> dogService.createOrder(message);
                case "etc" -> etcService.createOrder(message);
                case "eth" -> ethService.createOrder(message);
                case "lnk" -> lnkService.createOrder(message);
                case "ltc" -> ltcService.createOrder(message);
                case "mat" -> matService.createOrder(message);
                case "sol" -> solService.createOrder(message);
                case "xrp" -> xrpService.createOrder(message);
            }
        }
    }
}
