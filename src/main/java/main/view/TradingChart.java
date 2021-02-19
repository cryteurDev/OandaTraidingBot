package main.view;

import main.main.Main;
import main.main.Utilits;
import main.broker.Config;
import main.broker.Instrument;
import main.broker.TradeConst;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class TradingChart {
    JFreeChart chart;
    ChartPanel chartPanel;
    NumberAxis yAxis;
    XYPlot plot;
    XYSeries linePrice;
    XYSeries lineAvgMin;
    XYSeries lineAvgMax;

    public void display() {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("TraidingBot");
            frame.setSize(1000, 600);

            JList<String> list = new JList<>(Config.getList());
            list.addListSelectionListener(e -> {
                Config.setCurrentInstrument(list.getSelectedValue());
                drawChart();
            });

            JButton buttonOpen = new JButton("Open");
            buttonOpen.addActionListener(e -> {
//                Broker.makeOrder(Config.ACCOUNT_ID, , 10, TradeConst.BUY);
            });

            JButton buttonClose = new JButton("Close");
            buttonClose.addActionListener(e -> {
//                Broker.closeOrder(Config.ACCOUNT_ID, Config.current_instrument);
//                Broker.makeOrder(Config.ACCOUNT_ID, Config.currentInstrument, 10, TradeConst.SELL);
            });

            JButton buttonSaveScreen = new JButton("Save");
            buttonSaveScreen.addActionListener(e -> {
                saveChartToJpeg();
            });

            JTextField fieldSum = new JTextField("1000");

            JButton buttonSetSum = new JButton("Set");
            buttonSaveScreen.addActionListener(e -> {
                Config.sum = Double.parseDouble(fieldSum.getText());
            });

            JTextField fieldMin = new JTextField("15");
            JTextField fieldMax = new JTextField("50");
            JButton buttonChangeAvgValue = new JButton("Ok");
            buttonChangeAvgValue.addActionListener(e -> {
                TradeConst.MIN_AVG = Integer.parseInt(fieldMin.getText());
                TradeConst.MAX_AVG = Integer.parseInt(fieldMax.getText());
            });

            JPanel panel = new JPanel();
            frame.setContentPane(panel);
            panel.setLayout(new BorderLayout());
            panel.add(new JScrollPane(list), BorderLayout.WEST);
            chartPanel = createChart();
            panel.add(chartPanel, BorderLayout.CENTER);

            JPanel panel2 = new JPanel(new FlowLayout());
//            panel2.add(buttonSaveScreen, BorderLayout.WEST);
//            panel2.add(buttonOpen, BorderLayout.WEST);
//            panel2.add(buttonClose, BorderLayout.WEST);
            panel2.add(fieldMin);
            panel2.add(fieldMax);
            panel2.add(buttonChangeAvgValue);
//            panel2.add(fieldSum, BorderLayout.EAST);
//            panel2.add(buttonSetSum, BorderLayout.EAST);

            panel.add(panel2, BorderLayout.SOUTH);

            frame.pack();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private ChartPanel createChart() {

        //инициализация трех линий для графика
        linePrice = new XYSeries(Config.getCurrentInstrument());
        lineAvgMin = new XYSeries(TradeConst.MIN_AVG);
        lineAvgMax = new XYSeries(TradeConst.MAX_AVG);

        //создание датасета и добавление линий в него
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(linePrice);
        dataset.addSeries(lineAvgMin);
        dataset.addSeries(lineAvgMax);

        //создание графика
        chart = ChartFactory.createXYLineChart(
                "Graphics " + Config.getCurrentInstrument(),
                "time",
                "price",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        yAxis = new NumberAxis();
//        yAxis.setTickUnit(new NumberTickUnit(0.001));

        plot = chart.getXYPlot();
        plot.getRendererForDataset(dataset).setSeriesPaint(2, Color.GREEN);

        new Timer(Config.SLEEP * 1000, e -> drawChart()).start();

        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 240);
            }
        };
    }

    public void drawChart() {
        System.out.println(Utilits.getTime("HH:mm:ss") + " creating chart");
        System.out.println("--------------------------------");

        Instrument instrument = Main.pricesBase.getInstrumentData(60, Config.currentInstrument);

        linePrice.clear();
        lineAvgMin.clear();
        lineAvgMax.clear();

        linePrice.setKey(Config.getCurrentInstrument());
        chart.setTitle("Graphics " + Config.getCurrentInstrument());

        //заполнение линии текущей цены
        for (int i = 0; i < 60; i++) {
            linePrice.add(i + 1, instrument.getPricesList().get(i));

            plot.setRangeAxis(yAxis);

            //вычисление небольшого отступа от верха и от низа графика
            double rangeOut = (instrument.getMax() - instrument.getMin()) * 0.1;
            plot.getRangeAxis().setRange(instrument.getMin() - rangeOut, instrument.getMax() + rangeOut);
        }

        //загрузка данных 120 последних записей из базы, для построения линий средней цены
        instrument = Main.pricesBase.getInstrumentData(120, Config.currentInstrument);

        //заполнение списка значений для линий
        List<Double> listMin = instrument.getAvgListByPriceCount(TradeConst.MIN_AVG, 60);
        List<Double> listMax = instrument.getAvgListByPriceCount(TradeConst.MAX_AVG, 60);

        //заполнение 1ой и 2ой линии средней цены
        for (int i = 0; i < 60; i++) {
            double d = listMin.get(i);
            lineAvgMin.add(i + 1, d);

            d = listMax.get(i);
            lineAvgMax.add(i + 1, d);
        }
    }

    //отрисовка графика и сохранение графика в файл изображения
    public void drawAndMakeScreen(String instrumentName) {
        Config.setCurrentInstrument(instrumentName);
        drawChart();
        saveChartToJpeg();
    }

    public void saveChartToJpeg() {
        File file = new File("chart.jpg");
        try {
            ChartUtils.saveChartAsJPEG(file, chart, chartPanel.getWidth(), chartPanel.getHeight());
        } catch (IOException | ConcurrentModificationException e) {
            System.out.println("error 180 | TradingChart | saveChartToJpeg");
            e.printStackTrace();
        }
    }
}
