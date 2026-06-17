import core.Network;
import core.NodeType;
import core.algorithms.*;
import core.models.FlowCostProb;
import core.utils.NetworkReader;
import core.utils.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowAlgorithmsTest {
    private FlowAlgorithms flowAlgorithms;
    private NetworkReader networkReader;
    private final String S = NodeType.SOURCE.getPrefix();
    private final String T = NodeType.SINK.getPrefix();

    @BeforeEach
    void setUp(){
        BellmanFord bellmanFord = new BellmanFord();
        Dijkstra dijkstra = new Dijkstra();
        BFS bfs = new BFS();
        PathFinder pathFinder = new PathFinder(bellmanFord, dijkstra, bfs);
        flowAlgorithms = new FlowAlgorithms(pathFinder);
        Random random = new Random(10);
        networkReader = new NetworkReader.Builder(new RandomNumberGenerator(random)).build();
    }

    @Test
    public void maxFlowCorrect1() {
        Network network = networkReader.getNetwork("1s4n1t-f6.csv");
        assertEquals(6, flowAlgorithms.maxFlow(network, S,T).flow);
    }
    
    @Test
    public void maxFlowCorrect2() {
        Network network = networkReader.getNetwork("1s4n1t-f19.csv");
        assertEquals(19, flowAlgorithms.maxFlow(network, S,T).flow);
    }

    @Test
    public void maxFlowCorrect3() {
        Network network = networkReader.getNetwork("1s4n1t-f17.csv");
        assertEquals(17, flowAlgorithms.maxFlow(network, S,T).flow);
    }

    @Test
    public void maxFlowCorrect4() {
        Network network = networkReader.getNetwork("1s3n1t-cf.csv");
        assertEquals(70, flowAlgorithms.maxFlow(network, S,T).flow);
    }

    @Test
    public void maxFlowCorrect5() {
        Network network = networkReader.getNetwork("1s4n1t-cf-1.csv");
        assertEquals(17, flowAlgorithms.maxFlow(network, S,T).flow);
    }

    @Test
    public void maxFlowCorrectRevEdge1() {
        Network network = networkReader.getNetwork("1s6n1t.csv");
        assertEquals(2, flowAlgorithms.maxFlow(network, S,T).flow);
    }


    @Test
    public void minCostMaxFlowCorrect1(){
        Network network = networkReader.getNetwork("1s3n1t-cf.csv");
        FlowCostProb expected = flowAlgorithms.minCostMaxFlow(network,S, T);
        assertEquals(70, expected.flow);
        assertEquals(136.0, expected.cost);
    }

    @Test
    public void minCostMaxFlowCorrect2(){
        Network network = networkReader.getNetwork("1s4n1t-cf-1.csv");
        FlowCostProb expected = flowAlgorithms.minCostMaxFlow(network,S, T);
        assertEquals(17, expected.flow);
        assertEquals(101.0, expected.cost);
    }

    @Test
    public void minCostMaxFlowCorrect3(){
        Network network = networkReader.getNetwork("1s4n1t-cf-2.csv");
        FlowCostProb expected = flowAlgorithms.minCostMaxFlow(network,S, T);

        assertEquals(17, expected.flow);
        assertEquals(95.0, expected.cost);
    }

    // https://www.desmos.com/scientific?lang=en
    @Test
    public void maxProbMaxFlowCorrect(){
        Network network = networkReader.getNetwork("1s3n1t-full.csv");
        FlowCostProb expected = flowAlgorithms.maxProbMaxFlow(network,S, T);

        assertEquals(150, expected.flow);
        assertEquals(170, expected.cost);
        assertTrue(Math.abs(-256.0339378- expected.probability)< 1e-7);
    }
}
