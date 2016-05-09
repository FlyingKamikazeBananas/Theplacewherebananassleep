package surrounding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import coordination.Position;
import nodebasis.Node;

public class Field {

	private HashMap<Position, Node> nodeMap;
	
	public Field(/*params*/){
		nodeMap = new HashMap<Position, Node>();
	}
	
	protected void update(){
		//do stuff
	}
	
	public ArrayList<Node> getNodesWithinRangeofNode(Node nodeAtCentrum){
		//
		int signalStrength = nodeAtCentrum.getSignalStrength();
		int minY = (0 < nodeAtCentrum.getPosition().getY()-signalStrength ? 
				nodeAtCentrum.getPosition().getY()-signalStrength : 0);
		int maxY = 0; //change
		ArrayList<Node> listToReturn = new ArrayList<Node>();
		
		for(Entry<Position, Node> entry : nodeMap.entrySet()){
			Position mapPos = entry.getKey();
			Node mapNode = entry.getValue();
			
			if(minY <= mapPos.getY() && mapPos.getY() <= maxY){
				
			}
			
		}
	}
}
