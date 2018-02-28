package uk.gov.digital.ho.egar.location.service.impl;

import java.util.List;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.location.api.exceptions.BadOperationLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.ConstraintViolationLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.DataNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;
import uk.gov.digital.ho.egar.location.service.GeographicLocationService;
import uk.gov.digital.ho.egar.location.service.repository.GeographicLocationPersistentRecordRepository;
import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;

@Service
public class GeographicLocationServiceDatabaseImpl implements GeographicLocationService {
	
	
	private final GeographicLocationPersistentRecordRepository repository;
	
	
	public GeographicLocationServiceDatabaseImpl(@Autowired GeographicLocationPersistentRecordRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public GeographicLocationWithUuid addLocation(UUID userUuid,GeographicLocation newLoc) throws LocationApiException {
		
		GeographicLocationPersistentRecord persistentLoc ;
		
		if ( newLoc instanceof GeographicLocationPersistentRecord )
		{
			persistentLoc = (GeographicLocationPersistentRecord) newLoc ;
			if ( persistentLoc.getLocationUuid() != null )
			{
				throw new BadOperationLocationApiException("Location passed in already had a UUID");
			}
			if ( persistentLoc.getUserUuid() != null )
			{
				throw new BadOperationLocationApiException("Location passed in already had a user UUID");
			}
		}
		else
		{
			persistentLoc = new GeographicLocationPersistentRecord().copy(newLoc);
		}
				
		persistentLoc.setUserUuid(userUuid);
		persistentLoc.setLocationUuid(UUID.randomUUID()); // NEW ID
		
		return saveAndFlush(persistentLoc);
		
	}

	/** 
	 * Simply does the exception catching on save.
	 */
	private GeographicLocationWithUuid saveAndFlush(GeographicLocationPersistentRecord persistentLoc) throws LocationApiException {
		try {
			return this.repository.saveAndFlush(persistentLoc);
		} 
		catch (ConstraintViolationException ex) 
		{
			// Failed
			throw new ConstraintViolationLocationApiException(ex);
		}
	}

	@Override
	public GeographicLocationWithUuid updateLocation(GeographicLocationWithUuid newLoc) throws LocationApiException {
		
		// Get record using internal method.
		GeographicLocationPersistentRecord persistentLoc = this.getLocation(newLoc.getUserUuid(), newLoc.getLocationUuid());

		persistentLoc.clear();

		persistentLoc.copy(newLoc);
		
		return saveAndFlush(persistentLoc);
	}



	@Override
	public GeographicLocationPersistentRecord getLocation(UUID userUuid,UUID locationUuid) throws DataNotFoundLocationApiException {
		
		GeographicLocationPersistentRecord record = this.repository.findOneByLocationUuidAndUserUuid(locationUuid, userUuid);
		
		if ( record == null )
		{
			throw new LocationNotFoundLocationApiException(locationUuid,userUuid);
		}
		
		return record ;
	}

	@Override
	public GeographicLocationWithUuid[] getBulkLocations(UUID uuidOfUser, List<UUID> locationUuids) {

		List<GeographicLocationPersistentRecord> locationsList = repository.findAllByUserUuidAndLocationUuidIn(uuidOfUser,locationUuids);
		GeographicLocationWithUuid[] locationsArray = new GeographicLocationWithUuid[locationsList.size()];
		locationsArray = locationsList.toArray(locationsArray);
		
		return locationsArray;
	}

}
