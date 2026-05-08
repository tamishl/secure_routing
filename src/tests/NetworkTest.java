//import core.Network;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class NetworkTest {
//
//    @Test
//    public void isPathTrue() {
//        Network network = new Network();
//        network.addNodeIfAbsent("A");
//        network.addNodeIfAbsent("B");
//        network.addNodeIfAbsent("C");
//        network.addNodeIfAbsent("D");
//        network.addNodeIfAbsent("E");
//        network.addNodeIfAbsent("F");
//        network.addNodeIfAbsent("G");
//        network.addNodeIfAbsent("H");
//        network.addNodeIfAbsent("I");
//        network.addNodeIfAbsent("J");
//
//        network.insertEdge("A", "D", 50, 0.5);
//        network.insertEdge("A", "B", 40, 0.4);
//
//        network.insertEdge("B", "D", 60, 0.6);
//        network.insertEdge("B", "E", 70, 0.7);
//
//        network.insertEdge("C", "E", 80, 0.8);
//        network.insertEdge("C", "F", 30, 0.3);
//
//        network.insertEdge("D", "H", 90, 1.0);
//
//        network.insertEdge("E", "H", 40, 0.9);
//        network.insertEdge("E", "F", 60, 0.6);
//
//        network.insertEdge("F", "E", 20, 0.1);
//        network.insertEdge("F", "H", 70, 1.2);
//        network.insertEdge("F", "G", 50, 0.5);
//
//        network.insertEdge("G", "I", 80, 0.8);
//
//        network.insertEdge("H", "I", 60, 1.1);
//        network.insertEdge("H", "J", 90, 1.3);
//
//        network.insertEdge("I", "H", 40, 0.6);
//        network.insertEdge("I", "J", 70, 0.7);
//
//        assertTrue(network.isPath("A", "I"));
//    }
//
//    @Test
//    public void isPathFalse() {
//        Network network = new Network();
//        network.addNodeIfAbsent("A");
//        network.addNodeIfAbsent("B");
//        network.addNodeIfAbsent("C");
//        network.addNodeIfAbsent("D");
//        network.addNodeIfAbsent("E");
//        network.addNodeIfAbsent("F");
//        network.addNodeIfAbsent("G");
//        network.addNodeIfAbsent("H");
//        network.addNodeIfAbsent("I");
//        network.addNodeIfAbsent("J");
//
//        network.insertEdge("A", "D", 50, 0.5);
//        network.insertEdge("A", "B", 40, 0.4);
//
//        network.insertEdge("B", "D", 60, 0.6);
//        network.insertEdge("B", "E", 70, 0.7);
//
//        network.insertEdge("C", "E", 80, 0.8);
//        network.insertEdge("C", "F", 30, 0.3);
//
//        network.insertEdge("D", "H", 90, 1.0);
//
//        network.insertEdge("E", "H", 40, 0.9);
//        network.insertEdge("E", "F", 60, 0.6);
//
//        network.insertEdge("F", "E", 20, 0.1);
//        network.insertEdge("F", "H", 70, 1.2);
//        network.insertEdge("F", "G", 50, 0.5);
//
//        network.insertEdge("G", "I", 80, 0.8);
//
//        network.insertEdge("H", "I", 60, 1.1);
//        network.insertEdge("H", "J", 90, 1.3);
//
//        network.insertEdge("I", "H", 40, 0.6);
//        network.insertEdge("I", "J", 70, 0.7);
//
//        assertFalse(network.isPath("D", "E"));
//    }
//
//
//    @Test
//    public void minCostCorrectMap() {
//        Network network = new Network();
//        network.addNodeIfAbsent("S1");
//        network.addNodeIfAbsent("A");
//        network.addNodeIfAbsent("B");
//        network.addNodeIfAbsent("C");
//        network.addNodeIfAbsent("D");
//
//        network.insertEdge("S1", "A", 100, 0.4);
//        network.insertEdge("S1", "B", 50, 0.2);
//        network.insertEdge("A", "C", 70, 0.3);
//        network.insertEdge("B", "C", 60, 0.1);
//        network.insertEdge("C", "D", 80, 0.5);
//        network.insertEdge("A", "D", 200, 0.9);
//
//        Map<String, String> expected = new HashMap<>();
//        expected.put("A", "S1");
//        expected.put("B", "S1");
//        expected.put("C", "B");
//        expected.put("D", "C");
//
//        assertEquals(expected, network.minCosts("S1"));
//    }
//
//    @Test
//    public void minCostPathCorrectPath() {
//        Network network = new Network();
//        network.addNodeIfAbsent("S1");
//        network.addNodeIfAbsent("A");
//        network.addNodeIfAbsent("B");
//        network.addNodeIfAbsent("C");
//        network.addNodeIfAbsent("D");
//
//        network.insertEdge("S1", "A", 100, 0.4);
//        network.insertEdge("S1", "B", 50, 0.2);
//        network.insertEdge("A", "C", 70, 0.3);
//        network.insertEdge("B", "C", 60, 0.1);
//        network.insertEdge("C", "D", 80, 0.5);
//        network.insertEdge("A", "D", 200, 0.9);
//
//        List<String> expected = new ArrayList<>();
//        Collections.addAll(expected, "S1", "B", "C", "D");
//
//        assertEquals(expected, network.minCostPath("S1","D"));
//
//    }
//
//    @Test
//    public void maxFlowCorrect() {
//        Network network = new Network();
//        network.addNodeIfAbsent("S1");
//        network.addNodeIfAbsent("A");
//        network.addNodeIfAbsent("B");
//        network.addNodeIfAbsent("C");
//        network.addNodeIfAbsent("D");
//
//        network.insertEdge("S1", "A", 100, 0.4);
//        network.insertEdge("S1", "B", 50, 0.2);
//        network.insertEdge("A", "C", 70, 0.3);
//        network.insertEdge("B", "C", 60, 0.1);
//        network.insertEdge("C", "D", 80, 0.5);
//        network.insertEdge("A", "D", 200, 0.9);
//
//        List<String> expected = new ArrayList<>();
//
//        assertEquals(50, network.maxFlow("S1","D"));
//
//    }
//}
