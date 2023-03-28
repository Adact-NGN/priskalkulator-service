package no.ding.pk.service;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.repository.SalesOfficePowerOfAttorneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesOfficePowerOfAttorneyServiceImpl implements SalesOfficePowerOfAttorneyService {

    private final SalesOfficePowerOfAttorneyRepository repository;

    @Autowired
    public SalesOfficePowerOfAttorneyServiceImpl(SalesOfficePowerOfAttorneyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PowerOfAttorney> findAll() {
        return repository.findAll();
    }
}
