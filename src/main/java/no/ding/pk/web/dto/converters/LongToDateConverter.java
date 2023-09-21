package no.ding.pk.web.dto.converters;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongToDateConverter extends StdConverter<String, Date> {

    private final Logger log = LoggerFactory.getLogger(LongToDateConverter.class);

    @Override
    public Date convert(String value) {
        if(StringUtils.isBlank(value)) {
            return null;
        }
        Pattern pattern = Pattern.compile("[/]Date[(](\\d+)[)][/]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        Long millis = null;
        if(matcher.find()) {
            String millisSrc = matcher.group(1);
            millis = Long.parseLong(millisSrc);
            return Date.from(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toInstant());
        } else {
            if(StringUtils.isNumeric(value)) {
                millis = Long.parseLong(value);
            }
        }

        if(millis == null) {
            return null;
        }

        return Date.from(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
