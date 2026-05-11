import core.Network;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;

import java.util.Random;

public class Main{
    public static void main(String[] args){
        RandomNumberGenerator rng = new RandomNumberGenerator(new Random());
        NetworkReader ng = new NetworkReader.Builder(rng).build();
        Network network = ng.getNetwork("1s3n1d-edges.csv");
        network.printGraph();

    }
}