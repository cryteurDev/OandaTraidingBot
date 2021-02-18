package main.db;

import main.broker.Config;
import main.broker.Instrument;

import java.util.List;

public class PricesBase extends BaseConnection {

    public PricesBase() {
        super("prices.db");
    }

    public Instrument getInstrumentData(int countLastRecords, String instrumentName) {
        refreshInstrumentsList(countLastRecords);

        return Config.getInstrumentByName(instrumentName);
    }

    public void refreshInstrumentsList(int countLastRecords) {
        List<Instrument> instrumentList = runSQL(createSQLGetRecords(countLastRecords));

        for (Instrument instrument : instrumentList) {
            for (Instrument instrument1 : Config.instrumentList) {
                if (instrument.getName().equals(instrument1.getName())) {
                    instrument1.setPrice(instrument.getPrice());
                    instrument1.setPricesList(instrument.getPricesList());

                    double min = 999999;
                    double max = 0;
                    for (Double d : instrument.getPricesList()) {
                        if (d < min)
                            min = d;
                        if (d > max)
                            max = d;
                    }
                    instrument1.setMin(min);
                    instrument1.setMax(max);
                }
            }
        }
    }

    private String createSQLGetRecords(int count) {
        String beginString = "SELECT ";

        StringBuilder select = new StringBuilder();

        for (String instrument : Config.INSTRUMENTS) {
            select.append(instrument).append(", ");
        }

        select.delete(select.length() - 2, select.length());

        String endString = " FROM (SELECT * FROM prices ORDER BY id DESC LIMIT " + count + ") ORDER BY id ASC;";

        return beginString + select + endString;
    }
}
