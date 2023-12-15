package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.MaterialPrice;
import no.ding.pk.repository.offer.MaterialPriceRepository;
import no.ding.pk.service.cache.InMemory3DCache;
import org.apache.commons.lang3.StringUtils;
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
        entity.setSalesOrg(materialPrice.getSalesOrg());
        entity.setSalesOffice(materialPrice.getSalesOffice());
        entity.setZone(materialPrice.getZone());

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

    @Override
    public Optional<MaterialPrice> findBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndSalesZone(String salesOrg, String salesOffice, String materialNumber, String deviceType, String salesZone) {
        String salesOrgToUse = StringUtils.isNotBlank(salesOrg) ? salesOrg : "";
        String salesOfficeToUse = StringUtils.isNotBlank(salesOffice) ? salesOffice : "";
        String zone = StringUtils.isNotBlank(salesZone) ? salesZone : "";
        String deviceTypeToUse = StringUtils.isNotBlank(deviceType) ? deviceType : "";

        return repository.findMaterialPriceBySalesOrgAndSalesOfficeAndMaterialNumberAndDeviceTypeAndZone(
                salesOrgToUse, salesOfficeToUse, materialNumber, deviceTypeToUse, zone);
    }

}
