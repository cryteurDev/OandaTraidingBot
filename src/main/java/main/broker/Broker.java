package main.broker;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeCloseResponse;
import com.oanda.v20.trade.TradeSpecifier;
import com.oanda.v20.transaction.OrderFillTransaction;
import com.oanda.v20.transaction.TradeReduce;
import com.oanda.v20.transaction.TransactionID;
import main.Main;
import main.Utilits;
import main.bot.Bot;
import main.bot.BotIds;

import java.util.ConcurrentModificationException;
import java.util.List;

public class Broker {
    private static Context ctx = new ContextBuilder(Config.URL)
            .setToken(Config.TOKEN)
            .setApplication("PricePolling")
            .build();

    public static AccountSummary getAccountInfo() {
        try {
            AccountSummary summary = ctx.account.summary(Config.ACCOUNT_ID).getAccount();
//            System.out.println(summary);
            return summary;
        } catch (ExecuteException | RequestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static double getBalance() {
        return getAccountInfo().getBalance().doubleValue();
    }

    public static int sendAdvMsgUp(Instrument instrument) {
        int orderNumber = Utilits.getRandom(100000);
        double balance = getBalance();
        makeOrder(Config.ACCOUNT_ID, instrument, TradeConst.BUY);

        try {
            Main.chart.drawAndMakeScreen(instrument.getName());
        } catch (ConcurrentModificationException e) {
            System.err.println(e.getMessage());
        }

        Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_ADVISOR,
                "#" + instrument.getName() + "_up"
        );

        Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_TRADING_INFO, "#open #order_" + orderNumber + ":\n"
                + "#" + instrument.getName()
                + " - " + instrument.getPrice()
                + "\n" + balance
        );

        instrument.setSend(true);
        instrument.getOrder().setOpen(true);
        instrument.getOrder().setNumber(orderNumber);
        instrument.getOrder().setPrice(instrument.getPrice());

        Config.updateInstrumentData(instrument);

        return orderNumber;
    }

    public static int sendAdvMsgDown(Instrument instrument) {
        try {
            Main.chart.drawAndMakeScreen(instrument.getName());
        } catch (ConcurrentModificationException e) {
            System.err.println(e.getMessage());
        }

        if (instrument.getOrder().isOpen()) {
            makeOrder(Config.ACCOUNT_ID, instrument, TradeConst.SELL);
            double balance = getBalance();

            Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_TRADING_INFO, "#close #order_" + instrument.getOrder().getNumber() + ":\n"
                    + "#" + instrument.getName()
                    + " - " + instrument.getPrice()
                    + "\n" + balance
            );

            instrument.getOrder().setOpen(false);
        }

        //note ___________________________________
        Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_ADVISOR,
                "#" + instrument.getName() + "_down"
        );
        //note ___________________________________

        instrument.setSend(true);
        instrument.getOrder().setOpen(true);

        Config.updateInstrumentData(instrument);

        return -1;
    }

    public static int openTestOrder(Instrument instrument) {
        int orderNumber = Utilits.getRandom(100000);

        double balance = getBalance();

        makeOrder(Config.ACCOUNT_ID, instrument, TradeConst.BUY);

        try {
            Main.chart.drawAndMakeScreen(instrument.getName());
        } catch (ConcurrentModificationException e) {
            System.err.println(e.getMessage());
        }


        Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_TRADING_INFO, "#open #order_" + orderNumber + ":\n"
                + "#" + instrument.getName()
                + " - " + instrument.getPrice()
                + "\n" + balance
        );

        instrument.setSend(true);
        instrument.getOrder().setNumber(orderNumber);
        instrument.getOrder().setPrice(instrument.getPrice());
        instrument.getOrder().setOpen(true);

        Config.updateInstrumentData(instrument);

        return orderNumber;
    }

    public static void closeTestOrder(Instrument instrument) {
        if (instrument.getOrder().isOpen()) {
            makeOrder(Config.ACCOUNT_ID, instrument, TradeConst.SELL);

            double balance = getBalance();
            instrument.getOrder().setOpen(false);

            Main.chart.drawAndMakeScreen(instrument.getName());

            Bot.getBotInstance().sendScreen("chart.jpg", BotIds.ID_TRADING_INFO, "#close #order_" + instrument.getOrder().getNumber() + ":\n"
                    + "#" + instrument.getName()
                    + " - " + instrument.getPrice()
                    + "\n" + balance
            );

            Config.updateInstrumentData(instrument);
        }
    }

    public static void makeOrder(AccountID accountId, Instrument instrument, String operation) {
        TransactionID tradeId;

        double sum = Config.sum;

        if (operation.equals(TradeConst.SELL))
            sum = instrument.getOrder().getSum() * -1;
        if (operation.equals(TradeConst.BUY))
            if (sum < 0)
                sum = sum * -1;

        try {
            OrderCreateRequest request = new OrderCreateRequest(accountId);
            MarketOrderRequest marketorderrequest = new MarketOrderRequest();
            marketorderrequest.setInstrument(instrument.getName());
            marketorderrequest.setUnits(sum);
            request.setOrder(marketorderrequest);
            OrderCreateResponse response = ctx.order.create(request);
            OrderFillTransaction transaction = response.getOrderFillTransaction();
//            tradeId = transaction.getId();

            Instrument instrument1 = Config.getInstrumentByName(instrument.getName());
            instrument1.getOrder().setPrice(instrument.getPrice());
            instrument1.getOrder().setSum(sum);

            Config.updateInstrumentData(instrument1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        Config.instrumentList.get().getTradeIdList().add(tradeId);
    }

    public static void closeOrder(AccountID accountId, String instrument) {
        try {

//            for (TransactionID transactionID : Config.instrumentList.get(Config.getInstrumentIdByName(instrument)).getTradeIdList()) {
//                System.out.println(transactionID);
//            }

            Instrument instrument1 = Config.instrumentList.get(
                    Config.getInstrumentIdByName(Config.currentInstrument)
            );

            TransactionID tradeId = instrument1.getTradeIdList().get(instrument1.getTradeIdList().size() - 1);

            System.out.println(instrument1.toString());

            System.out.println("try closed " + tradeId);

            TradeCloseResponse response = ctx.trade.close(
                    new TradeCloseRequest(accountId, new TradeSpecifier(tradeId.toString())));

            OrderFillTransaction transaction = response.getOrderFillTransaction();
            System.out.println(transaction.toString());
            List<TradeReduce> trades = transaction.getTradesClosed();

            if (trades.size() != 1)
                throw new RuntimeException("Only 1 trade was expected to be closed");
            TradeReduce trade = trades.get(0);
            if (!trade.getTradeID().equals(tradeId))
                throw new RuntimeException("The wrong trade was closed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
