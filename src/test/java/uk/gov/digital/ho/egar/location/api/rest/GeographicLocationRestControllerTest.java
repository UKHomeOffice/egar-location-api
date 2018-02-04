package uk.gov.digital.ho.egar.location.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;

import uk.gov.digital.ho.egar.location.api.LocationsRestAPI;
import uk.gov.digital.ho.egar.location.api.exceptions.ConstraintViolationLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.DataNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationApiException;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;
import uk.gov.digital.ho.egar.location.service.GeographicLocationService;
import uk.gov.digital.ho.egar.location.service.impl.GeographicLocationServiceDatabaseImpl;
import uk.gov.digital.ho.egar.location.service.repository.GeographicLocationPersistentRecordRepository;
import uk.gov.digital.ho.egar.location.service.repository.GeographicLocationPersistentRecordRepositoryTest;
import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;


/**
 * @See https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-jpa-test
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {GeographicLocationPersistentRecordRepositoryTest.class}) // Pulls in the repo test to init the tests.
@EnableAutoConfiguration
public class GeographicLocationRestControllerTest {
	

	@Autowired
    private GeographicLocationPersistentRecordRepository repo;
	
	private LocationsRestAPI restController ;
	
    private static Errors errors;
	
	@Rule 
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	

	@Before
	public void initRestController () 
	{		
		if ( this.restController == null )
		{
			GeographicLocationService geoService = new GeographicLocationServiceDatabaseImpl(repo);
			this.restController = new GeographicLocationRestController(geoService);
		    
		}
		
		errors = mock(Errors.class);
		when(errors.hasErrors()).thenReturn(false);
	}
	
	@Test
	public void jpaSchemaCanBeMappedFromClasses() {
	    assertThat(this.repo).isNotNull();
	}
	
	@Test
	public void canInitRestController() {
	    assertThat(this.restController).isNotNull();
	}


	@Test
	public void canSaveWithOnlyUserAndLocationId() throws Exception {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();
		GeographicLocationPersistentRecord newloc = GeographicLocationPersistentRecord.builder()
															.build();
		
				
	    // WHEN
		ResponseEntity<Void> resp = restController.addLocation(userUuid, newloc, errors);
		
		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	
	@Test(expected=ConstraintViolationLocationApiException.class)
	public void cannotSaveWithInvalidIcao() throws Exception {
		
		// WITH
		UUID userUuid = UUID.randomUUID();
		GeographicLocationPersistentRecord newloc = GeographicLocationPersistentRecord.builder()
				.icaoCode("WRONG_ICAO_CODE")
				.build();
				
	    // WHEN
		restController.addLocation(userUuid, newloc, errors);
		
		// THEN
		Assert.fail();
	}
	@Test
	public void canSaveWithUserAndLocationIdAndDateAndIcao() throws LocationApiException {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();
		GeographicLocationPersistentRecord newloc = GeographicLocationPersistentRecord.builder()
				.icaoCode("EGLL")
				.dateTimeAt(ZonedDateTime.now())
				.build();
				
	    // WHEN
		ResponseEntity<Void> resp = restController.addLocation(userUuid, newloc, errors);

		
		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();
		assertThat(repo.count()).isEqualTo(count+1);
	}

	@Test
	public void canSaveWithUserAndLocationIdAndDateAndLatLong() throws LocationApiException {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();
		GeographicLocationPersistentRecord newloc = GeographicLocationPersistentRecord.builder()
				// For Bath
				.latitude("51.37")
				.longitude("-02.35")
				.dateTimeAt(ZonedDateTime.now())
				.icaoCode("EGLL")
				.build();
		
	    // WHEN
		ResponseEntity<Void> resp = restController.addLocation(userUuid, newloc, errors);

		
		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();
		assertThat(repo.count()).isEqualTo(count+1);
	}	

	@Test
	public void canSaveWithInvalidLatLong() throws LocationApiException {
		
		// WITH
		long count = repo.count();

		UUID userUuid = UUID.randomUUID();
		GeographicLocationPersistentRecord newloc = GeographicLocationPersistentRecord.builder()
				// For Bath but no longitude
				.latitude("51.37")
				.dateTimeAt(ZonedDateTime.now())
				.icaoCode("EGLL")
				.build();
				
	    // WHEN
		ResponseEntity<Void> resp = restController.addLocation(userUuid, newloc, errors);
		
		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();
		assertThat(repo.count()).isEqualTo(count+1);		}

	@Test
	public void canRetrieveAGar() throws Exception
	{
		// WITH
		final long locCount = repo.findAll().size();
		final UUID userUuid = UUID.randomUUID() ;
		final UUID locUuid = repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
				.locationUuid(UUID.randomUUID())
				.userUuid(userUuid)
				.dateTimeAt(ZonedDateTime.now())
				.icaoCode("EGLL")
				.build())
				.getLocationUuid();
		
		// WHEN
		GeographicLocationWithUuid foundLoc = this.restController.getLocation(userUuid, locUuid);
		
		// THEN
		assertThat(foundLoc).isNotNull();
		assertThat(foundLoc.getLocationUuid()).isNotNull().isEqualTo(locUuid);
		assertThat(foundLoc.getUserUuid()).isNotNull().isEqualTo(userUuid);
		assertThat(repo.count()).isEqualTo(locCount+1);
	}	
	
	
	@Test
	public void canFindAllGarsForUser() throws Exception
	{
		// WITH
		final UUID userUuid1 = UUID.randomUUID() ;
		final UUID userUuid2 = UUID.randomUUID() ;
		UUID locUuid = null ;
		final long userLocCount = repo.countByUserUuid(userUuid1);
		final long locCount = repo.findAll().size();
		final int testCount = 10;
		
		for ( int n = 0 ; n < testCount ; n++ )
		{
			locUuid = 
					repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
							.locationUuid(UUID.randomUUID())
							.userUuid(userUuid1)
							.dateTimeAt(ZonedDateTime.now())
							.icaoCode("EGLL")
							.build())
					.getLocationUuid()
					;			
			repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
					.locationUuid(UUID.randomUUID())
					.userUuid(userUuid2)
					.dateTimeAt(ZonedDateTime.now())
					.icaoCode("EGLL")
					.build());
		}

		
		// WHEN
		GeographicLocationWithUuid foundLoc = this.restController.getLocation(userUuid1, locUuid);
		
		
		// THEN
		assertThat(foundLoc).isNotNull();
		assertThat(foundLoc.getLocationUuid()).isNotNull().isEqualTo(locUuid);
		assertThat(foundLoc.getUserUuid()).isNotNull().isEqualTo(userUuid1);
		assertThat(repo.count()).isEqualTo(locCount+(2*testCount));
		assertThat(repo.countByUserUuid(userUuid1)).isEqualTo(userLocCount+testCount);
	}



    @Test
    public void canUpdateALocation() throws Exception
    {
		// WITH
		final UUID userUuid = UUID.randomUUID() ;
		final UUID locUuid = repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
				.locationUuid(UUID.randomUUID())
				.userUuid(userUuid)
				.dateTimeAt(ZonedDateTime.now())
				.icaoCode("EGLL")
				.iataCode("ABC")
				.build())
				.getLocationUuid();
		
		// WHEN
		GeographicLocationPersistentRecord updateLoc = GeographicLocationPersistentRecord.builder()
				.userUuid(userUuid)
				.dateTimeAt(ZonedDateTime.now())
				.icaoCode("EGLL")
				.iataCode("CDE")
				.locationUuid(locUuid)
				.build();
		
		ResponseEntity<Void> resp = this.restController.updateLocation(userUuid, locUuid, updateLoc, errors);

		
		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();
		
		GeographicLocationPersistentRecord savedLoc = repo.getOne(locUuid);
		assertThat(savedLoc).isNotNull();
        assertThat(savedLoc.getUserUuid()).isEqualTo(userUuid);
        assertThat(savedLoc.getIataCode()).isEqualTo("CDE");

    }

    //Fix for bug EGAR-1368
    @Test
	public void locationIsClearedWhenUpdated() throws Exception {
		// WITH
		final UUID userUuid = UUID.randomUUID() ;
		final UUID locUuid = repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
				.locationUuid(UUID.randomUUID())
				.userUuid(userUuid)
				.dateTimeAt(ZonedDateTime.now())
				.latitude("22.22")
				.longitude("22.22")
				.build())
				.getLocationUuid();

		// WHEN
		GeographicLocationPersistentRecord updateLoc = GeographicLocationPersistentRecord.builder()
				.userUuid(userUuid)
				.dateTimeAt(ZonedDateTime.now())
				.iataCode("ABC")
				.locationUuid(locUuid)
				.build();


		ResponseEntity<Void> resp = this.restController.updateLocation(userUuid, locUuid, updateLoc, errors);

		// THEN
		assertThat(resp).isNotNull();
		assertThat(resp.getStatusCodeValue()).isEqualTo(303);
		assertThat(getLocationUuid(resp)).isNotNull();

		GeographicLocationPersistentRecord savedLoc = repo.getOne(locUuid);
		assertThat(savedLoc).isNotNull();
		assertThat(savedLoc.getUserUuid()).isEqualTo(userUuid);
		assertThat(savedLoc.getIataCode()).isEqualTo("ABC");
		assertNull(savedLoc.getLongitude());
		assertNull(savedLoc.getLatitude());

	}


    private UUID getLocationUuid(ResponseEntity<Void> response) {
    	
		assertThat(response.getHeaders().containsKey("Location")).isTrue();

        String redirectPath = response.getHeaders().getLocation().getPath();
        String[] delimitedPath = redirectPath.split("/");
        String stringId = delimitedPath[delimitedPath.length -1];
        return UUID.fromString(stringId);
    }
	


}
