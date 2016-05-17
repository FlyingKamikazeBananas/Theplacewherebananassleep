package tests;
import coordination.Position;
import org.junit.Assert;
import org.junit.Test;

public class PositionTest {

    /* Kommentarer:
        -- Ni behöver inte skapa en ny klass 'TestObject', räcker
        med att använda en instans till 'Object';
        
            Object object = new Object();
            Position position = new Position(x, y);
            assertEquals(false, position.equals(object));
           
            
        -- object.equals(position) ovan använder sig av Object.equals();
        equals-metoderna i Position och Object behöver inte vara lika,
        och är heller inte lika. Ett annat exempel:
        
            class AnotherObject{
                AnotherObject(){
                }
                
                @Override
                public boolean equals(Object o){
                    return true;
                }
            }
            
        Fundera på vad som händer om man då använder AnotherObject (ovan) till:
        
            Position position = new Position(x, y);
            AnotherObject another = new AnotherObject();
            assertEquals(false, another.equals(position);
            
        
        -- Använd helst andra variabelnamn än de ni använt. Dvs exempel 'position'
        istället för 'x'. Eller exempelvis 'position1', 'position2', etc. om man
        använder sig av fler instanser.
        
        
        -- Glöm inte att kolla följande fall:
            # objektet som jämförs med Position är null,
            # objektet som jämförs med Position är av samma instans.
            
            
        -- differentHashCodesShouldNotEqual() är som vi nämnde under gruppträffen
        inte sann. Två olika instanser till Position kan ha samma hashkod, då detta
        helt och hållet beror på implementationen av HashCode-metoden. Har man
        specificerat i sin kod, eller önskar att den alltid ska vara unik så är
        det en helt annan femma, däremot är detta inte fallet i vår implementation.
        
        Exempel:
        
            class Position{
                Position(){
                }
                
                @Override
                public boolean equals(Object o){
                    if(this == obj){
			            return true;
		            }
                }
                
                @Override
                public int hashCode(){
                    return 0;
                }
            }
        
    */

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
