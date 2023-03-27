package no.ding.pk.service.offer;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.offer.Terms;
import no.ding.pk.repository.offer.CustomerTermsRepository;

@Transactional
@Service
public class CustomerTermsServiceImpl implements CustomerTermsService {

    private CustomerTermsRepository repository;

    @Autowired
    public CustomerTermsServiceImpl(CustomerTermsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Terms save(Terms customerTerms) {
        return repository.save(customerTerms);
    }

    @Override
    public Optional<Terms> findById(Long id) {
        return repository.findById(id);
    }
    
}
