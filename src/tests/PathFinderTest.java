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
    private NetworkReader networkReader;
    private final String S = NodeType.SOURCE.getPrefix();
    private final String T = NodeType.SINK.getPrefix();

    @BeforeEach
    void setUp(){
        BellmanFord bellmanFord = new BellmanFord();
        Dijkstra dijkstra = new Dijkstra();
        BFS bfs = new BFS();
        pathFinder = new PathFinder(bellmanFord, dijkstra, bfs);

        Random random = new Random(10);
        networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
    }

    @Test
    public void isPathTrue() {
        Network network = networkReader.getNetwork("1s4n1t-f6.csv");

        assertFalse(pathFinder.flowPath(network,S, T, network.generateResidualFlowMap()).isEmpty());
    }

    @Test
    public void isPathFalse() {
        Network network = networkReader.getNetwork("1s8n1t-edges-antiparallel.csv");

        assertTrue(pathFinder.flowPath(network,"A", "E", network.generateResidualFlowMap()).isEmpty());
    }

    @Test
    public void minCostPathDijkstraCorrect() {
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, Algorithm.DIJKSTRA));
    }

    @Test
    public void minCostPathBFCorrect() {
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostPath(network, S,T, Algorithm.BELLMAN_FORD));
    }

    @Test
    public void minCostFlowPathDJCorrect1() {
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minCostFlowPath(network, S,T, network.generateResidualMap(),  network.generatePotentials()));
    }

    @Test
    public void minCostPathDJCorrect2() {
        Network network = networkReader.getNetwork("1s4n1t-cf-2.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "D", "B", "A", S);

        assertEquals(expected, pathFinder.minCostFlowPath(network, S,T, network.generateResidualMap(), network.generatePotentials()));
    }

    @Test
    public void minRiskPathCorrect1() {
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, T, "C", "B", S);

        assertEquals(expected, pathFinder.minRiskFlowPath(network, S,T, network.generateResidualMap()));
    }
    
    @Test
    public void paretoPathsCorrectSinglePath(){
        Network network = networkReader.getNetwork("1s3n1t-full.csv");

        HashSet<List<String>> expected = new HashSet<>();
        expected.add(List.of(S, "B", "C", T));

        assertEquals(expected, pathFinder.paretoPath(network, S,T, network.generateResidualMap(), network.generatePotentials()));

    }

    @Test
    public void paretoPathsCorrectAllPaths(){
        Network network = networkReader.getNetwork("1s4n1t-full-allPareto.csv");

        HashSet<List<String>> expected = new HashSet<>();
        expected.add(List.of(S, "B", "D", T));
        expected.add(List.of(S, "A", "B", "D", T));
        expected.add(List.of(S, "A", "B", "D", "C", T));
        expected.add(List.of(S, "A", "C", T));
        expected.add(List.of(S, "A", "D", T));
        expected.add(List.of(S, "A", "D", "C", T));

        assertEquals(expected, pathFinder.paretoPath(network, S,T, network.generateResidualMap(), network.generatePotentials()));

    }

    @Test
    public void paretoPathsCorrectMultiPaths(){
        Network network = networkReader.getNetwork("1s4n1t-full.csv");

        HashSet<List<String>> expected = new HashSet<>();
        expected.add(List.of(S, "B", "D", T));
        expected.add(List.of(S, "A", "B", "D", T));
        expected.add(List.of(S, "A", "B", "D", "C", T));
        expected.add(List.of(S, "A", "C", T));

        assertEquals(expected, pathFinder.paretoPath(network, S,T, network.generateResidualMap(), network.generatePotentials()));

    }

}
