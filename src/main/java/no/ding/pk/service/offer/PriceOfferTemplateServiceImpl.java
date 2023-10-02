package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.repository.offer.PriceOfferTemplateRepository;
import no.ding.pk.web.handlers.PriceOfferTemplateNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PriceOfferTemplateServiceImpl implements PriceOfferTemplateService {

    private final PriceOfferTemplateRepository repository;
    
    @Autowired
    public PriceOfferTemplateServiceImpl(PriceOfferTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public PriceOfferTemplate save(PriceOfferTemplate newTemplate) {
        return repository.save(newTemplate);
    }

    @Override
    public List<PriceOfferTemplate> findAll() {
        return repository.findAll();
    }

    @Override
    public PriceOfferTemplate findById(Long id) {
        Optional<PriceOfferTemplate> byId = repository.findById(id);

        if(byId.isEmpty()) {
            throw new PriceOfferTemplateNotFound();
        }

        return byId.get();
    }
}
