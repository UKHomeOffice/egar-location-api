/**
 * 
 */
package uk.gov.digital.ho.egar.location.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;

import javax.validation.ConstraintViolationException;
/**
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {GeographicLocationPersistentRecordRepositoryTest.class})
@EnableAutoConfiguration
public class GeographicLocationPersistentRecordRepositoryTest {

	@Autowired
    private GeographicLocationPersistentRecordRepository repo;
	
	@Test
	public void jpaSchemaCanBeMappedFromClasses() {
	    assertThat(this.repo).isNotNull();
	}

	@Test
	public void canSaveWithOnlyUserAndLocationId() {
		
		// WITH
		long count = repo.count();


		GeographicLocationPersistentRecord loc = GeographicLocationPersistentRecord.builder()
															.locationUuid(UUID.randomUUID())
															.userUuid(UUID.randomUUID())
															.build();
				
	    // WHEN
		repo.saveAndFlush(loc); // Cause Hibernate to flush result to database on the spot
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}

	@Test
	public void canSaveWithOnlyUserAndLocationIdAndDate() {
		// WITH
		long count = repo.count();


		// WHEN
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
												.locationUuid(UUID.randomUUID())
												.dateTimeAt(ZonedDateTime.now())
												.userUuid(UUID.randomUUID())
												.build());
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void cannotSaveWithInvalidIcao() {
		
		// WITH
				
	    // WHEN
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
												.locationUuid(UUID.randomUUID())
												.dateTimeAt(ZonedDateTime.now())
												.userUuid(UUID.randomUUID())
												.icaoCode("INVALID_ICAO_CODE") // ICAO Code too long
												.build());
		
		// THEN
		Assert.fail();
	}
	@Test
	public void canSaveWithUserAndLocationIdAndDateAndIcao() {
		
		// WITH
		long count = repo.count();
				
	    // WHEN
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
												.locationUuid(UUID.randomUUID())
												.userUuid(UUID.randomUUID())
												.dateTimeAt(ZonedDateTime.now())
												.icaoCode("EGLL") // ICAO for Heathrow.
												.build());
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}

	@Test
	public void canSaveWithUserAndLocationIdAndDateAndLatLong() {
		
		// WITH
		long count = repo.count();
				
	    // WHEN
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
												.locationUuid(UUID.randomUUID())
												.userUuid(UUID.randomUUID())
												.dateTimeAt(ZonedDateTime.now())
												// For Bath
												.latitude("51.37")
												.longitude("-02.35")
												.build());
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}	

	@Test
	public void canSaveWithInvalidLatLong() {
		
		// WITH
		long count = repo.count();

		// WHEN
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
												.locationUuid(UUID.randomUUID())
												.userUuid(UUID.randomUUID())
												.dateTimeAt(ZonedDateTime.now())
												// For Bath but no longitude
												.latitude("51.37")
												.build());
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	@Test
	public void canFindByUserId() {
		
		// WITH
		int numberOfGars = 10 ;
		UUID userUuid1 = UUID.randomUUID();
		UUID userUuid2 = UUID.randomUUID();
		for ( int n = 0 ; n < numberOfGars ; n++ )
		{
			repo.saveAndFlush(GeographicLocationPersistentRecord.builder().locationUuid(UUID.randomUUID()).userUuid(userUuid1).dateTimeAt(ZonedDateTime.now()).icaoCode("EGLL").build());
			repo.saveAndFlush(GeographicLocationPersistentRecord.builder().locationUuid(UUID.randomUUID()).userUuid(userUuid2).dateTimeAt(ZonedDateTime.now()).icaoCode("EGLL").build());
		}
		
	    // WHEN
		List<GeographicLocationPersistentRecord> gars = repo.findAllByUserUuid(userUuid1);
		
		// THEN
		assertThat(repo.count()).isGreaterThan(numberOfGars);
		assertThat(gars).isNotNull().hasSize(numberOfGars);
		
	}
	
	
	@Test
	public void canSaveAndFindByUserUuid() {
		
		// WITH
		UUID userUuid = UUID.randomUUID();
		UUID locationUuid = UUID.randomUUID();
		
		repo.saveAndFlush(GeographicLocationPersistentRecord.builder()
											.locationUuid(locationUuid)
											.dateTimeAt(ZonedDateTime.now())
											.icaoCode("EGLL")
										    .userUuid(userUuid)
								.build());
		
	    // WHEN
		GeographicLocationPersistentRecord foundLoc = repo.findOneByLocationUuidAndUserUuid(locationUuid,userUuid);
		
		// THEN
		assertThat(foundLoc).isNotNull();
		assertThat(foundLoc.getLocationUuid()).isEqualTo(locationUuid);
		assertThat(foundLoc.getUserUuid()).isEqualTo(userUuid);
		
	}
	

	
	
}
