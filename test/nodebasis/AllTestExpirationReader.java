package nodebasis;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the ExpirationReader class and implements the RequestReader interface.
 * It is solely used for testing purposes, of finding out if a specific message was
 * expired when it was supposed to or if a request message successfully fetched data
 * from a node where an event earlier had occurred.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-20
 * */
public class AllTestExpirationReader extends ExpirationReader implements RequestReader{

	public static final ReaderMode readerMode = 
			ExpirationReader.ReaderMode.ALL;
	
	private List<String> expiredList;
	private List<String> successfulRequestList;
	
	public AllTestExpirationReader(){
		expiredList = new ArrayList<String>();
		successfulRequestList = new ArrayList<String>();
	}
	
	@Override
	public void readIdOfExpiredObject(String str){
		if(str != null){
			expiredList.add(str);
		}
	}

	@Override
	public ReaderMode getReaderMode(){
		return readerMode;
	}

	public boolean hasExpired(String str){
		for(String string : expiredList){
			if(string.equals(str)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void readSuccessfulRequestId(String str) {
		if(str != null){
			successfulRequestList.add(str);
		}
		
	}
	
	public boolean isSuccessfulRequest(String str){
		for(String string : successfulRequestList){
			if(string.equals(str)){
				return true;
			}
		}
		return false;
	}
}
