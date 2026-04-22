package core;

public class Packet {
    String senderId;
    String targetId;
    String payload;


    public Packet(String senderId,String targetId, String payload){
        this.senderId = senderId;
        this.targetId = targetId;
        this.payload = payload;
    }


}
