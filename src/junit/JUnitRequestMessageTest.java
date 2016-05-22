package junit;
import coordination.Position;
import nodebasis.Message;
import nodebasis.Node;
import nodebasis.RequestMessage;
import org.junit.Assert;
import org.junit.Test;
import surrounding.Field;

/**
 * Created by Nils on 2016-05-18.
 */
public class JUnitRequestMessageTest {

    @Test
    public void twoRequestMessageIsEqual() {

        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        RequestMessage testRequestMessage1 = new RequestMessage(1, 10,1,testNode1);
        RequestMessage testRequestMessage2 = new RequestMessage(1,10,1,testNode1);

        Assert.assertTrue(testRequestMessage1.equals(testRequestMessage2));

    }

    @Test
    public void requestMessageIsNotNull() {
        Object testObject = null;
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        RequestMessage testRequestMessage = new RequestMessage(1,10,1,testNode1);

        Assert.assertFalse(testRequestMessage.equals(testObject));



    }

    @Test
    public void requestMessageIsNotSameObject() {
        Object testObject = new Object();
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        RequestMessage testRequestMessage = new RequestMessage(1,10,1,testNode1);
        Assert.assertFalse(testRequestMessage.equals(testObject));

    }
    @Test
    public void twoRequestMessageDoesNotHaveSameOrigin() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        Node testNode2 = new Node(testField1,
                new Position(2,1), 1,1,1);
        RequestMessage testRequestMessage1 = new RequestMessage(1,10,1,testNode1);
        RequestMessage testRequestMessage2 = new RequestMessage(1,10,1,testNode2);

        Assert.assertFalse(testRequestMessage1.equals(testRequestMessage2));
    }

    @Test
    public void twoRequestMessagedoesNotHaveSameDestination() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        RequestMessage testRequestMessage1 = new RequestMessage(5,10,1,testNode1);
        RequestMessage testRequestMessage2 = new RequestMessage(1,10,1,testNode1);

        Assert.assertFalse(testRequestMessage1.equals(testRequestMessage2));
    }


    @Test
    public void requestMessageHashCodeIsEqual() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        RequestMessage testRequestMessage1 = new RequestMessage(1,10,1,testNode1);
        RequestMessage testRequestMessage2 = new RequestMessage(1,10,1,testNode1);

        Assert.assertTrue(testRequestMessage1.hashCode() == testRequestMessage2.hashCode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testIfMessageLifeIsLessThanZero()   {
        Message testRequestMessage = new RequestMessage(1,-1, 1,
                new Node(new Field(1,1,1,1,1), new Position(1,2),1,1,1));
    }
}
