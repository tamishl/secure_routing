package core;

// Threat: the lower the number, the more urgent

public enum MaliciousActivity {
    MODIFIED_PACKET(1),
    FAKE_PACKET(2),
    DROPPED_PACKET(3);


    private final int threatLevel;

    private MaliciousActivity(int threatLevel) {
        this.threatLevel = threatLevel;
    }

    public int getThreatLevel() {
        return threatLevel;
    }


}
