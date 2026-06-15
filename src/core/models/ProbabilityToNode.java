package core.models;

public class ProbabilityToNode implements Comparable<ProbabilityToNode>{
        public String nodeId;
        public double probability;


        public ProbabilityToNode(String nodeId, double totalProbability){
            this.nodeId = nodeId;
            this.probability = totalProbability;
        }

        // Comparison method for priority queue
        public int compareTo(ProbabilityToNode other) {
            // Prioritize higher values, so use opposite sign by adding -
            return -Double.compare(this.probability, other.probability);
        }

}
