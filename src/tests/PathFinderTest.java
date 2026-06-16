import core.Network;
import core.NodeType;
import core.algorithms.*;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PathFinderTest {
    private PathFinder pathFinder;
    private final String S = NodeType.SOURCE.getPrefix();
    private final String T = NodeType.SINK.getPrefix();

    @BeforeEach
    void setUp(){
        BellmanFord bellmanFord = new BellmanFord();
        Dijkstra dijkstra = new Dijkstra();
        BFS bfs = new BFS();
        pathFinder = new PathFinder(bellmanFord, dijkstra, bfs);
    }

    @Test
    public void isPathTrue() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s4n1t-f6.csv");

        assertFalse(pathFinder.flowPath(network,S, T, network.generateResidualFlowMap()).isEmpty());
    }

    @Test
    public void isPathFalse() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s8n1t-edges-antiparallel.csv");

        assertTrue(pathFinder.flowPath(network,"A", "E", network.generateResidualFlowMap()).isEmpty());
    }

    @Test
    public void minCostPathDijkstraCorrect() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, Algorithm.DIJKSTRA));
    }

    @Test
    public void minCostPathBFCorrect() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, Algorithm.BELLMAN_FORD));
    }

    @Test
    public void minCostPathDJCorrect1() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, network.generateResidualMap(),  network.generatePotentials()));
    }

    @Test
    public void minCostPathDJCorrect2() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s4n1t-cf-2.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "D", "B", "A", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, network.generateResidualMap(), network.generatePotentials()));
    }

    @Test
    public void minRiskPathCorrect1() {
        Random random = new Random(10);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minRiskFlowPath(network, S,T, network.generateResidualMap()));
    }

}
