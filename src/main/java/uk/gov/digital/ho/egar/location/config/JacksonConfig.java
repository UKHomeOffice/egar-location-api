package uk.gov.digital.ho.egar.location.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import uk.gov.digital.ho.egar.location.adapter.GeographicLocationJsonDeserializer;
import uk.gov.digital.ho.egar.location.adapter.GeographicPointJsonDeserializer;
import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.model.GeographicPoint;

/**
 * Configuration of objects for use with jackson json conversion library.
 *
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates a configured object mapper.
     * Disables write dates as timestamps.
     * @param builder The Jackoson object mapper builder
     * @return The configured object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(final Jackson2ObjectMapperBuilder builder) {
        
    	ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // Configure JSON to return UTC dates
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Configure to convert Types
        SimpleModule module = new SimpleModule();
        module.addDeserializer(GeographicLocation.class, new GeographicLocationJsonDeserializer());
        module.addDeserializer(GeographicPoint.class, new GeographicPointJsonDeserializer());
        
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
}
