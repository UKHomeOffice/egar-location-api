package uk.gov.digital.ho.egar.location.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class LocationNotFoundLocationApiException extends DataNotFoundLocationApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public LocationNotFoundLocationApiException(final UUID locationId , final UUID userId )
	{
		super(String.format("Can not find location %s for user %s", locationId.toString(), userId.toString()));
	}
	
}
