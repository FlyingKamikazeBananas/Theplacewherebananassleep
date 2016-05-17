package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import surrounding.Field;
import surrounding.FieldRunner;

import static org.junit.Assert.*;

/**
 * Created by c15nkn on 2016-05-17.
 */
public class FieldRunnerTest {


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