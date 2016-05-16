package coordination;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Position{

	private final int x, y;
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	@Override
	public int hashCode(){
		final int primeA = 17;
		final int primeB = 31;
		
		return new HashCodeBuilder(primeA, primeB)
			.append(getX())
			.append(getY())
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		Position other;
		
		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		other = (Position)obj;
		if(this.getX() != other.getX()){
			return false;
		}
		if(this.getY() != other.getY()){
			return false;
		}
		return true;
	}
}
