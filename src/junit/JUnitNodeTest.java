package junit;
import coordination.Position;
import nodebasis.Event;
import nodebasis.Node;
import org.junit.Assert;
import org.junit.Test;
import surrounding.Field;

/**
 * Created by Nils on 2016-05-18.
 */
public class JUnitNodeTest {

    @Test
    public void nodeIsNotNull() {
        Node testNode1 = new Node(new Field(1,1,1,1,1), new Position(1,2), 1,1,1);
        Assert.assertNotNull(testNode1);
    }



    @Test
    public void nodeDifferentSignalStrengthsShouldNotEqual() {
        Field testField1 = new Field(1,1,1,1,1);
        Field testField2 = new Field(1,1,1,1,1);

        Node testNode1 = new Node(testField1,
                new Position    (1,2), 50,1,1);
        Node testNode2 = new Node(testField2,
                new Position    (1,2), 1,1,1);

        Assert.assertFalse(testNode1.equals(testNode2));

    }

    @Test
    public void nodeHashCodeExpectedEqual() {
        Field testField1 = new Field(1,1,1,1,1);
        Field testField2 = new Field(1,1,1,1,1);

        Node testNode1 = new Node(testField1,
                         new Position    (1,2), 1,1,1);
        Node testNode2 = new Node(testField2,
                         new Position    (1,2), 1,1,1);

        Assert.assertTrue(testNode1.hashCode() == testNode2.hashCode());
    }

    @Test
    public void nodeDifferentObjectShouldNotBeEqual() {
        Node testNode1 = new Node(new Field(1,1,1,1,1), new Position(1,2), 1,1,1);
        Object testObject = new Object();

        Assert.assertFalse(testNode1.equals(testObject));
    }

    @Test
    public void nodePositionEqualsNotNull() {
        Field testField1 = new Field(1,1,1,1,1);
        Field testField2 = new Field(1,1,1,1,1);

        Position testPosition = null;
        Node testNode1 = new Node(testField1,
                         new Position    (1,2), 1,1,1);
        Node testNode2 = new Node(testField2,
                         testPosition, 1,1,1);

        Assert.assertFalse(testNode2.equals(testNode1) && testNode1.equals(testNode2));
    }

    @Test
    public void nodePositionIsNotEqual()    {
        Field testField1 = new Field(1,1,1,1,1);
        Field testField2 = new Field(1,1,1,1,1);

        Node testNode1 = new Node(testField1, new Position(1,2), 1,1,1);
        Node testNode2 = new Node(testField2, new Position(2,1), 1,1,1);

        Assert.assertFalse(testNode1.equals(testNode2));

    }
    @Test(expected = NullPointerException.class)
    public void testGenerateNewTaskIfIdIsNull() {
        Node testNode = new Node(new Field(1, 1, 1, 1, 1), new Position(1, 2), 1, 1, 1);
        Integer testNodeId = null;
        testNode.generateNewTask(testNodeId);
    }
}
