import core.Network;
import core.algorithms.*;
import core.models.FlowCost;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowAlgorithmsTest {
    private PathFinder pathFinder;

    @BeforeEach
    void setUp(){
        BellmanFord bellmanFord = new BellmanFord();
        Dijkstra dijkstra = new Dijkstra();
        BFS bfs = new BFS();
        pathFinder = new PathFinder(bellmanFord, dijkstra, bfs);
    }


    @Test
    public void maxFlowMinPathDijkstraCorrect() {
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        assertEquals(50, flowAlgorithms.maxFlowMinPath(network,"S","T", Algorithm.DIJKSTRA));
    }

    @Test
    public void maxFlowMinPathBFCorrect() {
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-full.csv");

        assertEquals(50, flowAlgorithms.maxFlowMinPath(network,"S", "T", Algorithm.BELLMAN_FORD));
    }


    @Test
    public void maxFlowCorrect() {
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s4n1d-f19.csv");

        assertEquals(19, flowAlgorithms.maxFlow(network, "S","T"));
    }

    @Test
    public void maxFlowCorrect2() {
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s4n1d-f6.csv");

        assertEquals(6, flowAlgorithms.maxFlow(network, "S","T"));
    }

    @Test
    public void minCostFlowCorrect(){
        Random random = new Random(10);
        FlowAlgorithms flowAlgorithms = new FlowAlgorithms(pathFinder);
        NetworkReader networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
        Network network = networkReader.getNetwork("1s3n1d-cf.csv");
        FlowCost result = flowAlgorithms.minCostFlow(network,"S", "T");

        assertEquals(70, result.flow);
        assertEquals(136, result.cost);
    }
}
