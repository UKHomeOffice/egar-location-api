package uk.gov.digital.ho.egar.location.api.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.location.api.LocationsRestAPI;
import uk.gov.digital.ho.egar.location.api.PathConstants;
import uk.gov.digital.ho.egar.location.api.exceptions.BadOperationLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.DataNotFoundLocationApiException;
import uk.gov.digital.ho.egar.location.api.exceptions.LocationApiException;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicLocationWithUuid;
import uk.gov.digital.ho.egar.location.service.GeographicLocationService;
import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;

@RestController
@RequestMapping(PathConstants.ROOT_PATH)
@Api(value = PathConstants.ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class GeographicLocationRestController implements LocationsRestAPI {

	private static final String USER_HEADER_NAME = "x-auth-subject";

	private final GeographicLocationService geoService;

	public GeographicLocationRestController(@Autowired GeographicLocationService geoService) {
		super();
		this.geoService = geoService;
	}

	/**
	 * A quick & dirty fix to change 400 due to a ServletRequestBindingException to
	 * a 401. 99% of expected ServletRequestBindingException will be due to a
	 * missing header.
	 */
	@ExceptionHandler(ServletRequestBindingException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public @ResponseBody LocationApiException handleException(ServletRequestBindingException ex) {
		return new LocationApiException(ex.getMessage());
	}

	/**
	 * POST - To add to the Location.
	 */
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 303, message = "See other location when sucessfull"),
			@ApiResponse(code = 401, message = "unauthorised") })
	@Override
	@ResponseStatus(HttpStatus.SEE_OTHER)
	public ResponseEntity<Void> addLocation(@RequestHeader(USER_HEADER_NAME) UUID userUuid,
			@Valid @RequestBody GeographicLocation newLoc, Errors errors) throws LocationApiException {

		// EGAR 1388
		if (errors.hasErrors()) {
			return new ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
		}

		GeographicLocationWithUuid savedLoc = this.geoService.addLocation(userUuid, newLoc);

		// Creating the redirection location URI
		URI redirectLocation = getLocationURI(savedLoc);

		// Creating the response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);

		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 * GET - The location.
	 */
	@Override
	@GetMapping(path = PathConstants.PATH_LOCATIONS
			+ PathConstants.PATH_LOCATION, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 401, message = "unauthorised") })
	public GeographicLocationWithUuid getLocation(@RequestHeader(USER_HEADER_NAME) UUID userUuid,
			@PathVariable(PathConstants.PATH_LOCATION_VAR) UUID locationUuid) throws DataNotFoundLocationApiException {

		return this.geoService.getLocation(userUuid, locationUuid);

	}

	/**
	 * The actual Update.
	 */
	@PostMapping(path = PathConstants.PATH_LOCATIONS + PathConstants.PATH_LOCATION)
	@ApiResponses(value = { @ApiResponse(code = 303, message = "See other location when sucessfull"),
			@ApiResponse(code = 401, message = "unauthorised") })
	@Override
	@ResponseStatus(HttpStatus.SEE_OTHER)
	public ResponseEntity<Void> updateLocation(@RequestHeader(USER_HEADER_NAME) UUID userUuid,
			@PathVariable(PathConstants.PATH_LOCATION_VAR) UUID locationUuid, @Valid @RequestBody GeographicLocation location, Errors errors)
			throws LocationApiException {

		// EGAR 1388
		if (errors.hasErrors()) {
			return new ResponseEntity(new ApiErrors(errors), HttpStatus.BAD_REQUEST);
		}

		GeographicLocationWithUuid savedLoc = this.geoService
				.updateLocation(validateAndSetUuids(userUuid, locationUuid, location));

		// Creating the redirection location URI
		URI redirectLocation = getLocationURI(savedLoc);

		// Creating the response headers
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);

		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
	}
	
	//---------------------------------------------------------------------------------------------------------
    
    /**
     * A get endpoint that bulk retrieves a list of locations
     */
    
    
    @Override
    @ApiOperation(value = "Bulk retrieve a list of locations.",
            notes = "Retrieve a list of existing location	 for a user")
    @ApiResponses(value = {
    		@ApiResponse(
                    code = 200,
                    message = "Successful retrieval",
                    response = GeographicLocationWithUuid[].class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorised")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = PathConstants.PATH_BULK,
    			consumes = MediaType.APPLICATION_JSON_VALUE,
           		produces = MediaType.APPLICATION_JSON_VALUE)
    public GeographicLocationWithUuid[] bulkRetrieveLocations(@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    									   					  @RequestBody List<UUID> locationUuids) {
    	
    	return this.geoService.getBulkLocations(uuidOfUser,locationUuids);
    }

	private GeographicLocationWithUuid validateAndSetUuids(UUID userUuid, UUID locationUuid,
			GeographicLocation location) throws BadOperationLocationApiException {

		GeographicLocationWithUuid retLoc = null;

		if (location instanceof GeographicLocationWithUuid) {
			retLoc = (GeographicLocationWithUuid) location;

			if (retLoc.getUserUuid() == null) {
				retLoc.setUserUuid(userUuid);
			} else if (!userUuid.equals(retLoc.getUserUuid())) {
				throw new BadOperationLocationApiException("Provided object does not have the same user UUID.");
			}

			if (retLoc.getLocationUuid() == null) {
				retLoc.setLocationUuid(locationUuid);
			} else if (!locationUuid.equals(retLoc.getLocationUuid())) {
				throw new BadOperationLocationApiException("Provided object does not have the same location UUID.");
			}
		} else {
			// Populate an object to hold the data
			// A little naughty by it should be ok.
			retLoc = new GeographicLocationPersistentRecord().copy(location);
		}

		return retLoc;
	}

	/**
	 * Gets the gar URI from the provided gar id
	 * 
	 * @param garId
	 *            the gar uuid.
	 * @return The gar URI
	 * @throws LocationApiException
	 * @throws URISyntaxException
	 *             When unable to construct a valid URI
	 */
	private URI getLocationURI(final GeographicLocationWithUuid loc) throws LocationApiException {

		try {
			return new URI(PathConstants.ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + loc.getLocationUuid()
					+ ServicePathConstants.ROOT_PATH_SEPERATOR);
		} catch (URISyntaxException ex) {
			throw new LocationApiException(ex);
		}
	}

	public static class ApiErrors {

		@JsonProperty("message")
		private final List<String> errorMessages = new ArrayList<>();

		public ApiErrors(Errors errors) {
			for (final FieldError error : errors.getFieldErrors()) {
				errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
			}
			for (final ObjectError error : errors.getGlobalErrors()) {
				errorMessages.add(error.getObjectName() + ": " + error.getDefaultMessage());
			}
		}
	}

}
