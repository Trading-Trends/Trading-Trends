package com.tradingtrends.coin.infrastructure;

public enum CoinTopic {
    UPBIT_DATA("upbit-data");

    private final String topic;

    CoinTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
