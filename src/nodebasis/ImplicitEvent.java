package nodebasis;

/**
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class ImplicitEvent{

	private final int id;
	private int distance;
	private Node node;
	
	/**
	 * <b>ImplicitEvent</b>
	 * <pre>public ImplicitEvent(Event event)</pre>
	 * <p>
	 * Creates an <code>ImplicitEvent</code> object with an <code>Event</code> as basis.
	 * </p>
	 * @param event an existing event to base this implicit event on.
	 * @see Event
	 */
	public ImplicitEvent(Event event){
		this(event.getId(), 0, event.getNode());
	}
	
	/**
	 * <b>ImplicitEvent</b>
	 * <pre>public ImplicitEvent(int id, int distance, Node node)</pre>
	 * <p>
	 * Creates an <code>ImplicitEvent</code> object with attributes resembling
	 * a real event.
	 * </p>
	 * @param id the id of the event.
	 * @param distance the distance to the event.
	 * @param node a node which knows the way to the event.
	 * @see Event
	 */
	public ImplicitEvent(int id, int distance, Node node){
		this.id = id;
		this.distance = distance;
		this.node = node;
	}
	
	/**
	 * <b>getId</b>
	 * <pre>public int getId()</pre>
	 * <p>
	 * Returns the id of the event.
	 * </p>
	 * @return the id of the event.
	 * @see Event
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * <b>setDistance</b>
	 * <pre>public void setDistance(int distance)</pre>
	 * <p>
	 * Sets the distance to the event.
	 * </p>
	 * @param distance the new distance to the event.
	 * @see Event
	 */
	public void setDistance(int distance){
		this.distance = distance;
	}
	
	/**
	 * <b>getDistance</b>
	 * <pre>public int getDistance()</pre>
	 * <p>
	 * Gets the distance to the event.
	 * </p>
	 * @return the distance to the event.
	 * @see Event
	 */
	public int getDistance(){
		return distance;
	}
	
	/**
	 * <b>setNode</b>
	 * <pre>public void setNode(Node node)</pre>
	 * <p>
	 * Sets a new node which knows the way to the event.
	 * </p>
	 * @param node a node which knows the way to the event.
	 * @see Event
	 */
	public void setNode(Node node){
		this.node = node;
	}
	
	/**
	 * <b>getNode</b>
	 * <pre>public Node getNode()</pre>
	 * <p>
	 * Gets the node which knows the way to the event.
	 * </p>
	 * @return node which knows the way to the event.
	 * @see Event
	 */
	public Node getNode(){
		return node;
	}
}
