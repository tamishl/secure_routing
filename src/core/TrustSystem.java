package core;

import java.util.*;

public class TrustSystem {
    Map<String, Double> trustScores = new HashMap<>();
    Map<String,List<MaliciousActivity>> malActs = new HashMap<>();

    final double initScore;

    public TrustSystem(double initScore) {
        this.initScore = initScore;
    }

    // Recalculate after each malicious activity to allow more complex logic
    public void handleMalAct(String id, MaliciousActivity malAct){
        // Get list of malActs or create on if it doesn't exist yet (k for key)
        List<MaliciousActivity> mActs = malActs.computeIfAbsent(id, k -> new ArrayList<>());
        mActs.add(malAct);

       double penalty = 0;

       // The higher the threatLevel (lower nr), the higher the penalty
       for (MaliciousActivity ma : mActs){
           penalty += initScore/ma.getThreatLevel();
        }

       updateTrustScore(id, initScore-penalty);
    }

    private void updateTrustScore(String id, double newScore) {
        trustScores.put(id, newScore>= 0 ? newScore : 0);
    }

    public void addNode(String id) {
        trustScores.put(id, initScore);
    }
}
