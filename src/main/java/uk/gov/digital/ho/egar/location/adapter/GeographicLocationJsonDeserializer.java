package uk.gov.digital.ho.egar.location.adapter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.gov.digital.ho.egar.location.model.GeographicLocation;
import uk.gov.digital.ho.egar.location.service.repository.model.GeographicLocationPersistentRecord;

/**
 * A very basic serializer that creates a POJO for all interfaces that derive from GeographicLocation.
 * Turns JSON into the correct object type.
 */
public class GeographicLocationJsonDeserializer extends JsonDeserializer<GeographicLocation> {

	@Override
	public GeographicLocation deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		 ObjectMapper mapper = (ObjectMapper) jp.getCodec();
         ObjectNode root = mapper.readTree(jp);
         return mapper.readValue(root.toString(), GeographicLocationPersistentRecord.class);
	}

}
