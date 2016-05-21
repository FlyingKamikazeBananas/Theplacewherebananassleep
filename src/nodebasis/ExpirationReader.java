package nodebasis;

/**
 * Classes extending this abstract class can be used in purpose of monitoring
 * when a specific node discards an expired request or agent message. 
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-20
 * */
public abstract class ExpirationReader{
	
	public enum ReaderMode{
		EXPIRED_AGENTS, EXPIRED_REQUESTS, ALL;
	}
	
	public abstract void readIdOfExpiredObject(String str);
	public abstract ReaderMode getReaderMode();
}
