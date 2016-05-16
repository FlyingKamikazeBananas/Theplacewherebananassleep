package main;

import surrounding.Field;
import surrounding.FieldRunner;
import surrounding.NodeNetworkGenerator;
import surrounding.SimTestNodeNetworkGenerator;

public class SimTester{

	public static final int num_of_iterations = 10000;
	public static final float event_chance = 0.0001f;
	public static final float event_to_agent_chance = 0.5f;
	public static final int request_timestep_interval = 400;
	public static final int request_node_count = 4;
	
	public static final int request_life = 45;
	public static final int agent_life = 50;
	public static final int node_signal_strength = 15;
	public static final int node_distance = 10;
	public static final int node_count_x = 50;
	public static final int node_count_y = 50;
	
	public static void main(String[] args){
		Field field = new Field(num_of_iterations, (int)(1/event_chance),
				(int)(1/event_to_agent_chance), request_timestep_interval,
				request_node_count);
		field.loadNodeNetwork(new SimTestNodeNetworkGenerator(field).generate());
		new FieldRunner(field).start();
	}
}
