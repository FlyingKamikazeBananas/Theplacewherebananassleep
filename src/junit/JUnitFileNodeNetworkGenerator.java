package junit;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import nodebasis.Node;

import org.junit.Test;

import coordination.Position;
import surrounding.Field;
import surrounding.FileNodeNetworkGenerator;

public class JUnitFileNodeNetworkGenerator {

	@Test
	public void testIfSuccessfullyReadFromFile(){
		Field field = new Field(1,1,1,1,1);
		String testString = "1;2;3;4;5\n"
				+ "2;3;4;5;1\n"
				+ "3;4;5;1;2\n"
				+ "4;5;1;2;3\n"
				+ "5;1;2;3;4\n";
		Exception exc;
		try {
			exc = read("testNetwork.txt", field);
			if(exc != null){
				throw exc;
			}
			
			System.out.println("before read from file:\n");
			System.out.println(testString);
			System.out.println("returned from field:\n");
			System.out.println(field.getStringRepresentation());
			
			assertEquals(true, testString.equals(field.getStringRepresentation()));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private Exception read(String file, Field field) throws IOException{
		FileReader fileReader;
        	FileNodeNetworkGenerator nGen;
		HashMap<Position, Node> map;
        	
		fileReader = new FileReader(file);
		nGen = new FileNodeNetworkGenerator(fileReader, field);
		map = nGen.generate();
		if(map != null){
			field.loadNodeNetwork(map);
		}
		nGen.closeCurrentReader();
	    
		return nGen.getException();
	}
	
}
