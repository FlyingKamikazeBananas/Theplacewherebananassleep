package surrounding;

import java.util.HashMap;

import nodebasis.Node;
import coordination.Position;

public class FileNodeNetworkGenerator implements NodeNetworkGenerator{

	private Field field;
	
	public FileNodeNetworkGenerator(java.io.Reader reader,
			Field field){
		this.field = field;
	}
	
	//file convention
	//x;y;r;R;A
	
	//TBI
	@Override
	public HashMap<Position, Node> generate(){
		return null;
	}
}
