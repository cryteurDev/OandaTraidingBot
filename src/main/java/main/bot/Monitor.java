package main.bot;

import main.Main;
import main.Utilits;
import main.broker.Broker;
import main.broker.Config;
import main.broker.Instrument;
import main.broker.TradeConst;

import java.util.List;
import java.util.logging.Logger;

public class Monitor {
    static Logger log = Logger.getLogger(Monitor.class.getName());

    public static void start() {
        // если мин больше макс то пересечение вверх
        // если макс больше мин то пересечение вниз

        Utilits.sleep(Config.SLEEP);
        System.out.println(Utilits.getTime("HH:mm:ss") + " Analyses data");
        System.out.println("--------------------------------");

        refreshData();

        scanAvgMove();

        analysisInstruments();

    }

    public static void analysisInstruments() {
        for (Instrument instrument : Config.instrumentList) {
//            String direction = instrument.getAverageMove().getDirection();
            boolean crossDown = instrument.getAverageMove().isCrossOnDown();
            boolean crossUp = instrument.getAverageMove().isCrossOnUp();

            if (crossUp && !instrument.isSendOnOpen()) {
//                Broker.openTestOrder(instrument);
                Broker.sendAdvMsgUp(instrument);

                instrument.setSendOnOpen(true);
                instrument.setSendOnClose(false);
            }

            if (crossDown && !instrument.isSendOnClose() && instrument.isSendOnOpen()) {
//                Broker.closeTestOrder(instrument);
                Broker.sendAdvMsgDown(instrument);

                instrument.setSendOnOpen(false);
                instrument.setSendOnClose(true);
            }
        }
    }

    public static void scanAvgMove() {
        for (Instrument instrument : Config.instrumentList) {
            double minAvg = instrument.getAverageMove().getLineMinValue();
            double maxAvg = instrument.getAverageMove().getLineMaxValue();
            int duration = instrument.getAverageMove().getDurationMove();
            String direction = instrument.getAverageMove().getDirection();

            if (minAvg > maxAvg && direction.equals(TradeConst.DIRECTION_DOWN)) {
                instrument.getAverageMove().setDurationMove(++duration);
                if (duration == 1) {
                    instrument.getAverageMove().setDurationMove(0);
                    instrument.getAverageMove().setCrossOnUp(true);
                    instrument.getAverageMove().setCrossOnDown(false);
                    instrument.getAverageMove().setDirection(TradeConst.DIRECTION_UP);
                }
            }

            if (maxAvg > minAvg && direction.equals(TradeConst.DIRECTION_UP)) {
                instrument.getAverageMove().setDurationMove(++duration);

                if (duration == 1) {
                    instrument.getAverageMove().setDurationMove(0);
                    instrument.getAverageMove().setCrossOnDown(true);
                    instrument.getAverageMove().setCrossOnUp(false);
                    instrument.getAverageMove().setDirection(TradeConst.DIRECTION_DOWN);
                }
            }
        }
    }

    public static void refreshData() {
        Main.pricesBase.refreshInstrumentsList(TradeConst.MAX_AVG);

        for (Instrument instrument : Config.instrumentList) {
            Double min = instrument.getAvgListByPriceCount(TradeConst.MIN_AVG, 1).get(0);
            instrument.getAverageMove().setLineMinValue(min);

            Double max = instrument.getAvgListByPriceCount(TradeConst.MAX_AVG, 1).get(0);
            instrument.getAverageMove().setLineMaxValue(max);
        }
    }
}
