package no.ding.pk.utils;

import org.apache.commons.lang3.StringUtils;

public class ZoneUtils {
    public static String getFormattedZone(String zone) {
        if(!StringUtils.isNumeric(zone)) {
            throw new RuntimeException("Received a non numeric string, received: " + zone);
        }

        Integer value = Integer.valueOf(zone);

        if(0 < value && value < 10) {
            return String.format("0%d", value);
        } else {
            return String.valueOf(value);
        }
    }
}
