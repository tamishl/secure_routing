package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NetworkGenerator {
    public Builder builder;

    public RandomNumberGenerator rng;

    public int minFlow;
    public int maxFlow;

    public int costDecimals;
    public double minCost;
    public double maxCost;

    public int probabilityDecimals;
    public double minProbability;
    public double maxProbability;

    public NetworkGenerator(Builder builder){
        this.rng = builder.rng;
        this.minFlow = builder.minFlow;
        this.maxFlow = builder.maxFlow;
        this.costDecimals = builder.costDecimals;
        this.minCost = builder.minCost;
        this.maxCost = builder.maxCost;
        this.probabilityDecimals = builder.probabilityDecimals;
        this.minProbability = builder.minProbability;
        this.maxProbability = builder.maxProbability;
    }


    public Network getNetwork(String networkFile) {
        Network network = new Network();
        try (BufferedReader reader = new BufferedReader(new FileReader("src//networks/" + networkFile))) {
            String line;
            String from;
            String to;
            while ((line = reader.readLine()) != null) {
                String[] nodes = line.split(",");
                from = nodes[0];
                to = nodes[1];

                // Add nodes and edges to network
                network.addNodeIfAbsent(from, rng.randDouble(minProbability, maxProbability, probabilityDecimals));
                network.addNodeIfAbsent(to, rng.randDouble(minProbability, maxProbability, probabilityDecimals));
                // Include max values
                network.insertEdge(from, to, rng.randInt(minFlow,  maxFlow+1), rng.randDouble(minCost, maxCost+0.1, costDecimals));
            }
        }

        catch (IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }

        return network;
    }

    // Default values for the fields
    //    https://stackoverflow.com/questions/5007355/builder-pattern-in-effective-java
    public static class Builder {
        private RandomNumberGenerator rng;

        private int minFlow = 1;
        private int maxFlow = 100;

        private int costDecimals = 1;
        private double minCost = 0.1;
        private double maxCost = 3.0;

        private int probabilityDecimals = 2;
        private double minProbability = 0.01;
        private double maxProbability = 0.99;


        public Builder(RandomNumberGenerator rng){
            this.rng = rng;
        }

        public Builder minFlow(int value) {
            this.minFlow = value;
            return this;
        }

        public Builder maxFlow(int value){
            this.maxFlow = value;
            return this;
        }


        public Builder costDecimals(int value) {
            this.costDecimals = value;
            return this;
        }

        public Builder minCost(double value) {
            this.minCost = value;
            return this;
        }

        public Builder maxCost(double value){
            this.maxCost = value;
            return this;
        }


        public Builder probabilityDecimals(int value) {
            this.probabilityDecimals = value;
            return this;
        }

        public Builder minProbability(double value) {
            this.minProbability = value;
            return this;
        }

        public Builder maxProbability(double value){
            this.maxProbability = value;
            return this;
        }

        public NetworkGenerator build(){
            return new NetworkGenerator(this);
        }

    }
}

