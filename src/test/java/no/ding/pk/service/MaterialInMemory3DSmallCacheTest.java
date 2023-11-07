package no.ding.pk.service;

import no.ding.pk.service.cache.InMemory3DCache;
import no.ding.pk.service.cache.PingInMemory3DCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Disabled("Need to isolate the scope")
@Tag("unittest")
@ActiveProfiles({ "test", "unit-test" })
public class MaterialInMemory3DSmallCacheTest {
    
    private InMemory3DCache<String, String, String> cache;

    @BeforeEach
    public void setup() {
        cache = new PingInMemory3DCache<>(2);
    }

    @Test
    public void shouldRemoveLeastRequestedObjectInCache() {
        cache.put("Ansatt", "kjetil", "kjetil.torvund.minde@ngn.no");
        cache.put("Ansatt", "eirik", "eirik.flaa@ngn.no");
        cache.get("Ansatt", "kjetil");
        cache.put("Ansatt", "jørgen", "jorgensq@ngn.no");
        cache.put("Merkevare", "Microsoft", "Microsoft");
        cache.put("Merkevare", "NGN", "NGN");
        cache.put("Merkevare", "Nintendo", "Nintendo");

        assertThat(cache.getAllInList("Ansatt"), hasSize(2));
    }
}
