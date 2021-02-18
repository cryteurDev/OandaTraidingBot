package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utilits {

    public static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getTime(String format) {
        return (new SimpleDateFormat(format)).format(new Date());
    }

    public static int getRandom(int max) {
        int min = 0;
        return new Random().nextInt((max - min)) + min;
    }
}
