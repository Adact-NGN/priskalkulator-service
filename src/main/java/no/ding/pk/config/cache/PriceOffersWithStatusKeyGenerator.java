package no.ding.pk.config.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class PriceOffersWithStatusKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        Object[] nonNulls = Arrays.stream(params).filter(Objects::nonNull).toList().toArray();
        return StringUtils.arrayToDelimitedString(nonNulls, "_");
    }
}
