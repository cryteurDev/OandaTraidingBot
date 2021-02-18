package main;

import main.bot.Monitor;
import main.broker.Config;
import main.db.PricesBase;
import main.view.TradingChart;

public class Main {
    public static PricesBase pricesBase = new PricesBase();
    public static TradingChart chart = new TradingChart();

    public static void main(String[] args) {

        Config.init(7, 20);

        new Thread(() -> {
            while (true) {
                Monitor.start();
            }
        }
        ).start();


        chart.display();

    }
}
