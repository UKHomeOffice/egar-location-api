package uk.gov.digital.ho.egar.location;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import uk.co.civica.microservice.util.testing.FileReaderUtils;
import uk.gov.digital.ho.egar.location.service.repository.GeographicLocationPersistentRecordRepository;
import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;

import java.time.ZonedDateTime;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
        ={
        "eureka.client.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "flyway.enabled=false",
        "spring.profiles.active=h2",
        "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"
})
@AutoConfigureMockMvc
public class EndpointTest {

	public final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	@Deprecated
	public final String REGEX_UTC  = "[0-9]{4}-[0-9]{2}-[0-9]{2}T([0-9]{2}:){2}[0-9]{2}.[0-9]{3}[+|-][0-9]{4}";
	public final String REGEX_ZULU_DATETIME = "(19|20)[0-9][0-9]-(0[0-9]|1[0-2])-(0[1-9]|([12][0-9]|3[01]))T([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](.[0-9]{3})*Z";
	
	//private final String ENDPOINT_URI = "/api/v1/locations/";
	
	@Autowired
    private GeographicLocationPersistentRecordRepository repo;
	
    @Autowired
    private LocationApplication app;
    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper ;
    
    @BeforeClass
    public static void createObjectMapper()
    {
    objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.findAndRegisterModules();
    }
    
//    @Autowired
//    private UserService userService;

	@Test
	public void contextLoads() {
	    assertThat(app).isNotNull();
	}

	
	@Test
	public void jpaSchemaCanBeMappedFromClasses() {
	    assertThat(this.repo).isNotNull();
	}
	

	@Test()
	public void cannotPostWithOutUserAuth() throws Exception {
		
		// WITH

		String jsonLocation = FileReaderUtils.readFileAsString("files/CannotPostWithoutUserAuthRequest.json");
		
	    // WHEN
        this.mockMvc
	        .perform(post("/api/v1/locations/")
	        			.contentType(MediaType.APPLICATION_JSON_UTF8)
	        			.content(jsonLocation)
	        		)
	        .andDo(print())
	        .andExpect(status().isUnauthorized());          ;
		
		// THEN
	}

	@Test()
	public void canSaveWithUserAndLocationIdAndDateAndIcao() throws Exception {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();
		String jsonLocation = FileReaderUtils.readFileAsString("files/CanSaveWithUserAndLocationIdAndDateAndIcaoRequest.json");


		// WHEN
        this.mockMvc
	        .perform(post("/api/v1/locations/")
	        			.header("x-auth-subject", userUuid )
	        			.contentType(MediaType.APPLICATION_JSON_UTF8)
	        			.content(jsonLocation)	        		
	        		)
	        .andDo(print())
	        .andExpect(status().isSeeOther())
	        ;
        
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	@Test()
	public void canSaveWithUserAndLocationIdAndDateAndIcaoThenFetchIt() throws Exception {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();
		
		String jsonLocation = FileReaderUtils.readFileAsString("files/CanSaveWithUserAndLocationIdAndDateAndIcaoThenFetchItRequest.json");
		
	    // WHEN
		String locationURI
			=
	        this.mockMvc
		        .perform(post("/api/v1/locations/")
		        			.header("x-auth-subject", userUuid )
		        			.contentType(MediaType.APPLICATION_JSON_UTF8)
		        			.content(jsonLocation)	        		
		        		)
		        .andDo(print())
		        .andExpect(status().isSeeOther())
		        .andReturn()
		        	.getResponse()
		        		.getHeader("Location");
		        ;
       
        this.mockMvc
        		.perform(get(locationURI).header("x-auth-subject", userUuid ))
       		.andDo(print())
	        .andExpect(status().is2xxSuccessful())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
	        .andExpect(jsonPath("$.location_uuid", matchesRegex(REGEX_UUID) ))
            .andExpect(jsonPath("$.datetime", matchesRegex(REGEX_ZULU_DATETIME) ))
            ;
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	
	@Test
	public void cannotSaveWithInvalidIcao() throws Exception {
		
		// WITH
		UUID userUuid = UUID.randomUUID();

		String jsonLocation = FileReaderUtils.readFileAsString("files/CannotSaveWithInvalidIcaoRequest.json");


		// WHEN
        this.mockMvc
	        .perform(post("/api/v1/locations/")
	        			.header("x-auth-subject", userUuid )
	        			.contentType(MediaType.APPLICATION_JSON_UTF8)
	        			.content(jsonLocation)	        		
	        		)
	        .andDo(print())
			// THEN
	        .andExpect(status().is4xxClientError())
	        .andExpect(status().isBadRequest())
	        ;
		
	}

	@Test
	public void cannotSaveWithInvalidLatitude() throws Exception {

		// WITH
		UUID userUuid = UUID.randomUUID();

		String jsonLocation = FileReaderUtils.readFileAsString("files/CannotSaveWithInvalidLatitudeRequest.json");


		// WHEN
		this.mockMvc
				.perform(post("/api/v1/locations/")
						.header("x-auth-subject", userUuid )
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(jsonLocation)
				)
				.andDo(print())
				// THEN
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest())
		;

	}

	@Test
	public void cannotSaveWithInvalidLongitude() throws Exception {

		// WITH
		UUID userUuid = UUID.randomUUID();

		String jsonLocation = FileReaderUtils.readFileAsString("files/CannotSaveWithInvalidLongitudeRequest.json");


		// WHEN
		this.mockMvc
				.perform(post("/api/v1/locations/")
						.header("x-auth-subject", userUuid )
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(jsonLocation)
				)
				.andDo(print())
				// THEN
				.andExpect(status().is4xxClientError())
				.andExpect(status().isBadRequest())
		;

	}


	@Test()
	public void canSaveWithUserAndLocationIdAndDateAndLatLong() throws Exception {
		
		// WITH
		long count = repo.count();
		UUID userUuid = UUID.randomUUID();

		String jsonLocation = FileReaderUtils.readFileAsString("files/CanSaveWithUserAndLocationIdAndDateAndLatLongRequest.json");


		// WHEN
        this.mockMvc
	        .perform(post("/api/v1/locations/")
	        			.header("x-auth-subject", userUuid )
	        			.contentType(MediaType.APPLICATION_JSON_UTF8)
	        			.content(jsonLocation)	        		
	        		)
	        .andDo(print())
	        .andExpect(status().isSeeOther())
	        ;
        
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	
	@Test
	public void canSaveWithInvalidLatLong() throws Exception {
		
		// WITH
		UUID userUuid = UUID.randomUUID();

		String jsonLocation = FileReaderUtils.readFileAsString("files/CanSaveWithInvalidLatLongRequest.json");


		// WHEN
        this.mockMvc
	        .perform(post("/api/v1/locations/")
	        			.header("x-auth-subject", userUuid )
	        			.contentType(MediaType.APPLICATION_JSON_UTF8)
	        			.content(jsonLocation)	        		
	        		)
	        .andDo(print())
			// THEN
	        .andExpect(status().isSeeOther())
	        ;
	
	}
	
	//---------------------------------------------------------------------------------------------------------
	
	@Test
	public void shouldOnlyBulkfetchPeopleInListAndForThisUser() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();

		List<UUID> locationUuids = new ArrayList<>();

		// add three locations for the current user and save ids to list
		for (int i=0; i<3 ; i++) {
			UUID locationUuid = UUID.randomUUID();
			GeographicLocationPersistentRecord location = GeographicLocationPersistentRecord.builder()
							.locationUuid(locationUuid)
							.userUuid(userUuid)
							.dateTimeAt(ZonedDateTime.now())
							.icaoCode("EGLL")
							.build();

		repo.saveAndFlush(location);
			locationUuids.add(locationUuid);
		}
		// add a location for user but dont add to list
		UUID locationUuid = UUID.randomUUID();
			GeographicLocationPersistentRecord location = GeographicLocationPersistentRecord.builder()
							.locationUuid(locationUuid)
							.userUuid(userUuid)
							.dateTimeAt(ZonedDateTime.now())
							.icaoCode("EGLL")
							.build();

		repo.saveAndFlush(location);
		// add a location for different user
		UUID locationUuidOther = UUID.randomUUID();
		UUID UuidDiffUser = UUID.randomUUID();
		GeographicLocationPersistentRecord otherLocation = GeographicLocationPersistentRecord.builder()
							.locationUuid(locationUuidOther)
							.userUuid(UuidDiffUser)
							.dateTimeAt(ZonedDateTime.now())
							.icaoCode("EGLL")
							.build();

		repo.saveAndFlush(otherLocation);
		locationUuids.add(locationUuidOther);
		// WHEN
		MvcResult result =
				this.mockMvc
				.perform(post("/api/v1/locations/Summaries")
						.header("x-auth-subject", userUuid)
						.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
						.content(objectMapper.writeValueAsString(locationUuids)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				//THEN
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[*].location_uuid", hasItems(locationUuids.get(0).toString(),locationUuids.get(1).toString(),locationUuids.get(2).toString()))).andReturn();
		//Check it doesn't contain other locations
		String response = result.getResponse().getContentAsString();
		assertFalse(response.contains(locationUuid.toString()));
		assertFalse(response.contains(locationUuids.get(3).toString()));

	}

	@Test
	public void bulkFetchShouldReturnEmptyArrayIfNoMatch() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid       = UUID.randomUUID();
		List<UUID> locationUuids = new ArrayList<>();
		for (int i=0; i<3 ; i++) {
			locationUuids.add(UUID.randomUUID());
		}
		// WHEN
		this.mockMvc
		.perform(post("/api/v1/locations/Summaries")
				.header("x-auth-subject", userUuid)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(objectMapper.writeValueAsString(locationUuids)))
		// THEN
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	public void bulkFetchShouldNotContainDuplicateData() throws Exception{
		// WITH
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();
		UUID locationUuid  = UUID.randomUUID();
		
		// add gar for user
		GeographicLocationPersistentRecord location = GeographicLocationPersistentRecord.builder()
							.locationUuid(locationUuid)
							.userUuid(userUuid)
							.dateTimeAt(ZonedDateTime.now())
							.icaoCode("EGLL")
							.build();


		repo.saveAndFlush(location);
		
		List<UUID> locationUuids = new ArrayList<>();
		// add same location uuid to request list
		locationUuids.add(locationUuid);
		locationUuids.add(locationUuid);
		// WHEN
		this.mockMvc
		.perform(post("/api/v1/locations/Summaries")
				.header("x-auth-subject", userUuid)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(objectMapper.writeValueAsString(locationUuids)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(1)));
	}

}
