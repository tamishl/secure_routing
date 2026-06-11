import core.Network;
import core.algorithms.BellmanFord;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BellmanFordTest {
    private BellmanFord bellmanFord;

    @BeforeEach
    void setUp(){
        bellmanFord = new BellmanFord();
    }
    @Test
    public void minCostParentBFCorrectParents(){
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s2n1t-cNeg.csv");

        Map<String, String> expected = new HashMap<>();
        expected.put("T", "A");
        expected.put("A", "B");
        expected.put("B", "S");
        assertEquals(expected, bellmanFord.compute(network, "S"));
    }

    @Test
    public void minCostParentBFDetectNegativeLoop(){
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1t-cNegLoop.csv");

        assertTrue(bellmanFord.compute(network, "S").isEmpty());
    }
}
