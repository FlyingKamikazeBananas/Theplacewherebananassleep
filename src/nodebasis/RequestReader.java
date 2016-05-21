package nodebasis;

/**
 * Classes implementing this interface can be used in purpose of monitoring
 * when a specific node retrieves an earlier sent message.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-20
 * */
public interface RequestReader {

	public void readSuccessfulRequestId(String str);
}
