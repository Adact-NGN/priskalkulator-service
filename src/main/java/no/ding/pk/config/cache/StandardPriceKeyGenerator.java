package no.ding.pk.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class StandardPriceKeyGenerator implements KeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(StandardPriceKeyGenerator.class);

    @Override
    public Object generate(Object target, Method method, Object... params) {
        Object[] nonNulls = Arrays.stream(params).filter(Objects::nonNull).toList().toArray();
        return StringUtils.arrayToDelimitedString(nonNulls, "_");
    }
}
