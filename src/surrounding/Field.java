package surrounding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import coordination.Position;
import nodebasis.Node;

public class Field {

	private static final int MAX_PLACEHOLDER = 0;
	
	private HashMap<Position, Node> nodeMap;
	private boolean recentlyChangedNodeNetwork;
	private int currentTime;
	
	public Field(/*params*/){
		recentlyChangedNodeNetwork = true;
		nodeMap = new HashMap<Position, Node>();
		setCurrentTime(0);
		
		update();
		setRecentlyChangedNodeNetwork(false);
	}
	
	protected void update(){
		//do stuff
		incrementCurrentTime();
	}
	
	public ArrayList<Node> getNodesWithinRangeofNode(Node nodeAtCentrum){
		int signalStrength = nodeAtCentrum.getSignalStrength();
		int centrumX = nodeAtCentrum.getPosition().getX();
		int centrumY = nodeAtCentrum.getPosition().getY();
		int minY = (0 < centrumY-signalStrength ? centrumY-signalStrength : 0);
		int maxY = (MAX_PLACEHOLDER > centrumY+signalStrength ? MAX_PLACEHOLDER 
				: centrumY+signalStrength); //change placeholder
		int currentYOffset;
		int currentMinX;
		int currentMaxX;
		int boundsX;
		ArrayList<Node> listToReturn = new ArrayList<Node>();
		
		for(Entry<Position, Node> entry : nodeMap.entrySet()){
			Position mapPos = entry.getKey();
			Node mapNode = entry.getValue();
			
			if(minY <= mapPos.getY() && mapPos.getY() <= maxY){
				currentYOffset = Math.abs(centrumY-mapPos.getY());
				boundsX = (int)Math.sqrt(Math.abs(Math.pow(signalStrength, 2)-
						Math.pow(currentYOffset, 2))); 
				currentMinX = (0 < centrumX-boundsX ? centrumX-boundsX : 0);
				currentMaxX = (MAX_PLACEHOLDER > centrumX+boundsX ? MAX_PLACEHOLDER 
						: centrumX+boundsX); //change placeholder
				
				if((centrumX <= mapPos.getX() && mapPos.getX() <= currentMaxX) ||
						(currentMinX <= mapPos.getX() && mapPos.getX() < centrumX)){
					listToReturn.add(mapNode);
				}
			}
		}
		
		return listToReturn;
	}
	
	public boolean getRecentlyChangedNodeNetwork(){
		return recentlyChangedNodeNetwork;
	}
	
	private void setRecentlyChangedNodeNetwork(boolean recentlyChangedNodeNetwork){
		this.recentlyChangedNodeNetwork = recentlyChangedNodeNetwork;
	}
	
	private void setCurrentTime(int currentTime){
		this.currentTime = currentTime;
	}
	
	private void incrementCurrentTime(){
		currentTime++;
	}
	
	public int getCurrentTime(){
		return currentTime;
	}
}
