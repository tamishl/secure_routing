import core.Network;
import core.RandomNumberGenerator;
import core.utils.NetworkReader;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkTest {

    @Test
    public void isPathTrue() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new core.utils.RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s8n1d-edges.csv");

        assertTrue(network.isPath("S", "D"));
    }


    @Test
    public void isPathFalse() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new core.utils.RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s8n1d-edges.csv");

        assertFalse(network.isPath("A", "E"));
    }


    @Test
    public void minCostCorrectMap() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new core.utils.RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        Map<String, String> expected = new HashMap<>();
        expected.put("A", "S1");
        expected.put("B", "S1");
        expected.put("C", "B");
        expected.put("D", "C");

        assertEquals(expected, network.minCosts("S1"));
    }

    @Test
    public void minCostPathCorrectPath() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new core.utils.RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, "S1", "B", "C", "D");

        assertEquals(expected, network.minCostPath("S1","D"));
    }

    @Test
    public void maxFlowCorrect() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new core.utils.RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        assertEquals(50, network.maxFlow("S1","D"));
    }
}


// backup network setups:
//private static Network getNetwork() {
//    Network network = new Network();
//    RandomNumberGenerator rng = new RandomNumberGenerator(new Random());
//    network.addNodeIfAbsent("S", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("B", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("C", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("A", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("E", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("F", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("G", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("H", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("I", rng.randDouble(0.01,0.99));
//    network.addNodeIfAbsent("D", rng.randDouble(0.01,0.99));
//
//    network.insertEdge("S", "A", 50, 0.5);
//    network.insertEdge("S", "B", 40, 0.4);
//
//    network.insertEdge("B", "A", 60, 0.6);
//    network.insertEdge("B", "E", 70, 0.7);
//
//    network.insertEdge("C", "E", 80, 0.8);
//    network.insertEdge("C", "F", 30, 0.3);
//
//    network.insertEdge("A", "H", 90, 1.0);
//
//    network.insertEdge("E", "H", 40, 0.9);
//    network.insertEdge("E", "F", 60, 0.6);
//
//    network.insertEdge("F", "E", 20, 0.1);
//    network.insertEdge("F", "H", 70, 1.2);
//    network.insertEdge("F", "G", 50, 0.5);
//
//    network.insertEdge("G", "I", 80, 0.8);
//
//    network.insertEdge("H", "I", 60, 1.1);
//    network.insertEdge("H", "D", 90, 1.3);
//
//    network.insertEdge("I", "H", 40, 0.6);
//    network.insertEdge("I", "D", 70, 0.7);
//    return network;
//}

//
//        network.addNodeIfAbsent("S1",0.5);
//        network.addNodeIfAbsent("A", 0.5);
//        network.addNodeIfAbsent("B" ,0.5);
//        network.addNodeIfAbsent("C", 0.5);
//        network.addNodeIfAbsent("D", 0.5);
//
//        network.insertEdge("S1", "A", 100, 0.4);
//        network.insertEdge("S1", "B", 50, 0.2);
//        network.insertEdge("A", "C", 70, 0.3);
//        network.insertEdge("B", "C", 60, 0.1);
//        network.insertEdge("C", "D", 80, 0.5);
//        network.insertEdge("A", "D", 200, 0.9);