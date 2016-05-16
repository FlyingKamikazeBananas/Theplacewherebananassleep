package surrounding;

import java.util.HashMap;
import java.util.Map;

import nodebasis.Node;
import coordination.Position;


public class StandardNodeNetworkGenerator implements NodeNetworkGenerator{

	private Map<Position, Node> map;
	private Field field;
	private final int numNodesX;
	private final int numNodesY;
	private final int nodeDistance;
	private final int nodeSignalStrength;
	private final int requestLife;
	private final int agentLife;
	
	public StandardNodeNetworkGenerator(int numNodesX, int numNodesY,
			int nodeDistance, int nodeSignalStrength,
			int requestLife, int agentLife,
			Field field){
		this.numNodesX = numNodesX;
		this.numNodesY = numNodesY;
		this.nodeDistance = nodeDistance;
		this.nodeSignalStrength = nodeSignalStrength;
		this.requestLife = requestLife;
		this.agentLife = agentLife;
		this.field = field;
		map = new HashMap<Position, Node>();
		
	}
	
	@Override
	public HashMap<Position, Node> generate(){
		Node tempNode;
		Position tempPosition;
		if(map.isEmpty()){
			for(int y=0; y<numNodesY; y++){
				for(int x=0; x<numNodesX; x++){
					tempPosition = new Position(x*nodeDistance, 
							y*nodeDistance);
					tempNode = new Node(field, tempPosition,
							nodeSignalStrength,
							requestLife,
							agentLife);
					map.put(tempPosition, tempNode);
				}
			}
		}
		
		return new HashMap<Position, Node>(map);
	}
}
