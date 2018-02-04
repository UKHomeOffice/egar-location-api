package uk.gov.digital.ho.egar.location.api.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.gov.digital.ho.egar.shared.util.exceptions.NoCallStackException;

/**
 * A base exception type that does not pick uo the stack trace.
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY) // Suppress empty arrays & nulls
public class LocationApiException extends NoCallStackException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LocationApiException() {
		this(null,null);		
	}

	public LocationApiException(String message) {
		this(message,null);
	}

	public LocationApiException(Throwable cause) {
		this(null,cause);
	}

	public LocationApiException(String message, Throwable cause) {
		super(message, cause);
        }

}
