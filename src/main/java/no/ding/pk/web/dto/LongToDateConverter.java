package no.ding.pk.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.util.StdConverter;

public class LongToDateConverter extends StdConverter<String, Date> {

    @Override
    public Date convert(String value) {
        Pattern pattern = Pattern.compile("[/]Date[(](\\d+)[)][/]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        String millisSrc = "";
        if(matcher.find()) {
            millisSrc = matcher.group(1);
        }
        Long millis = Long.parseLong(millisSrc);
        LocalDateTime localDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Date.from(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
