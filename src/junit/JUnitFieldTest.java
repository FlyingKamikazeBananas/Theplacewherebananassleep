package junit;
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
    public void testIfFieldIsNotLoaded() {
        Field testField = new Field(1,1,1,1,1);
        assertFalse(testField.getHasLoadedNodeNetwork());
    }
    @Test
    public void testIfFieldIsLoaded()   {

    }

    @Test
    public void testIfCenterNodeHasCorrectNeighbours()    {

    }
    @Test(expected = IllegalArgumentException.class)
    public void testIfRequestNodesAmountExceedsMapSize()  {
        HashMap<Position, Node> testMap = new HashMap<Position, Node>();
        Field testField = new Field(1,1,1,1,100);
        testField.loadNodeNetwork(testMap);
    }
}
