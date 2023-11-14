package no.ding.pk.web.dto.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class DoubleToStringSerializer extends StdSerializer<Double> {

    protected DoubleToStringSerializer(Class<Double> t) {
        super(t);
    }

    protected DoubleToStringSerializer(JavaType type) {
        super(type);
    }

    protected DoubleToStringSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected DoubleToStringSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(String.format("%.2f", value));
    }
}
