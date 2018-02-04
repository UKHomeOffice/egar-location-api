package uk.gov.digital.ho.egar.location.api;

import uk.gov.digital.ho.egar.constants.ServicePathConstants;

public interface PathConstants {

	String ROOT_SERVICE_NAME = "locations";
	String ROOT_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.ROOT_SERVICE_API
						+ ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.SERVICE_VERSION_ONE
						+ ServicePathConstants.ROOT_PATH_SEPERATOR + ROOT_SERVICE_NAME;
	String PATH_LOCATIONS = "/";
	
	String PATH_LOCATION_VAR = "location_uuid";
	String PATH_LOCATION = "{" + PATH_LOCATION_VAR + "}";

}