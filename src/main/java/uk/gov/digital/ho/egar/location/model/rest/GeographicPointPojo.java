/**
 * 
 */
package uk.gov.digital.ho.egar.location.model.rest;

import lombok.Data;
import uk.gov.digital.ho.egar.location.model.GeographicPoint;
import uk.gov.digital.ho.egar.location.model.Latlong;

/**
 * Stores data coming in through the rest API.
 */
@Data
public class GeographicPointPojo implements GeographicPoint {

	private String latitude ;
	private String longitude ;

}
