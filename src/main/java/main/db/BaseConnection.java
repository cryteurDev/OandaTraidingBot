package main.db;

import main.broker.Config;
import main.broker.Instrument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseConnection {
    private static final String URL = "jdbc:sqlite:";
    private Connection connection;

    public BaseConnection(String base_name) {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
            this.connection = DriverManager.getConnection(URL + base_name);
            System.out.println("connection " + base_name + " ok");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Instrument> runSQL(String sql) {
        List<Instrument> res = new ArrayList<>();

        for (String instrument : Config.INSTRUMENTS) {
            res.add(new Instrument(instrument));
        }

//        System.out.println(sql);
        try (Statement statement = this.connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                String[] columns = getColumns(resultSet.getMetaData());

/*                for (String column : columns) {
                    Instrument instrument = new Instrument();
                    instrument.setName(column);
                    res.add(instrument);
                }*/
                while (resultSet.next()) {
                    for (String column : columns) {
                        for (Instrument instrument : res) {
                            if (instrument.getName().equals(column)) {
                                instrument.getPricesList().add((Double) resultSet.getObject(column));
                                instrument.setPrice((Double) resultSet.getObject(column));
                            }
                        }
//                        int id = Config.getInstrumentIdByName(column);
//                        Config.instrumentList.get(id).getPricesList().add((Double) resultSet.getObject(column));
//                        Config.instrumentList.get(id).setPrice((Double) resultSet.getObject(column));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void updateSQL(String sql) {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("record added");
        } catch (SQLException e1) {
            System.err.println(e1.getMessage());
        }
    }

    public String[] getColumns(ResultSetMetaData metadata) throws SQLException {

        String[] columns = new String[metadata.getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = metadata.getColumnName(i + 1);
        }

        return columns;

    }
}

