package junit;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import nodebasis.Node;

import org.junit.Test;

import coordination.Position;
import surrounding.Field;
import surrounding.FileNodeNetworkGenerator;
import surrounding.StandardNodeNetworkGenerator;

public class JUnitFileNodeNetworkGeneratorAndNeighbours{

	@Test
	public void testIfSuccessfullyReadFromFile(){
		Field field = new Field(1,1,1,1,1);
		String testString = "1;2;3;4;5\n"
				+ "2;3;4;5;1\n"
				+ "3;4;5;1;2\n"
				+ "4;5;1;2;3\n"
				+ "5;1;2;3;4\n";
		Exception exc;
		
		try{
			exc = read("testNetwork.txt", field);
			if(exc != null){
				throw exc;
			}
			
			System.out.println("---------------------------");
			System.out.println("Test if read file properly:");
			System.out.println("before read from file:\n");
			System.out.println(testString);
			System.out.println("returned from field:\n");
			System.out.println(field.getStringRepresentation());
			
			assertEquals(true, testString.equals(field.getStringRepresentation()));
			
			System.out.println("Result: success");
			System.out.println("");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIfNodeSuccessfullyFindsNeighboursX(){
		Field field = new Field(1,1,1,1,1);
		Node testNode = new Node(field, new Position(5,0),
				2,1,1);
		String testString[] = {"3;0;1;1;1\n",
				"4;0;1;1;1\n",
				"6;0;1;1;1\n",
				"7;0;1;1;1\n"};
		String otherString = "";
		Exception exc;
		
		try{
			exc = read("testNeighboursX.txt", field);
			if(exc != null){
				throw exc;
			}
			
			for(Node node : field.getNodesWithinRangeofNode(testNode)){
				otherString += node.getStringRepresentation() + "\n";
			}
			
			System.out.println("---------------------------");
			System.out.println("Test if node can find legit neighbours X-wise:");
			System.out.println("expected neighbours:\n");
			System.out.println(testString[0]+testString[1]
					+testString[2]+testString[3]);
			System.out.println("got neighbours:\n");
			System.out.println(otherString);
			
			for(String str : testString){
				assertEquals(true, otherString.contains(str));
			}
			
			System.out.println("Result: success");
			System.out.println("");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIfNodeSuccessfullyFindsNeighboursY(){
		Field field = new Field(1,1,1,1,1);
		Node testNode = new Node(field, new Position(0,5),
				2,1,1);
		String testString[] = {"0;3;1;1;1\n",
				"0;4;1;1;1\n",
				"0;6;1;1;1\n",
				"0;7;1;1;1\n"};
		String otherString = "";
		Exception exc;
		
		try{
			exc = read("testNeighboursY.txt", field);
			if(exc != null){
				throw exc;
			}
			
			for(Node node : field.getNodesWithinRangeofNode(testNode)){
				otherString += node.getStringRepresentation() + "\n";
			}
			
			System.out.println("---------------------------");
			System.out.println("Test if node can find legit neighbours Y-wise:");
			System.out.println("expected neighbours:\n");
			System.out.println(testString[0]+testString[1]
					+testString[2]+testString[3]);
			System.out.println("got neighbours:\n");
			System.out.println(otherString);
			
			for(String str : testString){
				assertEquals(true, otherString.contains(str));
			}
			
			System.out.println("Result: success");
			System.out.println("");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIfNodeSuccessfullyFindsNeighboursXY(){
		Field field = new Field(1,1,1,1,1);
		field.loadNodeNetwork(new StandardNodeNetworkGenerator(10, 10,
				1, 1, 1, 1, field).generate());
		Node testNode = new Node(field, new Position(5,5),
				2,1,1);
		ArrayList<String> strList = new ArrayList<String>();
		String testString = "5;3;1;1;1\n"
				+"5;4;1;1;1\n"
				+"5;6;1;1;1\n"
				+"5;7;1;1;1\n"
				+"3;5;1;1;1\n"
				+"4;5;1;1;1\n"
				+"6;5;1;1;1\n"
				+"7;5;1;1;1\n"
				+"4;4;1;1;1\n"
				+"6;4;1;1;1\n"
				+"4;6;1;1;1\n"
				+"6;6;1;1;1\n";
		
		for(Node node : field.getNodesWithinRangeofNode(testNode)){
			strList.add(node.getStringRepresentation());
		}
		
		System.out.println("---------------------------");
		System.out.println("Test if node can find (only) legit neighbours X- & Y-wise:");
		System.out.println("expected neighbours:\n");
		System.out.println(testString);
		System.out.println("got neighbours:\n");
		for(String str : strList){
			System.out.println(str);
		}
		
		for(String str : strList){
			assertEquals(true, testString.contains(str));
		}
		
		System.out.println("");
		System.out.println("Result: success");
		System.out.println("");
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
