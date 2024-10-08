package com.tradingtrends.stock.infrastructure;

public enum StockTopic {
    STOCK_DATA("stock-data");

    private final String topic;

    StockTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
