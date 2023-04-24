package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.domain.offer.CustomerTermsPK;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.repository.offer.CustomerTermsRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CustomerTermsServiceImpl implements CustomerTermsService {

    private static final Logger log = LoggerFactory.getLogger(CustomerTermsServiceImpl.class);

    private final CustomerTermsRepository repository;

    @Autowired
    public CustomerTermsServiceImpl(CustomerTermsRepository repository) {
        this.repository = repository;
    }

    public CustomerTerms update(CustomerTerms customerTerms) {
        return repository.save(customerTerms);
    }

    @Override
    public List<CustomerTerms> findAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber) {
        return repository.findAllBySalesOfficeAndCustomerNumber(salesOffice, customerNumber);
    }

    @Override
    public Integer countAllBySalesOfficeAndCustomerNumber(String salesOffice, String customerNumber) {
        return repository.countAllBySalesOfficeAndCustomerNumber(salesOffice, customerNumber);
    }

    @Override
    public CustomerTerms save(SalesOffice salesOffice, String customerNumber, CustomerTerms customerTerms) {
        List<CustomerTerms> currentCustomerTerms = repository.findAllBySalesOfficeAndCustomerNumber(salesOffice.getSalesOffice(), customerNumber);

        invalidatePreviousCustomerTerm(currentCustomerTerms);

        CustomerTermsPK termsPK = createTermsPK(salesOffice, customerNumber, currentCustomerTerms);

        ModelMapper modelMapper = new ModelMapper();
        CustomerTerms newTerm = modelMapper.map(customerTerms, CustomerTerms.class);
        newTerm.setId(termsPK);

        newTerm = repository.save(newTerm);

        log.debug("Created new Term for customer: {}", newTerm);
        return newTerm;
    }

    private static CustomerTermsPK createTermsPK(SalesOffice salesOffice, String customerNumber, List<CustomerTerms> currentCustomerTerms) {
        CustomerTermsPK customerTermsPK = new CustomerTermsPK();
        customerTermsPK.setSalesOfficeId(Long.parseLong(salesOffice.getSalesOffice()));
        customerTermsPK.setCustomerId(Long.parseLong(customerNumber));
        customerTermsPK.setSerialNumber(currentCustomerTerms.size());
        return customerTermsPK;
    }

    private void invalidatePreviousCustomerTerm(List<CustomerTerms> currentCustomerTerms) {
        currentCustomerTerms.stream().filter(terms -> terms.getAgreementEndDate() == null || terms.getAgreementEndDate().equals(new Date())).forEach(terms -> {
            terms.setAgreementEndDate(new Date());
            repository.save(terms);
        });
    }

    @Override
    public Optional<CustomerTerms> findById(Long id) {
        return repository.findById(id);
    }
    
}
