package core;

import java.util.Random;

public class RandomNumberGenerator {
    Random random;


    public RandomNumberGenerator(Random random){
        this.random = random;
    }


    // Min inclusive, max exclusive
    public int randInt(int min, int max){
        return random.nextInt(min, max);
    }

    // Min inclusive, max exclusive
    public double randDouble(double min, double max, int decimals){
        double d = random.nextDouble(min, max);
        return roundDouble(d, decimals);
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
