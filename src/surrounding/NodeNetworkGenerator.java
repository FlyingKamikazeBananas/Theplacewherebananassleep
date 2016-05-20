package surrounding;

import java.util.HashMap;

import nodebasis.Node;
import coordination.Position;

/**
 * Implement this interface when creating a new means to generate a node network.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public interface NodeNetworkGenerator{
	public HashMap<Position, Node> generate();
}
