import coordination.Position;
import nodebasis.Node;
import org.junit.Assert;
import org.junit.Test;
import surrounding.Field;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertFalse;

/**
 * Created by Nils on 2016-05-18.
 */
public class JUnitFieldTest {

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalFieldParams() {
       Field testField = new Field(0,0,0,0,0);
    }

    @Test
    public void testIfThereIsNoField() {
        Field testField = new Field(1,1,1,1,1);
        assertFalse(testField.getHasLoadedNodeNetwork());
    }

    @Test
    public void testIfCenterNodeHasCorrectNeighbours()    {
        Field testField = new Field(1,1,1,1,1);
        Node tempNode;
        Node centerNode;
        Position tempPosition;
        HashMap testNodeMap = new HashMap<Position, Node>();
        ArrayList<Node> nodesList1 = new ArrayList<Node>();
        ArrayList<Node> nodesList2;
        if(testNodeMap.isEmpty()){
            for(int y=0; y<3; y++){
                for(int x=0; x<3; x++){
                    tempPosition = new Position(x*10, y*10);
                    tempNode = new Node(testField, tempPosition, 15, 1, 1);
                    nodesList1.add(tempNode);
                    testNodeMap.put(tempPosition ,tempNode);
                }
            }
        }
        centerNode = nodesList1.get(5);
        nodesList1.remove(5);
        nodesList2 = testField.getNodesWithinRangeofNode(centerNode);
        for(int i=0; i<nodesList2.size(); i++)  {
            Assert.assertTrue(nodesList1.contains
                    (nodesList2.get(i)) && nodesList2.contains(nodesList1.get(i)));
        }
    }
}
