package uk.gov.digital.ho.egar.location.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class DataNotFoundLocationApiException extends LocationApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DataNotFoundLocationApiException() {
	}

	/**
	 * @param message
	 */
	public DataNotFoundLocationApiException(String message) {
		super(message);
	}


}