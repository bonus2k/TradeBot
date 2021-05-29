package com.example.tradebot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public enum Symbol {
    ADAUSDT("ADA", 1, 4, false, 0.0),
    ATOMUSDT("ATOM", 3, 3, false, 0.0),
    BCHUSDT("BCH", 5, 2, false, 0.0),
    BTCUSDT("BTC", 6, 2, false, 0.0),
    BNBUSDT("BNB", 2, 2,false, 0.0),
    DASHUSDT("DASH", 5, 2,false, 0.0),
    DOTUSDT("DOT", 2, 3,false, 0.0),
    EOSUSDT("EOS", 2, 4,false, 0.0),
    ETHUSDT("ETH", 5, 2,false, 0.0),
    FILUSDT("FIL", 2, 2,false, 0.0),
    LINKUSDT("LINK", 2, 3,false, 0.0),
//    LTCUSDT("LTC", 5),
    NEOUSDT("NEO", 3, 3,false, 0.0),
    OXTUSDT("OXT", 2, 4,false, 0.0),
    SOLUSDT("SOL", 2, 3,false, 0.0),
    TRXUSDT("TRX", 1, 5,false, 0.0),
    UNIUSDT("UNI", 2, 3,false, 0.0),
    VETUSDT("VET", 0, 5,false, 0.0),
    XLMUSDT("XLM", 1, 5,false, 0.0),
    XMRUSDT("XMR", 5, 2,false, 0.0),
    XRPUSDT("XRP",1, 4,false, 0.0),
    ZECUSDT("ZEC", 5, 2,false, 0.0);

    private String asset;
    private int scale;
    private int scaleLimit;
    private boolean limit;
    private Double percent;

    Symbol(String asset, int scale, int scaleLimit, boolean limit, Double percent) {
        this.asset = asset;
        this.scale = scale;
        this.scaleLimit=scaleLimit;
        this.limit = limit;
        this.percent = percent;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    public void setLimit(String limit) {
        this.limit = "on".equals(limit);
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
