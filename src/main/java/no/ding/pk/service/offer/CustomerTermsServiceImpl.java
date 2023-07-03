package no.ding.pk.service.offer;

import no.ding.pk.domain.offer.CustomerTerms;
import no.ding.pk.repository.offer.CustomerTermsRepository;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static no.ding.pk.repository.specifications.CustomerTermsSpecifications.*;

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
    public CustomerTerms save(String salesOffice, String customerNumber, CustomerTerms customerTerms) {
        List<CustomerTerms> currentCustomerTerms = repository.findAllBySalesOfficeAndCustomerNumber(salesOffice, customerNumber);
        
        invalidatePreviousCustomerTerm(currentCustomerTerms, customerTerms.getAgreementStartDate());
        
        CustomerTerms newTerm = repository.save(customerTerms);
        
        log.debug("Created new Term for customer: {}", newTerm);
        return newTerm;
    }
    
    @Override
    public List<CustomerTerms> findAll(String salesOffice, String customerNumber) {
        return repository.findAll(Specification.where(withSalesOffice(salesOffice).and(withCustomerNumber(customerNumber))));
    }
    
    private void invalidatePreviousCustomerTerm(List<CustomerTerms> currentCustomerTerms, Date agreementStartDate) {
        currentCustomerTerms.stream().filter(terms -> terms.getAgreementEndDate() == null || terms.getAgreementEndDate().equals(new Date())).forEach(terms -> {
            LocalDateTime localDateTime = new LocalDateTime(agreementStartDate);
            terms.setAgreementEndDate(localDateTime.minusDays(1).toDate());
            repository.save(terms);
        });
    }
    
    @Override
    public Optional<CustomerTerms> findById(Long id) {
        return repository.findById(id);
    }
    
    @Override
    public CustomerTerms findActiveTermsForCustomerForSalesOfficeAndSalesOrg(String customerNumber, String salesOffice, String salesOrg) {
        List<CustomerTerms> allActiveCustomerTerms = repository.findBySalesOrgAndSalesOfficeAndCustomerNumberAndAgreementEndDateGreaterThanOrAgreementEndDateIsNullOrderByCreatedDateDesc(salesOrg, salesOffice, customerNumber, new Date());

        return allActiveCustomerTerms.get(0);
    }

    @Override
    public List<CustomerTerms> findAllActive(String salesOffice, String customerNumber) {
        return repository.findAll(Specification.where(withSalesOffice(salesOffice).and(withCustomerNumber(customerNumber)).and(withAgreementEndDateGreaterThan(null))));
    }

}
