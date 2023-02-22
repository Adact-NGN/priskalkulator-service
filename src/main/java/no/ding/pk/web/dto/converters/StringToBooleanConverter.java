package no.ding.pk.web.dto.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.apache.commons.lang3.BooleanUtils;

public class StringToBooleanConverter extends StdConverter<String, Boolean> {


    @Override
    public Boolean convert(String value) {
        return BooleanUtils.toBoolean(value);
    }
}
