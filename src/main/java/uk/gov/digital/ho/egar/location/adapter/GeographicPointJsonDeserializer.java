package uk.gov.digital.ho.egar.location.adapter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.gov.digital.ho.egar.location.model.GeographicPoint;
import uk.gov.digital.ho.egar.location.model.rest.GeographicPointPojo;

/**
 * A very basic serializer that creates a POJO for all interfaces that derive from GeographicLocation.
 * Turns JSON into the correct object type.
 */
public class GeographicPointJsonDeserializer extends JsonDeserializer<GeographicPoint> {

	@Override
	public GeographicPoint deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		 ObjectMapper mapper = (ObjectMapper) jp.getCodec();
         ObjectNode root = mapper.readTree(jp);
         return mapper.readValue(root.toString(), GeographicPointPojo.class);
	}

}
