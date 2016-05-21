package surrounding;

import java.util.HashMap;
import java.util.Map;

import nodebasis.Node;
import coordination.Position;

/**
 * The <code>StandardNodeNetworkGenerator</code> generates a grid 
 * of nodes (network), based on specified number of nodes, their distance
 * to one and another, etc.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class StandardNodeNetworkGenerator implements NodeNetworkGenerator{

	private Map<Position, Node> map;
	private Field field;
	private final int numNodesX;
	private final int numNodesY;
	private final int nodeDistance;
	private final int nodeSignalStrength;
	private final int requestLife;
	private final int agentLife;
	
	/**
	 * <p>
	 * Creates a <code>StandardNodeNetworkGenerator</code> object to
	 * prepare for the creation of a node network grid.
	 * </p>
	 * @param numNodesX the number of nodes on the x-axis.
	 * @param numNodesY the number of nodes on the y-axis.
	 * @param nodeDistance the distance between the nodes.
	 * @param nodeSignalStrength the signal strength of the nodes.
	 * @param agentLife the amount of lives this the nodes should instantiate their agent messages
	 * with.
	 * @param requestLife the amount of lives this the nodes should instantiate their request messages
	 * with.
	 * @param field the <code>Field</code>.
	 */
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
	/**
	 * <p>
	 * Generates the node network and returns it in a <code>HashMap</code>.
	 * Calling this method has no effect other than returning the generated node network
	 * again, if called additional times after already called it once.
	 * </p>
	 * @return the node network.
	 */
	@Override
	public HashMap<Position, Node> generate(){
		Node tempNode;
		Position tempPosition;
		if(map != null && map.isEmpty()){
			for(int y=0; y<numNodesY; y++){
				for(int x=0; x<numNodesX; x++){
					tempPosition = new Position(x*nodeDistance, 
							y*nodeDistance);
					tempNode = new Node(field, tempPosition,
							nodeSignalStrength,
							agentLife,
							requestLife);
					map.put(tempPosition, tempNode);
				}
			}
		}
		
		return new HashMap<Position, Node>(map);
	}
}
