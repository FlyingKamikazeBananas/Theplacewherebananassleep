package surrounding;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nodebasis.Node;
import coordination.Position;

/**
 * As of now this class is only used for testing purposes.
 * It does pretty much the same thing as the <code>StandardNodeNetworkGenerator</code> class
 * except that it reads from a file. This allows for further customization
 * of the node network.
 * 
 * */
public class FileNodeNetworkGenerator implements NodeNetworkGenerator{

	private Field field;
	private BufferedReader reader;
	private Map<Position, Node> map;
	private Exception e;
	
	public FileNodeNetworkGenerator(java.io.Reader reader,
			Field field){
		this.reader = new BufferedReader(reader);
		this.field = field;
		map = new HashMap<Position, Node>();
		e = null;
	}
	
	//file convention
	//x;y;r;A;R
	
	@Override
	public HashMap<Position, Node> generate(){
		final int numAttributes = 5;
		String line;
		String nodeAttributes[];
		Position tempPosition;
		Node tempNode;
		e = null;
		
		if(map.isEmpty()){
			try{
				while((line = reader.readLine()) != null){
					nodeAttributes = line.split("[;]");
					if(nodeAttributes.length != numAttributes){
						e = new IOException("too many or too few attributes"
								+ " for node(s) in node network specification.");
						return null;
					}else{
						try{
							tempPosition = new Position(Integer.parseInt(nodeAttributes[0]),
									Integer.parseInt(nodeAttributes[1]));
							tempNode = new Node(field, tempPosition,
									Integer.parseInt(nodeAttributes[2]),
									Integer.parseInt(nodeAttributes[3]),
									Integer.parseInt(nodeAttributes[4]));
							map.put(tempPosition, tempNode);
						}catch(NumberFormatException e){
							this.e = e;
							return null;
						}
					}
				}
				return (HashMap<Position, Node>)map;
			}catch (IOException e) {
				this.e = e;
				return null;
			}
		}else{
			return (HashMap<Position, Node>)map;
		}
	}
	
	public void newReader(java.io.Reader reader) throws IOException{
		closeCurrentReader();
		this.reader = new BufferedReader(reader);
	}
	
	public void closeCurrentReader() throws IOException{
		this.reader.close();
	}
	
	public Exception getException(){
		return e;
	}
	
	public BufferedReader getCurrentReader(){
		return reader;
	}
	
	
}
