package uk.gov.digital.ho.egar.location.model.validation;

import org.junit.Test;

import java.util.regex.Pattern;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class LocationValidatorTest {

    @Test
    public void testLatitudeRegex(){
        //Valid Decimal latitudes
        assertTrue(matchesLatitude("00.00"));
        assertTrue(matchesLatitude("0.00"));
        assertTrue(matchesLatitude("09.99"));
        assertTrue(matchesLatitude("9.99"));
        assertTrue(matchesLatitude("10.00"));
        assertTrue(matchesLatitude("89.99"));
        assertTrue(matchesLatitude("90.00"));
        assertTrue(matchesLatitude("+00.00"));
        assertTrue(matchesLatitude("+0.00"));
        assertTrue(matchesLatitude("+09.99"));
        assertTrue(matchesLatitude("+9.99"));
        assertTrue(matchesLatitude("+10.00"));
        assertTrue(matchesLatitude("+89.99"));
        assertTrue(matchesLatitude("+90.00"));
        assertTrue(matchesLatitude("-00.00"));
        assertTrue(matchesLatitude("-0.00"));
        assertTrue(matchesLatitude("-09.99"));
        assertTrue(matchesLatitude("-9.99"));
        assertTrue(matchesLatitude("-10.00"));
        assertTrue(matchesLatitude("-90.00"));
        assertTrue(matchesLatitude("-89.99"));

        //Valid Degree minute latitude
        assertTrue(matchesLatitude("N0000"));
        assertTrue(matchesLatitude("N8959"));
        assertTrue(matchesLatitude("N9000"));
        assertTrue(matchesLatitude("S0000"));
        assertTrue(matchesLatitude("S8959"));
        assertTrue(matchesLatitude("S9000"));

        //Invalid latitude decimal
        assertFalse(matchesLatitude("90.01"));
        assertFalse(matchesLatitude("+90.01"));
        assertFalse(matchesLatitude("-90.01"));
        assertFalse(matchesLatitude("10.1"));
        assertFalse(matchesLatitude("+10.1"));
        assertFalse(matchesLatitude("-10.1"));
        assertFalse(matchesLatitude("10.111"));
        assertFalse(matchesLatitude("+10.111"));
        assertFalse(matchesLatitude("-10.111"));

        //Invalid Degree minute latitude
        assertFalse(matchesLatitude("n0000"));
        assertFalse(matchesLatitude("s0000"));
        assertFalse(matchesLatitude("E0000"));
        assertFalse(matchesLatitude("N00000"));
        assertFalse(matchesLatitude("N000"));
        assertFalse(matchesLatitude("N9001"));
        assertFalse(matchesLatitude("S9001"));
        assertFalse(matchesLatitude("N0060"));
        assertFalse(matchesLatitude("S0060"));
    }

    @Test
    public void testLongitudeRegex(){
        //Longitude regex
        assertTrue(matchesLongitude("0.00"));
        assertTrue(matchesLongitude("00.00"));
        assertTrue(matchesLongitude("000.00"));
        assertTrue(matchesLongitude("+0.00"));
        assertTrue(matchesLongitude("+00.00"));
        assertTrue(matchesLongitude("+000.00"));
        assertTrue(matchesLongitude("-0.00"));
        assertTrue(matchesLongitude("-00.00"));
        assertTrue(matchesLongitude("-000.00"));
        assertTrue(matchesLongitude("9.99"));
        assertTrue(matchesLongitude("+9.99"));
        assertTrue(matchesLongitude("-9.99"));
        assertTrue(matchesLongitude("09.99"));
        assertTrue(matchesLongitude("+09.99"));
        assertTrue(matchesLongitude("-09.99"));
        assertTrue(matchesLongitude("009.99"));
        assertTrue(matchesLongitude("+009.99"));
        assertTrue(matchesLongitude("-009.99"));
        assertTrue(matchesLongitude("10.00"));
        assertTrue(matchesLongitude("+10.00"));
        assertTrue(matchesLongitude("-10.00"));
        assertTrue(matchesLongitude("010.00"));
        assertTrue(matchesLongitude("+010.00"));
        assertTrue(matchesLongitude("-010.00"));
        assertTrue(matchesLongitude("99.99"));
        assertTrue(matchesLongitude("+99.99"));
        assertTrue(matchesLongitude("-99.99"));
        assertTrue(matchesLongitude("099.99"));
        assertTrue(matchesLongitude("+099.99"));
        assertTrue(matchesLongitude("-099.99"));
        assertTrue(matchesLongitude("100.00"));
        assertTrue(matchesLongitude("+100.00"));
        assertTrue(matchesLongitude("-100.00"));
        assertTrue(matchesLongitude("179.99"));
        assertTrue(matchesLongitude("-179.99"));
        assertTrue(matchesLongitude("+179.99"));
        assertTrue(matchesLongitude("180.00"));
        assertTrue(matchesLongitude("+180.00"));
        assertTrue(matchesLongitude("-180.00"));

        //Valid Longitude Degree minute seconds
        assertTrue(matchesLongitude("E0000"));
        assertTrue(matchesLongitude("E9959"));
        assertTrue(matchesLongitude("E10000"));
        assertTrue(matchesLongitude("E17959"));
        assertTrue(matchesLongitude("E18000"));
        assertTrue(matchesLongitude("W0000"));
        assertTrue(matchesLongitude("W9959"));
        assertTrue(matchesLongitude("W10000"));
        assertTrue(matchesLongitude("W17959"));
        assertTrue(matchesLongitude("W18000"));

        //Invalid latitude decimal
        assertFalse(matchesLongitude("180.01"));
        assertFalse(matchesLongitude("+180.01"));
        assertFalse(matchesLongitude("-180.01"));
        assertFalse(matchesLongitude("10.1"));
        assertFalse(matchesLongitude("+10.1"));
        assertFalse(matchesLongitude("-10.1"));
        assertFalse(matchesLongitude("10.111"));
        assertFalse(matchesLongitude("+10.111"));
        assertFalse(matchesLongitude("-10.111"));

        //Invalid Degree minute latitude
        assertFalse(matchesLongitude("e0000"));
        assertFalse(matchesLongitude("w0000"));
        assertFalse(matchesLongitude("S0000"));
        assertFalse(matchesLongitude("W000000"));
        assertFalse(matchesLongitude("W000"));
        assertFalse(matchesLongitude("W18001"));
        assertFalse(matchesLongitude("E18001"));
        assertFalse(matchesLongitude("W0060"));
        assertFalse(matchesLongitude("E0060"));
    }

    private boolean matchesLatitude(String value){
        return Pattern.matches(LocationValidator.ValidLatitude.REGEX, value);
    }

    private boolean matchesLongitude(String value){
        return Pattern.matches(LocationValidator.ValidLongitude.REGEX, value);
    }
}
