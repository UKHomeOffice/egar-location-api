package uk.gov.digital.ho.egar.location.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Similar to the GeoGraphic location but with an identifying Id.
 */
public interface GeographicLocationWithUuid extends GeographicLocation {

	@JsonProperty("location_uuid")
	UUID getLocationUuid();

	void setLocationUuid(UUID val);

	
	@JsonIgnore
	UUID getUserUuid() ;

	void setUserUuid(UUID val) ;

}
