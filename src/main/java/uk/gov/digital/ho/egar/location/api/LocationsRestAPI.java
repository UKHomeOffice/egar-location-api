package uk.gov.digital.ho.egar.location.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import uk.gov.digital.ho.egar.location.api.exceptions.DataNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationApiException;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;

/**
 * The definition of the API in response to the location requests.
 */
public interface LocationsRestAPI  {
	
	/**
	 * Add a new location.
	 * @param userUuid The id of the user requesting the data.
	 * @param location the new location
	 * @return The response code. A 303,401.
	 * @throws LocationApiException 
	 */
	ResponseEntity<Void> addLocation(UUID userUuid, GeographicLocation location, Errors errors) throws LocationApiException;
	
	
	
	/**
	 * Retrieve an existing location.
	 * @param userUuid The id of the user requesting the data.
	 * @param locationUuid The id 
	 * @return
	 * @throws DataNotFoundLocationApiException 
	 */
	GeographicLocationWithUuid getLocation(UUID userUuid, UUID locationUuid) throws DataNotFoundLocationApiException;
	
	/**
	 * Update an existing location.
	 * @param userUuid
	 * @param locationUuid
	 * @param location
	 * @return The response code. A 303,401.
	 * @throws DataNotFoundLocationApiException 
	 * @throws LocationApiException 
	 */
	ResponseEntity<Void> updateLocation(UUID userUuid, UUID locationUuid, GeographicLocation location,  Errors errors) throws LocationApiException;


	/**
	 * Retrieve a list of locations
	 * @param uuidOfUser
	 * @param locationUuids
	 * @return list of locations
	 */
	GeographicLocationWithUuid[] bulkRetrieveLocations(final UUID uuidOfUser, final List<UUID> locationUuids);
}
