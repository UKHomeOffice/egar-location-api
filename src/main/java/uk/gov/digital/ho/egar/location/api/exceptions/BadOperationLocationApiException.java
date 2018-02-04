package uk.gov.digital.ho.egar.location.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.FORBIDDEN) 
public class BadOperationLocationApiException extends LocationApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @param message
	 */
	public BadOperationLocationApiException(String message) {
		super(message);
	}




	
}