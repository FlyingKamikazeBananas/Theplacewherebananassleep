package junit;
import coordination.Position;
import org.junit.Assert;
import org.junit.Test;

public class JUnitPositionTest {

    @Test
    public void positionIsNotNull() {
        Position testPosition = new Position(1,2);
        Assert.assertNotNull(testPosition);
    }


    @Test
    public void differentObjectsShouldNotBeEqual()    {
        Position testPosition = new Position(1, 2);
        Object testObject = new Object();
        Assert.assertFalse(testObject.equals(testPosition) && testPosition.equals(testObject));
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
}