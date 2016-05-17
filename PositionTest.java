package tests;
import coordination.Position;
import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    @Test
    public void positionTest() throws Exception {

        //?? vet ej om detta behövs
        Position testPosition = new Position(1, 2);

    }

    @Test
    public void positionEquals() throws Exception   {
        Position x = new Position(1, 2);
        Position y = new Position(1, 2);
        Position z = new Position(2, 1);
        Assert.assertTrue(x.equals(y) && y.equals(x));
        Assert.assertFalse(x.equals(z) && z.equals(x));
    }
    @Test
    public void hashCodeEquals() throws Exception {
        Position x = new Position(1, 2);
        Position y = new Position(1, 2);
        Position z = new Position(2, 1);
        Assert.assertTrue(x.hashCode() == y.hashCode());
        Assert.assertFalse(z.hashCode() == x.hashCode());
    }
}