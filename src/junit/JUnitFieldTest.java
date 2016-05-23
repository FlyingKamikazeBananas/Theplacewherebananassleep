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
    public void testFieldUpdateLimitIsZero() {
        Field testField = new Field(0,1,1,1,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldEventChanceRangeIsZero() {
        Field testField = new Field(1,0,1,1,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldAgentChanceRangeIsZero() {
        Field testField = new Field(1,1,0,1,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldRequestIntervalRangeIsZero() {
        Field testField = new Field(1,1,1,0,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldNumberOfRequestNodesIsLessThanZero() {
        Field testField = new Field(1,1,1,1,-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIfRequestNodesAmountExceedsMapSize()  {
        HashMap<Position, Node> testMap = new HashMap<Position, Node>();
        Field testField = new Field(1,1,1,1,100);
        testField.loadNodeNetwork(testMap);
    }
}