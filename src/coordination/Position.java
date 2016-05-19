package coordination;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
* The <code>Position class</code> represents a two-dimensional point consisting of an x- and a y-value.
* Inspector and navigator methods such as <code>getX</code> and <code>getY</code> are provided. 
* 
* @author  Alexander Beliaev
* @version 1.0
* @since   2016-05-19
* */
public class Position{

	private final int x, y;
	
	/**
	 * <b>Position</b>
	 * <pre>public Position(int x, int y)</pre>
	 * <p>
	 * Creates a <code>Position</code> object with the specified x- and y-values.
	 * </p>
	 * @param x the x value.
	 * @param y the y value.
	 */
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * <b><i>getX</i></b>
	 * <pre>public int getX()</pre>
	 * <p>
	 * Gets the x-value.
	 * </p>
	 * @return Returns the <code><b>int</b></code> x-value of the <code>Position</code>.
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * <b><i>getY</i></b>
	 * <pre>public int getY()</pre>
	 * <p>
	 * Gets the y-value.
	 * </p>
	 * @return Returns the <code><b>int</b></code> y-value of the <code>Position</code>.
	 */
	public int getY(){
		return y;
	}
	
	@Override
	public int hashCode(){
		final int primeA = 92821;
		final int primeB = 31;
		
		return new HashCodeBuilder(primeA, primeB)
			.append(getX())
			.append(getY())
			.toHashCode();
	}
	
	/**
	 * <b><i>equals</i></b>
	 * <pre>public boolean equals(Object obj)</pre>
	 * <p>
	 * Returns <code>true</code> if and only if this <code>Position</code> and 
	 * the compared object refer to the same (<code>this == other is true</code>), <b>or</b> if the <code>x</code> and <code>y</code> values of this 
	 * <code>Position</code> equal to the <code>x</code> and <code>y</code> value of the compared <code>Position</code>.
	 * </p>
	 * @param obj - the <code>Object</code> to compare to this.
	 * @return <code>true</code> if this <code>Position</code> and the compared object refer to the same, or if their <code>x</code> and <code>y</code> 
	 * values correspond.
	 * @see Position
	 */
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
