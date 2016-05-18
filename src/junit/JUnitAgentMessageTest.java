import coordination.Position;
import nodebasis.AgentMessage;

import nodebasis.Node;
import org.junit.Assert;
import org.junit.Test;
import surrounding.Field;

/**
 * Created by Nils on 2016-05-18.
 */
public class JUnitAgentMessageTest {

    @Test(expected = IllegalArgumentException.class)
    public void testMessageLifeIsNotWithinThePermittedSpanOfIntegers() {
        Field testField1 = new Field(10,1,1,1,1);

        Node testNode1 = new Node(testField1,
                         new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage = new AgentMessage(testNode1, 0);
    }

    @Test
    public void twoAgentMessagesEqual() {

        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);
        AgentMessage testAgentMessage2 = new AgentMessage(testNode1, 10);

        Assert.assertTrue(testAgentMessage1.equals(testAgentMessage2));

    }

    @Test
    public void agentMessageIsNotNull() {
        Object testObject = null;
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);

        Assert.assertFalse(testAgentMessage1.equals(testObject));



    }

    @Test
    public void agentMessageIsNotSameObject() {
        Object testObject = new Object();
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);

        Assert.assertFalse(testAgentMessage1.equals(testObject));

    }
    @Test
    public void twoAgentMessagesDoesNotHaveSameLifeSpan() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);
        AgentMessage testAgentMessage2 = new AgentMessage(testNode1, 12);

        Assert.assertFalse(testAgentMessage1.equals(testAgentMessage2));
    }


    @Test
    public void agentMessageHashCodeIsEqual() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);
        AgentMessage testAgentMessage2 = new AgentMessage(testNode1, 10);

        Assert.assertTrue(testAgentMessage1.hashCode() == testAgentMessage2.hashCode());
    }

    @Test
    public void agentMessageHashCodeIsNotEqual() {
        Field testField1 = new Field(10,1,1,1,1);
        Node testNode1 = new Node(testField1,
                new Position(1,2), 1,1,1);
        AgentMessage testAgentMessage1 = new AgentMessage(testNode1, 10);
        AgentMessage testAgentMessage2 = new AgentMessage(testNode1, 11);

        Assert.assertFalse(testAgentMessage1.hashCode() == testAgentMessage2.hashCode());
    }





}
