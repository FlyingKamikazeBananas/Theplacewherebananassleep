package nodebasis;

import coordination.Position;

/**
 * The <code>Event</code> class holds some specific details regarding an event,
 * such as the time of occurrence and the id.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * 
 * */
public class Event{
	
	private final int id, time;
	private final Position position;
	private final Node node;
	
	/**
	 * <p>
	 * Creates an <code>Event</code> object with the id of the event, the
	 * time which the event occurred and the originating node.
	 * </p>
	 * @param id the id of the event.
	 * @param time the time of the event.
	 * @param node the originating node where the event occurred.
	 */
	public Event(int id, int time, Node node){
		this.id = id;
		this.time = time;
		this.position = node.getPosition();
		this.node = node;
	}

	/**
	 * <p>
	 * Return the id of the event.
	 * </p>
	 * @return the id of the event.
	 */
	public int getId() {
		return id;
	}

	/**
	 * <p>
	 * Return the time of the event.
	 * </p>
	 * @return the time of the event.
	 */
	public int getTime() {
		return time;
	}

	/**
	 * <p>
	 * Return the position of the node where the event occurred.
	 * </p>
	 * @return the position of the node where the event occurred.
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * <p>
	 * Returns the node where the event occurred.
	 * </p>
	 * @return the node where the event occurred.
	 */
	public Node getNode(){
		return node;
	}
	
	
}
