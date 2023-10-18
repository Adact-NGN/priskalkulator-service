package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.service.cache.InMemory3DCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class MaterialPriceServiceImpl implements MaterialPriceService {

    private final MaterialPriceRepository repository;
    private final InMemory3DCache<String, String, MaterialPrice> materialPriceCache;
    
    @Autowired
    public MaterialPriceServiceImpl(MaterialPriceRepository repository,
                                    @Qualifier("materialPriceCache") InMemory3DCache<String, String, MaterialPrice> materialPriceCache) {
        this.repository = repository;
        this.materialPriceCache = materialPriceCache;
    }

    @Override
    public MaterialPrice save(MaterialPrice materialPrice) {
        MaterialPrice entity = new MaterialPrice();

        if(materialPrice.getId() != null) {
            Optional<MaterialPrice> optMaterialPrice = repository.findById(materialPrice.getId());

            if(optMaterialPrice.isPresent()) {
                entity = optMaterialPrice.get();
            }
        }

        entity.setMaterialNumber(materialPrice.getMaterialNumber());
        entity.setStandardPrice(materialPrice.getStandardPrice());

        return repository.save(entity);
    }

    @Override
    public Optional<MaterialPrice> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public MaterialPrice findByMaterialNumber(String materialNumber) {
        return repository.findByMaterialNumber(materialNumber);
    }

    @Override
    public List<MaterialPrice> findAll() {
        return repository.findAll();
    }
    
}
