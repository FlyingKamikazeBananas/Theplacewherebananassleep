package nodebasis;

import java.util.ArrayList;

public class RoutingTable{
	
	private ArrayList<TableItem> itemList;
	
	class TableItem{
		private int id, distance;
		private Node node;
		
		TableItem(int id, int distance, Node node){
			this.id = id;
			this.distance = distance;
			this.node = node;
		}
		
		public int getId(){
			return id;
		}
		
		public int getDistance(){
			return distance;
		}
		
		public Node getNode(){
			return node;
		}
		
		@Override
		public boolean equals(Object obj){
			TableItem other;
			
			if(this == obj){
				return true;
			}
			if(obj == null){
				return false;
			}
			if(getClass() != obj.getClass()){
				return false;
			}
			other = (TableItem)obj;
			if(this.getId() != other.getId()){
				return false;
			}
			return true;
		}
	}
	
	public RoutingTable(){
		itemList = new ArrayList<TableItem>();
	}
	
	public void addItem(int id, int distance, Node node){
		itemList.add(new TableItem(id, distance, node));
	}
	
	public void removeItemById(int id){
		itemList.remove(new TableItem(id, 0, null));
			
	}
	
	public TableItem getItemById(int id) throws IllegalArgumentException{
		for(TableItem item : itemList){
			if(item.getId() == id){
				return item;
			}
		}
		throw new IllegalArgumentException("item with id not found");
	}
	
	
}
