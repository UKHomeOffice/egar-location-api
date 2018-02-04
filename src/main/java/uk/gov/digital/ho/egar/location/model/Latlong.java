package uk.gov.digital.ho.egar.location.model;


import uk.gov.digital.ho.egar.location.model.validation.LocationValidator;

import javax.validation.constraints.Pattern;

/**
 * This holds the Latlong.
 *
 * @see https://en.wikipedia.org/wiki/Geographic_coordinate_system
 */
public interface Latlong {

    @Pattern(regexp = LocationValidator.ValidLatitude.REGEX ,message=LocationValidator.ValidLatitude.MSG)
    public String getLatitude();

    @Pattern(regexp = LocationValidator.ValidLongitude.REGEX ,message=LocationValidator.ValidLongitude.MSG)
    public String getLongitude();
}
