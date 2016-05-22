package junit;

import org.junit.Test;
import surrounding.Field;
import surrounding.FieldRunner;

/**
 * Created by c15nkn on 2016-05-17.
 */
public class JUnitFieldRunnerTest {


    /*
        Se PositionTest.java för kommentarer.
    */

    @Test(expected = IllegalArgumentException.class)
    public void testFieldRunner() {

        int testUpdatesPerSecond = 0;
        Field testField = new Field(0,0,0,0,0);
        FieldRunner testFieldRunner = new FieldRunner(testField, testUpdatesPerSecond);

    }

    @Test(expected = IllegalStateException.class)
    public void testRun() {

        Field testField = new Field(0,0,0,0,0);
        FieldRunner testFieldRunner = new FieldRunner(testField);
        testFieldRunner.run();

    }
}
