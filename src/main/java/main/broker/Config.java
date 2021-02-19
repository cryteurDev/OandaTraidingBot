package main.broker;

import com.oanda.v20.account.AccountID;
import main.main.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String URL = OandaData.URL;
    public static final String TOKEN = OandaData.TOKEN;
    public static final AccountID ACCOUNT_ID = OandaData.ACCOUNT_ID;
    public static final int SLEEP = 60;

    public static List<Instrument> instrumentList = new ArrayList<>();

    public static final String[] list = {
            "AUD_CAD",
            "AUD_CHF",
            "AUD_JPY",
            "AUD_NZD",
            "AUD_USD",
            "CAD_JPY",
            "EUR_AUD",
            "EUR_CAD",
            "EUR_CHF",
            "EUR_GBP",
            "EUR_JPY",
            "EUR_USD",
            "GBP_AUD",
            "GBP_CHF",
            "GBP_JPY",
            "GBP_NZD",
            "NZD_JPY",
            "NZD_USD",
            "USD_CAD",
            "USD_CHF",
            "USD_JPY"
    };

    public static final String[] list2 = {
            "AUD_CAD"
            , "AUD_CHF"
            , "AUD_HKD", "AUD_JPY"
            , "AUD_NZD", "AUD_SGD", "AUD_USD", "CAD_CHF"
            , "CAD_HKD", "CAD_JPY", "CAD_SGD", "CHF_HKD"
            , "CHF_JPY", "CHF_ZAR", "EUR_AUD", "EUR_CAD"
            , "EUR_CHF", "EUR_CZK", "EUR_DKK", "EUR_GBP"
            , "EUR_HKD", "EUR_HUF", "EUR_JPY", "EUR_NOK"
            , "EUR_NZD", "EUR_PLN", "EUR_SEK", "EUR_SGD"
            , "EUR_TRY", "EUR_USD", "EUR_ZAR", "GBP_AUD"
            , "GBP_CAD", "GBP_CHF", "GBP_HKD", "GBP_JPY"
            , "GBP_NZD", "GBP_PLN", "GBP_SGD", "GBP_USD"
            , "GBP_ZAR", "HKD_JPY", "NZD_CAD", "NZD_CHF"
            , "NZD_HKD", "NZD_JPY", "NZD_SGD", "NZD_USD"
            , "SGD_CHF", "SGD_HKD", "SGD_JPY", "TRY_JPY"
            , "USD_CAD", "USD_CHF", "USD_CNH", "USD_CZK"
            , "USD_DKK", "USD_HKD", "USD_HUF", "USD_JPY"
            , "USD_MXN", "USD_NOK", "USD_PLN", "USD_SAR"
            , "USD_SEK", "USD_SGD", "USD_THB", "USD_TRY"
            , "USD_ZAR", "ZAR_JPY"
    };

    public static final List<String> INSTRUMENTS= new ArrayList<>(Arrays.asList(list));

    public static String currentInstrument = "USD_TRY";

    public static double sum = 1000;

    public static String[] getList() {
        return list;
    }

    public static String getCurrentInstrument() {
        return currentInstrument;
    }

    public static void setCurrentInstrument(String currentInstrument) {
        Config.currentInstrument = currentInstrument;
    }

    public static Instrument getInstrumentByName(String instrumentName) {
        for (Instrument instrument : instrumentList) {
            if (instrument.getName().equals(instrumentName))
                return instrument;
        }

        return null;
    }

    public static void updateInstrumentData(Instrument newInstrument) {
        for (Instrument instrument : instrumentList) {
            if (instrument.getName().equals(newInstrument.getName())) {
                instrument.setPrice(newInstrument.getPrice());
                instrument.setAverageMove(newInstrument.getAverageMove());
                instrument.setOrder(newInstrument.getOrder());
                instrument.setSendOnOpen(newInstrument.isSendOnOpen());
                instrument.setSendOnClose(newInstrument.isSendOnClose());
            }
        }
    }

    public static void setInstrumentPricesList(Instrument newInstrument) {
        for (int i = 0; i < instrumentList.size(); i++) {
            Instrument instrument = instrumentList.get(i);

            if (instrument.getName().equals(newInstrument.getName()))
                instrumentList.get(i).setPricesList(newInstrument.getPricesList());
        }
    }

    public static int getInstrumentIdByName(String instrumentName) {
        for (int i = 0; i < instrumentList.size(); i++) {
            if (instrumentList.get(i).getName().equals(instrumentName))
                return i;
        }

        return -1;
    }

    public static void init(int avg1, int avg2) {
        //создание списка инструментов на основе списка валютных пар
        for (String instrument : INSTRUMENTS) {
            instrumentList.add(new Instrument(instrument));
        }

        TradeConst.MIN_AVG = avg1;
        TradeConst.MAX_AVG = avg2;

        firstLoad();

        System.out.println("Instrument list created. Size: " + instrumentList.size());
    }

    public static void firstLoad() {
        //загрузка информации по инструментам из базы
        Main.pricesBase.refreshInstrumentsList(TradeConst.MAX_AVG);

        for (Instrument instrument : instrumentList) {
            //определение направления графика на основе последних значений средних линий

            Double min = instrument.getAvgListByPriceCount(TradeConst.MIN_AVG, 1).get(0);
            instrument.getAverageMove().setLineMinValue(min);

            Double max = instrument.getAvgListByPriceCount(TradeConst.MAX_AVG, 1).get(0);
            instrument.getAverageMove().setLineMaxValue(max);

            if (min > max) {
                instrument.getAverageMove().setDirection(TradeConst.DIRECTION_UP);
            }
            if (max > min) {
                instrument.getAverageMove().setDirection(TradeConst.DIRECTION_DOWN);
            }

//            System.out.println(instrument.getAverageMove().toString());
        }
    }
}
