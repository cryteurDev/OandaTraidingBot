package main.broker;

import com.oanda.v20.transaction.TransactionID;

import java.util.ArrayList;
import java.util.List;

public class Instrument {
    private String name;
    private double price;
    private double min;
    private double max;
    private List<Double> pricesList = new ArrayList<>();
    private List<TransactionID> tradeIdList = new ArrayList<>();
    private AverageMove averageMove = new AverageMove();
    private Order order = new Order();
    private boolean send = false;
    private boolean send_s2 = false;
    private boolean sendOnOpen = false;
    private boolean sendOnClose = false;

    public Instrument(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", min=" + min +
                ", max=" + max +
                ", prices_list=" + pricesList +
//                ", tradeIdList=" + tradeIdList +
                '}';
    }

    public List<Double> getAvgListByPriceCount(int countForAvg, int count) {
        List<Double> list = new ArrayList<>();
        int s = pricesList.size();

        for (int i = s - count; i < s; i++) {
            double d = 0;

            for (int j = i; j > i - countForAvg; j--) {
                d = d + pricesList.get(j);
            }

            list.add(d / countForAvg);
        }

        return list;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Double> getPricesList() {
        return pricesList;
    }

    public void setPricesList(List<Double> pricesList) {
        this.pricesList = pricesList;
    }

    public List<TransactionID> getTradeIdList() {
        return tradeIdList;
    }

    public AverageMove getAverageMove() {
        return averageMove;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public Order getOrder() {
        return order;
    }

    public void setAverageMove(AverageMove averageMove) {
        this.averageMove = averageMove;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isSendOnOpen() {
        return sendOnOpen;
    }

    public void setSendOnOpen(boolean sendOnOpen) {
        this.sendOnOpen = sendOnOpen;
    }

    public boolean isSendOnClose() {
        return sendOnClose;
    }

    public void setSendOnClose(boolean sendOnClose) {
        this.sendOnClose = sendOnClose;
    }

    public boolean isSend_s2() {
        return send_s2;
    }

    public void setSend_s2(boolean send_s2) {
        this.send_s2 = send_s2;
    }
}
