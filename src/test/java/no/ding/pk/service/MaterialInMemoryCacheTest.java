package no.ding.pk.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.PropertyResolver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import no.ding.pk.config.SchedulingTestConfig;

@ActiveProfiles({ "test" })
@TestPropertySource(properties = {"cache.ttl=1000", "cache.max.amount.items=5000"})
@SpringJUnitConfig(SchedulingTestConfig.class)
public class MaterialInMemoryCacheTest {
    
    @Autowired
    private MaterialInMemoryCache<String, String, String> cache;

    @Autowired private PropertyResolver propertySourceResolver;

    @Test
    public void shouldGetTimeToLiveProperty() {
        String testTtlProperty = propertySourceResolver.getProperty("cache.ttl");

        assertThat(testTtlProperty, is("1000"));
    }

    @Test
    public void shouldAddAndRemoveObjects() {
        cache.put("Ansatt", "kjetil", "kjetil.torvund.minde@ngn.no");
        cache.put("Ansatt", "eirik", "eirik.flaa@ngn.no");
        cache.put("Ansatt", "jørgen", "jorgensq@ngn.no");
        cache.put("Merkevare", "Microsoft", "Microsoft");
        cache.put("Merkevare", "NGN", "NGN");
        cache.put("Merkevare", "Nintendo", "Nintendo");

        System.out.println("3 Cache Object of 'Ansatt' Added.. cache.size(): " + cache.size("Ansatt"));
        System.out.println("3 Cache Object of 'Merkevare' Added.. cache.size(): " + cache.size("Merkevare"));
        cache.remove("Merkevare", "IBM");
        System.out.println("One object of 'Merkevare' removed.. cache.size(): " + cache.size("Merkevare"));

        cache.put("Merkevare", "PS", "PS");
        cache.put("Merkevare", "Xbox", "Xbox");
        System.out.println("Two objects added to 'Merkevare'.. cache.size(): " + cache.size("Merkevare"));
    }

    @Test
    public void shouldBeEmtpyWhenTimeToLiveExpired() throws InterruptedException {
        cache.put("Ansatt", "kjetil", "kjetil");
        cache.put("Ansatt", "eirik", "eirik");

        System.out.println("Cache size is: " + cache.size("Ansatt"));

        Thread.sleep(10000);

        System.out.println("Two objects added but timeToLive was reached. cache.size(): " + cache.size("Ansatt"));

        assertThat(cache.size("Ansatt"), is(0));
    }
}
