package main;

import surrounding.Field;
import surrounding.FieldRunner;
import surrounding.StandardNodeNetworkGenerator;

/**
 * SimTester is a test program, testing the functionality 
 * of a wireless node network which has been implemented to pass
 * messages using the rumor-routing algorithm.</br>
 * </br>
 * The test simulation is run for 10000 updates, for as short as
 * 10 seconds (1000 updates / second). Every updates has
 * a 0.01% chance per node to create an event, which in turn has
 * a 50% chance to create an agent message. Every 400 updates
 * 4 different nodes (chosen at random before starting running
 * the simulation) create request messages. An agent message
 * is instantiated with a set lifetime of 50 updates, while the
 * request message is initiated with 45 health, but can go on for
 * a lot longer.</br>
 * </br>
 * There are 50*50 nodes, spread out by 10 units from each other.
 * Each node has a signal strength of 15 units, therefore having 
 * up to 8 neighbors.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class SimTester{

	public static final int num_of_iterations = 10000;
	public static final int event_chance_range = 10000;
	public static final int event_to_agent_chance_range = 2;
	public static final int request_timestep_interval = 400;
	public static final int request_node_count = 4;
	
	public static final int request_life = 45;
	public static final int agent_life = 50;
	public static final int node_signal_strength = 15;
	public static final int node_distance = 10;
	public static final int node_count_x = 50;
	public static final int node_count_y = 50;
	
	/**
	 * <b><i>main</i></b>
	 * <pre>public static void main(String[] args)</pre>
	 * <p>
	 * Main body.
	 * </p>
	 * @param args not used.
	 */
	public static void main(String[] args){
		Field field = new Field(num_of_iterations, event_chance_range,
				event_to_agent_chance_range, request_timestep_interval,
				request_node_count);
		field.loadNodeNetwork(new StandardNodeNetworkGenerator(node_count_x,
				node_count_y, node_distance, node_signal_strength,
				request_life, agent_life, field).generate());
		new Thread(new FieldRunner(field)).start();
	}
}
