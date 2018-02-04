package uk.gov.digital.ho.egar.location.model.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.h2.util.StringUtils;

import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.Latlong;

/**
 * Does the validation for a Location.
 */
public class LocationValidator implements ConstraintValidator<HasMandatoryFields, GeographicLocation> {

    /**
     * The ICAO (/ˌaɪˌkeɪˈoʊ/, eye-KAY-oh) airport code or location indicator is a four-letter code designating aerodromes around the world.
     * A utility class just to hold the validation.
     * Validation for
     * <pre>
     * {
     * "datetime":"YYYY-MM-DDThh:mm:ssZ",
     * "ICAO":"CODE"
     * }
     * </pre>
     *
     * @see https://en.wikipedia.org/wiki/ICAO_airport_code
     */
    public static class ValidIcao {
        public static final String REGEX = "^[0-9a-zA-Z\\-]{1,13}$";
        public static final String MSG = "Airport ICAO not four character Uppercase.";

        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            boolean state = true;
            if (!StringUtils.isNullOrEmpty(loc.getIcaoCode())
                    &&
                    !Pattern.matches(REGEX, loc.getIcaoCode())) {
                ctx
                        .buildConstraintViolationWithTemplate(MSG)
                        .addPropertyNode("icao")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }

            return state;
        }
    }
    
    /**
     * The IATA airport code or location identifier is a three-letter code designating airports around the world.
     * A utility class just to hold the validation.
     * Validation for
     * <pre>
     * {
     * "datetime":"YYYY-MM-DDThh:mm:ssZ",
     * "IATA":"CODE"
     * }
     * </pre>
     *
     * @see https://en.wikipedia.org/wiki/IATA_airport_code
     */
    public static class ValidIata {
        public static final String REGEX = "^[0-9a-zA-Z\\-]{1,13}$";
        public static final String MSG = "Airport IATA not three character Uppercase.";

        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            boolean state = true;
            if (!StringUtils.isNullOrEmpty(loc.getIataCode())
                    &&
                    !Pattern.matches(REGEX, loc.getIataCode())) {
                ctx
                        .buildConstraintViolationWithTemplate(MSG)
                        .addPropertyNode("iata")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }

            return state;
        }
    }

    /**
     * A utility class just to hold the validation.
     * Validation for:
     * {
     * "datetime":"YYYY-MM-DDThh:mm:ssZ",
     * "point":{
     * "latitude": some_number ,
     * "longitude": some_number ,
     * }
     * }
     */
    protected static class ValidLatLong {

        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            Latlong latlong = loc.getPoint();
            boolean state = true;

            if (latlong == null)
                // Nothing so this is ok
                return true;

            if (latlong.getLatitude() == null) {
                ctx
                        .buildConstraintViolationWithTemplate("latitude is null")
                        .addPropertyNode("latitude")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }
            if (latlong.getLongitude() == null) {
                ctx
                        .buildConstraintViolationWithTemplate("longitude is null")
                        .addPropertyNode("longitude")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }

            return state;
        }
    }

    public static class ValidLatitude {
        public static final String REGEX = "^(([NS]([0-8][0-9][0-5][0-9]|9000))|([+-]?([0-8]?[0-9].[0-9]{2}|90.00)))$";
        public static final String MSG = "Latitude does not match the decimal or degrees seconds formats.";

        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            boolean state = true;
            if (loc.getPoint() != null
                    && !StringUtils.isNullOrEmpty(loc.getPoint().getLatitude())
                    && !Pattern.matches(REGEX, loc.getPoint().getLatitude())) {
                ctx
                        .buildConstraintViolationWithTemplate(MSG)
                        .addPropertyNode("point.latitude")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }

            return state;
        }
    }

    public static class ValidLongitude {
        public static final String REGEX = "^(([EW](0?[0-9]{2}[0-5][0-9]|1[0-7][0-9][0-5][0-9]|18000))|([+-]?(0?[0-9]?[0-9].[0-9]{2}|1[0-7][0-9].[0-9][0-9]|180.00)))$";
        public static final String MSG = "Longitude does not match the decimal or degrees seconds formats.";

        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            boolean state = true;
            if (loc.getPoint() != null
                    && !StringUtils.isNullOrEmpty(loc.getPoint().getLongitude())
                    && !Pattern.matches(REGEX, loc.getPoint().getLongitude())) {
                ctx
                        .buildConstraintViolationWithTemplate(MSG)
                        .addPropertyNode("point.longitude")
                        .addBeanNode()
                        .addConstraintViolation();
                state = false;
            }

            return state;
        }
    }



    /**
     * A utility class just to hold the validation.
     */
    private static class LocationPresent {
        static boolean check(GeographicLocation loc, ConstraintValidatorContext ctx) {
            Latlong latlong = loc.getPoint();
            String icao = loc.getIcaoCode();

            return (latlong != null)
                    ||
                    (!StringUtils.isNullOrEmpty(icao));
        }
    }

    @Override
    public void initialize(HasMandatoryFields constraintAnnotation) {
        // Nothing to do
    }

    /**
     * Actually perform the validation.
     */
    @Override
    public boolean isValid(GeographicLocation loc, ConstraintValidatorContext ctx) {

        if (loc == null)
            // Nothing so this is ok
            return true;

        boolean isOk = LocationPresent.check(loc, ctx)
                && ValidIcao.check(loc, ctx)
                && ValidLatLong.check(loc, ctx);


        if (!isOk)
            ctx
                    .buildConstraintViolationWithTemplate("None of the locations are valid, Icao,Latlong or EastingNorthing")
                    .addBeanNode()
                    .addConstraintViolation();

        return isOk;
    }

}
