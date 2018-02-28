package uk.gov.digital.ho.egar.location.service;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.location.api.exceptions.DataNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationApiException;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;

public interface GeographicLocationService {
	
	/**
	 * Find the location using the params.
	 * @param locationUuid
	 * @param userUuid
	 * @return the record 
	 * @throws DataNotFoundLocationApiException 
	 */
	GeographicLocationWithUuid getLocation(UUID userUuid,UUID locationUuid) throws DataNotFoundLocationApiException;
	
	GeographicLocationWithUuid addLocation(UUID userUuid,GeographicLocation newLoc) throws LocationApiException;
	GeographicLocationWithUuid updateLocation(GeographicLocationWithUuid loc) throws LocationApiException;

	GeographicLocationWithUuid[] getBulkLocations(final UUID uuidOfUser, final List<UUID> locationUuids);
}
