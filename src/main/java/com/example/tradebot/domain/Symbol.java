package com.example.tradebot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public enum Symbol {
    ADAUSDT("ADA", 1),
    ATOMUSDT("ATOM", 3),
    BCHUSDT("BCH", 5),
    BTCUSDT("BTC", 6),
    BNBUSDT("BNB", 2),
    DASHUSDT("DASH", 5),
    DOTUSDT("DOT", 2),
    EOSUSDT("EOS", 2),
    ETHUSDT("ETH", 5),
    FILUSDT("FIL", 2),
    LINKUSDT("LINK", 2),
//    LTCUSDT("LTC", 5),
    NEOUSDT("NEO", 3),
    OXTUSDT("OXT", 2),
    SOLUSDT("SOL", 2),
    TRXUSDT("TRX", 1),
    UNIUSDT("UNI", 2),
    VETUSDT("VET", 0),
    XLMUSDT("XLM", 1),
    XMRUSDT("XMR", 5),
    XRPUSDT("XRP",1),
    ZECUSDT("ZEC", 5);

    private String asset;
    private int scale;

    Symbol(String asset, int scale) {
        this.asset = asset;
        this.scale = scale;
    }

}
