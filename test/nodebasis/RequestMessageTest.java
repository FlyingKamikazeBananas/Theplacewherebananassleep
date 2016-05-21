package nodebasis;

import static org.junit.Assert.*;

import java.util.HashMap;

import nodebasis.Node;

import org.junit.Test;

import surrounding.Field;
import coordination.Position;

/**
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class RequestMessageTest {

	public static final int EVENT_ID = 1;
	
	/*
	 * Checks if a request message with too few lives expires
	 * before reaching its goal.
	 * */
	@Test
	public void requestShouldNotReach(){
		Field field = new Field(5,-1,-1,1,0);
		AllTestExpirationReader eRead = new AllTestExpirationReader();
		Node node1 = new Node(field, new Position(1,1), 1, 1, 1);
		Node node2 = new Node(field, new Position(1,2), 1, 1, 1);
		Node node3 = new Node(field, new Position(1,3), 1, 1, 1);
		Node node4 = new Node(field, new Position(1,4), 1, 1, 1);
		Node node5 = new Node(field, new Position(1,5), 1, 1, 1);
		Node node6 = new Node(field, new Position(1,6), 1, 1, 4);
		HashMap<Position, Node> nodeMap = new HashMap<Position, Node>();
		String requestId = new RequestMessage(EVENT_ID, 4, 0, node6).getRequestId();
		
		node2.setExpirationReader(eRead);
		
		nodeMap.put(node1.getPosition(), node1);
		nodeMap.put(node2.getPosition(), node2);
		nodeMap.put(node3.getPosition(), node3);
		nodeMap.put(node4.getPosition(), node4);
		nodeMap.put(node5.getPosition(), node5);
		nodeMap.put(node6.getPosition(), node6);
		
		field.loadNodeNetwork(nodeMap);
		field.setSimulationIsRunning(true);
		
		node1.generateNewEvent(EVENT_ID);
		node6.generateNewTask(EVENT_ID);
		for(int i=0; i<5; i++){
			field.update();
		}
		System.out.println("\nTesting if a request message adheres to the"
				+ " contract over its lifespan, and then is discarded when"
				+ " found out to be expired by a node.\n");
		assertEquals(true, eRead.hasExpired(requestId));
		System.out.println(" ---Result: success");
	}
	
	/*
	 * Checks if a request message with just enough lives reaches
	 * its goal, and properly returns the fetched data.
	 * */
	@Test
	public void requestShouldBarelyReach(){
		Field field = new Field(10,-1,-1,1,0);
		Node node1 = new Node(field, new Position(1,1), 1, 1, 1);
		Node node2 = new Node(field, new Position(1,2), 1, 1, 1);
		Node node3 = new Node(field, new Position(1,3), 1, 1, 1);
		Node node4 = new Node(field, new Position(1,4), 1, 1, 1);
		Node node5 = new Node(field, new Position(1,5), 1, 1, 1);
		Node node6 = new Node(field, new Position(1,6), 1, 1, 5);
		AllTestExpirationReader eRead = new AllTestExpirationReader();
		HashMap<Position, Node> nodeMap = new HashMap<Position, Node>();
		String requestId = new RequestMessage(EVENT_ID, 5, 0, node6).getRequestId();
		
		nodeMap.put(node1.getPosition(), node1);
		nodeMap.put(node2.getPosition(), node2);
		nodeMap.put(node3.getPosition(), node3);
		nodeMap.put(node4.getPosition(), node4);
		nodeMap.put(node5.getPosition(), node5);
		nodeMap.put(node6.getPosition(), node6);
		
		field.loadNodeNetwork(nodeMap);
		field.setSimulationIsRunning(true);
		
		node1.generateNewEvent(EVENT_ID);
		node6.generateNewTask(EVENT_ID);
		node6.setRequestReader(eRead);
		
		System.out.println("\nTesting if a request message (with just the right amount of lives) can reach an event,"
				+ " and then return it.\n");
		
		for(int i=0; i<11; i++){
			field.update();
		}
		
		assertEquals(true, eRead.isSuccessfulRequest(requestId));
		System.out.println(" ---Result: success");
	}
	
	/*
	 * Tests if a request message which normally would expire due to too few
	 * lives, is restored to full health do to being passed to a node who 
	 * happen to know the direction to the sought event, and then returns
	 * the fetched data properly.
	 * */
	@Test
	public void requestShouldReachDueToLifeReset(){
		Field field = new Field(12,-1,-1,1,0);
		Node node1 = new Node(field, new Position(1,1), 1, 2, 1);
		Node node2 = new Node(field, new Position(1,2), 1, 1, 1);
		Node node3 = new Node(field, new Position(1,3), 1, 1, 1);
		Node node30 = new Node(field, new Position(0,3), 1, 1, 1);
		Node node32 = new Node(field, new Position(2,3), 1, 1, 1);
		Node node4 = new Node(field, new Position(1,4), 1, 1, 1);
		Node node5 = new Node(field, new Position(1,5), 1, 1, 1);
		Node node6 = new Node(field, new Position(1,6), 1, 1, 1);
		Node node7 = new Node(field, new Position(1,7), 1, 1, 5);
		AllTestExpirationReader eRead = new AllTestExpirationReader();
		HashMap<Position, Node> nodeMap = new HashMap<Position, Node>();
		String requestId = new RequestMessage(EVENT_ID, 5, 0, node7).getRequestId();
		
		nodeMap.put(node1.getPosition(), node1);
		nodeMap.put(node2.getPosition(), node2);
		nodeMap.put(node3.getPosition(), node3);
		nodeMap.put(node30.getPosition(), node30);
		nodeMap.put(node32.getPosition(), node32);
		nodeMap.put(node4.getPosition(), node4);
		nodeMap.put(node5.getPosition(), node5);
		nodeMap.put(node6.getPosition(), node6);
		nodeMap.put(node7.getPosition(), node7);
		
		field.loadNodeNetwork(nodeMap);
		field.setSimulationIsRunning(true);
		
		node1.generateNewTask(node1.generateNewEvent(EVENT_ID));
		node7.generateNewTask(EVENT_ID);
		node7.setRequestReader(eRead);
		
		System.out.println("\nTesting if a request message with initially too few lives can reach an event due"
				+ " to resetting the amount of lives\n (it reachas a node who knows the way to the event),"
				+ " and then return it.\n");
		
		for(int i=0; i<13; i++){
			field.update();
		}
		
		assertEquals(true, eRead.isSuccessfulRequest(requestId));
		System.out.println(" ---Result: success");
	}

}
