package surrounding;

import java.util.HashMap;

import nodebasis.Node;
import coordination.Position;

public interface NodeNetworkGenerator{
	public HashMap<Position, Node> generate();
}
