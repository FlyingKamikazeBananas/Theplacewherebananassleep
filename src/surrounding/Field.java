package surrounding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import coordination.Position;
import nodebasis.Node;

public class Field {
	
	private HashMap<Position, Node> nodeMap;
	private boolean recentlyChangedNodeNetwork;
	private int currentTime;
	private boolean hasLoadedNodeNetwork;
	
	public Field(int updateLimit, int eventChanceRange, 
			int agentChanceRange, int requestChanceRange,
			int numberOfRequestNodes){
		recentlyChangedNodeNetwork = true;
		nodeMap = new HashMap<Position, Node>();
		setCurrentTime(0);
		hasLoadedNodeNetwork = false;
		//update();
		setRecentlyChangedNodeNetwork(false);
	}
	
	protected void update(){
		//do stuff
		incrementCurrentTime();
	}
	
	public void loadNodeNetwork(HashMap<Position, Node> nodeMap){
		if(!hasLoadedNodeNetwork){
			this.nodeMap = nodeMap;
			hasLoadedNodeNetwork = true;
		}
	}
	
	public ArrayList<Node> getNodesWithinRangeofNode(Node nodeAtCentrum){
		int signalStrength = nodeAtCentrum.getSignalStrength();
		int centrumX = nodeAtCentrum.getPosition().getX();
		int centrumY = nodeAtCentrum.getPosition().getY();
		int minYFromCentrum = (0 < centrumY-signalStrength ? centrumY-signalStrength : 0);
		int maxYFromCentrum = centrumY+signalStrength;
		int currentYOffset;
		int currentMinXFromCentrum;
		int currentMaxXFromCentrum;
		int boundsX;
		ArrayList<Node> listToReturn = new ArrayList<Node>();
		
		for(Entry<Position, Node> entry : nodeMap.entrySet()){
			Position mapPos = entry.getKey();
			Node mapNode = entry.getValue();
			
			if(minYFromCentrum <= mapPos.getY() && mapPos.getY() <= maxYFromCentrum){
				currentYOffset = Math.abs(centrumY-mapPos.getY());
				boundsX = (int)Math.sqrt(Math.abs(Math.pow(signalStrength, 2)-
						Math.pow(currentYOffset, 2))); 
				currentMinXFromCentrum = (0 < centrumX-boundsX ? centrumX-boundsX : 0);
				currentMaxXFromCentrum = centrumX+boundsX;
				
				if((centrumX <= mapPos.getX() && mapPos.getX() <= currentMaxXFromCentrum) ||
						(currentMinXFromCentrum <= mapPos.getX() && mapPos.getX() < centrumX)){
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
	
	public boolean getHasLoadedNodeNetwork(){
		return hasLoadedNodeNetwork;
	}
}
