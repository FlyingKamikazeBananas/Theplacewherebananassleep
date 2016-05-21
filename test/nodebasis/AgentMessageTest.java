package nodebasis;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import surrounding.Field;
import coordination.Position;

/**
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-20
 * */
public class AgentMessageTest {

	public static final int EVENT_ID = 1;
	
	/*
	 * Testing if an agent message expires where it is supposed to.
	 * 
	 * 11 nodes are put into node network. Each node can only reach
	 * any node within 1 units distance. A test simulation is 
	 * run for 10 iterations, where an agent message is created with 
	 * 9 health by a node at the end of the network. A destination node is
	 * selected before the start of the test (9 units away). 
	 * If the message dies while visiting that node it would seem that 
	 * the message adheres to the contract.
	 * */
	@Test
	public void agentMessageShouldExpireWhenSpecified() {
		Field field = new Field(10,-1,-1,1,0);
		Node node1 = new Node(field, new Position(1,1), 1, 9, 1);
		Node node2 = new Node(field, new Position(1,2), 1, 1, 1);
		Node node3 = new Node(field, new Position(1,3), 1, 1, 1);
		Node node4 = new Node(field, new Position(1,4), 1, 1, 1);
		Node node5 = new Node(field, new Position(1,5), 1, 1, 1);
		Node node6 = new Node(field, new Position(1,6), 1, 1, 1);
		Node node7 = new Node(field, new Position(1,7), 1, 1, 1);
		Node node8 = new Node(field, new Position(1,8), 1, 1, 1);
		Node node9 = new Node(field, new Position(1,9), 1, 1, 1);
		Node node10 = new Node(field, new Position(1,10), 1, 1, 1);
		Node node11 = new Node(field, new Position(1,11), 1, 1, 1);
		
		AllTestExpirationReader eRead = new AllTestExpirationReader();
		HashMap<Position, Node> nodeMap = new HashMap<Position, Node>();
		String agentId = new AgentMessage(node1, 1, 0).getAgentId();
		
		nodeMap.put(node1.getPosition(), node1);
		nodeMap.put(node2.getPosition(), node2);
		nodeMap.put(node3.getPosition(), node3);
		nodeMap.put(node4.getPosition(), node4);
		nodeMap.put(node5.getPosition(), node5);
		nodeMap.put(node6.getPosition(), node6);
		nodeMap.put(node7.getPosition(), node7);
		nodeMap.put(node8.getPosition(), node8);
		nodeMap.put(node9.getPosition(), node9);
		nodeMap.put(node10.getPosition(), node10);
		nodeMap.put(node11.getPosition(), node11);
		
		field.loadNodeNetwork(nodeMap);
		field.setSimulationIsRunning(true);
		
		node10.setExpirationReader(eRead);
		node1.generateNewTask(node1.generateNewEvent(EVENT_ID));
		
		for(int i=0; i<11; i++){
			field.update();
		}
		
		System.out.println("Testing if an agent message expires where it is supposed to.\n");
		assertEquals(true, eRead.hasExpired(agentId));
		System.out.println(" ---Result: success");
		
	}

}
