package no.ding.pk.web.deserialzers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import no.ding.pk.domain.DiscountLevel;

import java.io.IOException;

public class DiscountLevelDeserializer extends StdDeserializer<DiscountLevel> {

    protected DiscountLevelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DiscountLevel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }
}
