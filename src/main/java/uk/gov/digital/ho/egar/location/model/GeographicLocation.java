package uk.gov.digital.ho.egar.location.model;

import java.time.ZonedDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.digital.ho.egar.location.model.validation.HasMandatoryFields;
import uk.gov.digital.ho.egar.location.model.validation.LocationValidator;


/**
 * This is the defined API model for the Rest API.
 * @see https://gist.github.com/yectep/4372d1166a192d5e9754
 */
//@HasMandatoryFields(message="Location does not contain the mandated location fields.")
public interface GeographicLocation {
	
	/**
	 * This is the time the flight is at this location.
	 */
	@JsonProperty("datetime")
	public ZonedDateTime getDateTimeAt();
	
	@Size(max = 13)
	@JsonProperty("ICAO")
	@Pattern(regexp = LocationValidator.ValidIcao.REGEX ,message=LocationValidator.ValidIcao.MSG)
	public String getIcaoCode();
	
	@Size(max = 13)
	@JsonProperty("IATA")
	@Pattern(regexp = LocationValidator.ValidIata.REGEX ,message=LocationValidator.ValidIata.MSG)
	public String getIataCode();
	
	@JsonProperty("point")
	public GeographicPoint getPoint();

	@JsonIgnore
	@Valid
	public Latlong getLatlong();

}
