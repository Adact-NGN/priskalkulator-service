package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialPriceRepository;

@Transactional
@Service
public class MaterialPriceServiceImpl implements MaterialPriceService {

    private MaterialPriceRepository repository;
    
    @Autowired
    public MaterialPriceServiceImpl(MaterialPriceRepository repository) {
        this.repository = repository;
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
        // TODO Auto-generated method stub
        return repository.findAll();
    }
    
}
