package nodebasis;

/**
 * The Lifespan interface can be implemented by any classes whose instances should have 
 * an explicit lifespan, and where the control of that lifespan is important.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public interface Lifespan{
	void decrementLifespan();
	boolean isDead();
}
