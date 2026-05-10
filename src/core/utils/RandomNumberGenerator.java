package core.utils;

import java.util.Random;

public class RandomNumberGenerator {
    Random random;


    public RandomNumberGenerator(Random random){
        this.random = random;
    }


    // Min inclusive, max exclusive
    public int randInt(int min, int max){
        return min != max
                ? random.nextInt(min, max)
                : min;
    }

    // Min inclusive, max exclusive
    public double randDouble(double min, double max, int decimals){
        return min != max
                ? roundDouble(random.nextDouble(min, max), decimals)
                : min;
        }

    // 1 decimal by default
    public double randDouble(double min, double max){
        return randDouble(min, max, 1);
    }


    private double roundDouble(double d, int decimals){
        // Double to avoid integer division
        double scale = Math.pow(10, decimals);

        return Math.round(d * scale) / scale;
    }
}
