import core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testEqualsTrue() {
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v2, 10);
        assertEquals(e1, e2);
    }

    @Test
    void testEqualsFalse() {
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Host v3 = new Host("V3");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v3, 10);
        assertNotEquals(e1, e2);
    }

    @Test
    void testEqualsSymmetric(){
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v2, 10);
        assertEquals(e1.equals(e2), e2.equals(e1));
    }

    @Test
    void testEqualsReflexive(){
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        assertTrue(e1.equals(e1));

    }

    @Test
    void testEqualsTransitive(){
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v2, 10);
        Edge e3 = new Edge(v1, v2, 10);
        assertTrue(e1.equals(e2) == e2.equals(e3) == e3.equals(e1));
    }


    @Test
    void testHashCodeTrue() {
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v2, 10);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testHashCodeFalse() {
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Host v3 = new Host("V3");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v3, 10);
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testHashCodeEqualsTrue(){
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v2, 10);
        assertTrue((e1.hashCode() == e2.hashCode()) == e1.equals(e2) == e2.equals(e1));
    }

    @Test
    void testHashCodeEqualsFalse(){
        Host v1 = new Host("V1");
        Host v2 = new Host("V2");
        Host v3 = new Host("V3");
        Edge e1 = new Edge(v1, v2, 10);
        Edge e2 = new Edge(v1, v3, 10);
        assertFalse((e1.hashCode() == e2.hashCode()) == e1.equals(e2) == e2.equals(e1));
    }
}