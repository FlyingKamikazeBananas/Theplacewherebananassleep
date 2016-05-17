package tests;
import coordination.Position;
import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    @Test
    public void differentObjectsShouldNotEqual()    {
        Position x = new Position(1, 2);
        TestObject y = new TestObject(1, 2);
        Assert.assertFalse(x.equals(y) && y.equals(x));
    }
    @Test
    public void equalPositionsShouldBeEqual() {
        Position x = new Position(1, 2);
        Position y = new Position(1, 2);
        Assert.assertTrue(x.equals(y) && y.equals(x));
    }

    @Test
    public void differentPositionsShouldNotEqual() {
        Position x = new Position(1,2);
        Position y = new Position(2,1);
        Assert.assertFalse(x.equals(y) && y.equals(x));

    }



    @Test
    public void equalHashCodesShouldBeEqual() {
        Position x = new Position(1, 2);
        Position y = new Position(1, 2);
        Assert.assertTrue(x.hashCode() == y.hashCode());
    }
    @Test
    public void differentHashCodesShouldNotEqual() {
        Position x = new Position(1,2);
        Position y = new Position(2,1);
        Assert.assertFalse(x.hashCode() == y.hashCode());

    }



}