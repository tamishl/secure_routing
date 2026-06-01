import core.Network;
import core.algorithms.Dijkstra;
import core.utils.RandomNumberGenerator;
import core.utils.NetworkReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class DijkstraTest {
    private Dijkstra dijkstra;

    @BeforeEach
    void setUp(){
        dijkstra = new Dijkstra();
    }

    @Test
    public void dijkstraCorrectMap() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        Map<String, String> expected = new HashMap<>();
        expected.put("A", "S");
        expected.put("B", "S");
        expected.put("C", "B");
        expected.put("T", "C");

        assertEquals(expected, dijkstra.computeDijkstra(network, "S"));
    }


    @Test
    public void dijkstraJohnsonCorrectMap1() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        Map<String, String> expected = new HashMap<>();
        expected.put("A", "S");
        expected.put("B", "S");
        expected.put("C", "B");
        expected.put("T", "C");

        assertEquals(expected, dijkstra.computeDijkstraJohnson(network, "S", "T", network.generateResidualMap(), network.generatePotentials()));

    }

    @Test
    public void dijkstraJohnsonCorrectMap2() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s4n1d-cf-2.csv");

        Map<String, String> expected = new HashMap<>();
        expected.put("A", "S");
        expected.put("B", "A");
        expected.put("C", "D");
        expected.put("D", "B");
        expected.put("T", "C");

        assertEquals(expected, dijkstra.computeDijkstraJohnson(network, "S", "T", network.generateResidualMap(), network.generatePotentials()));
    }
}
