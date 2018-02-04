/**
 * 
 */
package uk.gov.digital.ho.egar.location.api.exceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author localuser
 *
 */
public class ConstraintViolationLocationApiException extends BadOperationLocationApiException {

	private static final long serialVersionUID = 1L;
	
	public ConstraintViolationLocationApiException(ConstraintViolationException ex) {
		super(buildValidationErrorCauseMessage(ex)) ;
	}


	private static String buildValidationErrorCauseMessage(ConstraintViolationException ex) {
		
		StringBuffer sb = new StringBuffer();
		
		for ( ConstraintViolation<?> error : ex.getConstraintViolations() )
		{
			sb.append("Error:");
			sb.append(error.getPropertyPath());
			sb.append(" '");
			sb.append(error.getMessage());
			sb.append("';\r");
		}
		
		return sb.toString();
	}


	
}
