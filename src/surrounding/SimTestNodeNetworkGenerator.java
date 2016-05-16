package surrounding;

import java.util.HashMap;
import java.util.Map;

import main.SimTester;
import nodebasis.Node;
import coordination.Position;

public class SimTestNodeNetworkGenerator implements NodeNetworkGenerator{

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
			for(int y=0; y<SimTester.node_count_y; y++){
				for(int x=0; x<SimTester.node_count_x; x++){
					tempPosition = new Position(x*SimTester.node_distance, 
							y*SimTester.node_distance);
					tempNode = new Node(field, tempPosition,
							SimTester.node_signal_strength,
							SimTester.request_life,
							SimTester.agent_life);
					map.put(tempPosition, tempNode);
				}
			}
		}
		
		return new HashMap<Position, Node>(map);
	}
}
