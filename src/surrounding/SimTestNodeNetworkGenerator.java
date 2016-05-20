package surrounding;

import java.util.HashMap;
import java.util.Map;

import nodebasis.Node;
import coordination.Position;

/** 
 * Implementation of the NodeNetworkGenerator interface. For those who
 * feel a tad bit extra lazy.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class SimTestNodeNetworkGenerator implements NodeNetworkGenerator{

	public static final int request_life = 45;
	public static final int agent_life = 50;
	public static final int node_signal_strength = 15;
	public static final int node_distance = 10;
	public static final int node_count_x = 50;
	public static final int node_count_y = 50;
	
	private Field field;
	private Map<Position, Node> map;
	
	public SimTestNodeNetworkGenerator(Field field){
		this.field = field;
		map = new HashMap<Position, Node>();
	}
	
	@Override
	public HashMap<Position, Node> generate(){
		Node tempNode;
		Position tempPosition;
		
		if(map.isEmpty()){
			for(int y=0; y<node_count_y; y++){
				for(int x=0; x<node_count_x; x++){
					tempPosition = new Position(x*node_distance, 
							y*node_distance);
					tempNode = new Node(field, tempPosition,
							node_signal_strength,
							request_life,
							agent_life);
					map.put(tempPosition, tempNode);
				}
			}
		}
		
		return new HashMap<Position, Node>(map);
	}
}
