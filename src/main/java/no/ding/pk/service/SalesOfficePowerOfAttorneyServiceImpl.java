package no.ding.pk.service;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.repository.SalesOfficePowerOfAttorneyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalesOfficePowerOfAttorneyServiceImpl implements SalesOfficePowerOfAttorneyService {

    private final Logger log = LoggerFactory.getLogger(SalesOfficePowerOfAttorneyServiceImpl.class);

    private final SalesOfficePowerOfAttorneyRepository repository;

    @Autowired
    public SalesOfficePowerOfAttorneyServiceImpl(SalesOfficePowerOfAttorneyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PowerOfAttorney> findAll() {
        log.debug("Getting all PowerOfAttorneys");
        return repository.findAll();
    }

    @Override
    public PowerOfAttorney save(PowerOfAttorney poa) {
        log.debug("Persisting/Updating Power of Attorney");
        return repository.save(poa);
    }

    @Override
    public PowerOfAttorney findById(Long id) {
        log.debug("Getting entity for id {}", id);
        Optional<PowerOfAttorney> byId = repository.findById(id);

        return byId.orElse(null);
    }

    @Override
    public boolean delete(Long id) {
        repository.deleteById(id);

        Optional<PowerOfAttorney> byId = repository.findById(id);

        return byId.isEmpty();
    }

    @Override
    public List<PowerOfAttorney> findBySalesOfficeInList(List<Integer> salesOffices) {
        return repository.findAllBySalesOfficeIsIn(salesOffices);
    }
}
