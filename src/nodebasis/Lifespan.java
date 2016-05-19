package nodebasis;

/**
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public interface Lifespan{
	void decrementLifespan();
	boolean isDead();
}
