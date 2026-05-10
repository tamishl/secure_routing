package core.utils;

import core.Network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NetworkReader {
    public RandomNumberGenerator randNumGenerator;

    public int minFlow;
    public int maxFlow;

    public int costDecimals;
    public double minCost;
    public double maxCost;

    public int probabilityDecimals;
    public double minProbability;
    public double maxProbability;

    public NetworkReader(Builder builder){
        this.randNumGenerator = builder.randNumGenerator;
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
        Section section = Section.NONE;
        List<String> headers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src//networks/" + networkFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Continue after an empty line (space between sections)
                if (line.isEmpty()){
                    continue;
                }

                // Select section
                switch (line) {
                    case "[NODES]" -> {
                        section = Section.NODES;
                        headers = List.of();
                        continue;
                    }

                    case "[EDGES]" -> {
                        section = Section.EDGES;
                        headers = List.of();
                        continue;
                    }
                }


                // First line after section or first line of file without section
                if (headers.isEmpty()) {
                    headers = Arrays.asList(line.split(","));
                    continue;
                }

                // Map data to headers
                Map<String, String> valueMap = mapValues(headers, Arrays.asList(line.split(",")));

                // Add data to specified part of graph
                switch (section) {
                    case NODES -> addNode(valueMap, network);
                    case EDGES -> addEdge(valueMap, network);
                    case NONE -> {
                        addNodesFromEdge(valueMap, network);
                        addEdge(valueMap, network);
                    }
                }
            }
        }

        catch (IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }

        return network;
    }

    // Map values to headers
    private Map<String, String> mapValues(List<String> headers, List<String> values) {
        Map<String, String> valueMap = new HashMap<>();

        for (int i = 0; i < headers.size(); i++) {
            String value = i < values.size()
                    ? values.get(i).trim()
                    : "";
            valueMap.put(headers.get(i), value);
        }
        return valueMap;
    }


    private void addNode(Map<String, String> valueMap, Network network){
        String id = valueMap.get(Field.ID.getValue());
        double probability;

        if (valueMap.containsKey(Field.PROBABILITY.getValue())) {
            probability = Double.parseDouble(valueMap.get("probability"));
        } else {
            probability = randNumGenerator.randDouble(minProbability, maxProbability, probabilityDecimals);
        }

        network.addNodeIfAbsent(id, probability);
    }

    private void addEdge(Map<String, String> valueMap, Network network){
        String from = valueMap.get(Field.FROM.getValue());
        String to = valueMap.get(Field.TO.getValue());
        int capacity;
        double cost;

        if (valueMap.containsKey(Field.CAPACITY.getValue())) {
            capacity = Integer.parseInt(valueMap.get(Field.CAPACITY.getValue()));
        } else {
            capacity = randNumGenerator.randInt(minFlow, maxFlow+1);
        }

        if (valueMap.containsKey(Field.COST.getValue())) {
            cost = Double.parseDouble(valueMap.get(Field.COST.getValue()));
        } else {
            cost = randNumGenerator.randDouble(minCost, maxCost+0.1, costDecimals);
        }

        network.insertEdge(from, to, capacity, cost);
    }


    private void addNodesFromEdge(Map<String, String> valueMap, Network network){
        String from = valueMap.get(Field.FROM.getValue());
        String to = valueMap.get(Field.TO.getValue());

        // Add nodes to network
        network.addNodeIfAbsent(from, randNumGenerator.randDouble(minProbability, maxProbability, probabilityDecimals));
        network.addNodeIfAbsent(to, randNumGenerator.randDouble(minProbability, maxProbability, probabilityDecimals));
    }


    // Default values for the class fields
    //    https://stackoverflow.com/questions/5007355/builder-pattern-in-effective-java
    public static class Builder {
        private RandomNumberGenerator randNumGenerator;

        private int minFlow = 1;
        private int maxFlow = 100;

        private int costDecimals = 1;
        private double minCost = 0.1;
        private double maxCost = 3.0;

        private int probabilityDecimals = 2;
        private double minProbability = 0.01;
        private double maxProbability = 0.99;


        public Builder(RandomNumberGenerator randNumGenerator){
            this.randNumGenerator = randNumGenerator;
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

        public NetworkReader build(){
            return new NetworkReader(this);
        }
    }
}