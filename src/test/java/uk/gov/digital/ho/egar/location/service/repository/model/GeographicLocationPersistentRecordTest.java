package uk.gov.digital.ho.egar.location.service.repository.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeographicLocationPersistentRecordTest {

	private static ObjectMapper mapper ;
	
	@BeforeClass
	public static void initMapper()
	{
		mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
		mapper.findAndRegisterModules();
	}
	
	@Test
	public void parseUTC()
	{
		 String ts = "2016-09-12T13:15:17.309Z";
	        ZonedDateTime parse = ZonedDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME);
	        System.out.println(parse);
	        System.out.println(parse.toLocalDateTime());
		     assertThat(parse.toString()).isEqualTo("2016-09-12T13:15:17.309Z");
		     assertThat(parse.toLocalDateTime().toString()).isEqualTo("2016-09-12T13:15:17.309");
	}
	
	@Test
	public void parseUTCNoSeconds()
	{
		 String ts = "2016-09-12T13:15Z";
	     ZonedDateTime parse = ZonedDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME);
	     
	     assertThat(parse.toString()).isEqualTo("2016-09-12T13:15Z");
	     assertThat(parse.toLocalDateTime().toString()).isEqualTo("2016-09-12T13:15");
	}

	@Test
	public void parseUTCWithOffsetNoSeconds()
	{
		 String ts = "2016-09-12T13:15+02:00";
	     ZonedDateTime parse = ZonedDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME);
	     
	     assertThat(parse.toString()).isEqualTo("2016-09-12T13:15+02:00");
	     assertThat(parse.toLocalDateTime().toString()).isEqualTo("2016-09-12T13:15");
	}
	@Test
	public void canCreateFromJsonTimeWithoutSeconds() throws Exception {

		String jsonLocation = "{\"datetime\":\"2017-12-15T11:46Z\",\"ICAO\":\"EGLL\"}";
		
		GeographicLocationPersistentRecord readObj = mapper.readValue(jsonLocation,GeographicLocationPersistentRecord.class);
		
		assertThat(readObj.getIcaoCode()).isEqualTo("EGLL");
		assertThat(readObj.getDateTimeAt()).isNotNull();
		
	}
	
	
	

	@Test
	public void canCreateFromJsonUtcTimeWithoutMilliSecondsAndUtcTimeZone() throws Exception {

		String jsonLocation = "{\"datetime\":\"2017-12-15T07:30:08Z\",\"ICAO\":\"EGLL\"}";
		
		GeographicLocationPersistentRecord readObj = mapper.readValue(jsonLocation,GeographicLocationPersistentRecord.class);
		
		assertThat(readObj.getIcaoCode()).isEqualTo("EGLL");
		assertThat(readObj.getDateTimeAt()).isNotNull();
		
	}
	
	@Test
	public void canCreateFromJsonUtcTimeWithoutSecondsAndWithTimeZone() throws Exception {

		String jsonLocation = "{\"datetime\":\"2017-12-15T11:46Z\",\"ICAO\":\"EGLL\"}";
		
		GeographicLocationPersistentRecord readObj = mapper.readValue(jsonLocation,GeographicLocationPersistentRecord.class);
		
		assertThat(readObj.getIcaoCode()).isEqualTo("EGLL");
		assertThat(readObj.getDateTimeAt()).isNotNull();
		
	}
	
	@Test
	public void canCreateJsonAndReRead() throws Exception {

		GeographicLocationPersistentRecord newloc 
				= GeographicLocationPersistentRecord.builder()
				.dateTimeAt(ZonedDateTime.now()).icaoCode("EGLL").build();
		
		String jsonLocation = mapper.writeValueAsString(newloc);
		
		GeographicLocationPersistentRecord readObj = mapper.readValue(jsonLocation,GeographicLocationPersistentRecord.class);
		
		assertThat(readObj.getIcaoCode()).isEqualTo("EGLL");
		assertThat(readObj.getDateTimeAt()).isNotNull();
		
	}

	
	

}
