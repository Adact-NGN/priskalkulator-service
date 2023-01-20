package no.ding.pk.service.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.repository.offer.SalesOfficeRepository;
import no.ding.pk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PriceOfferServiceImpl implements PriceOfferService {

    private static final Logger log = LoggerFactory.getLogger(PriceOfferServiceImpl.class);

    private final PriceOfferRepository repository;

    private final SalesOfficeService salesOfficeService;

    private final UserService userService;

    private final CustomerTermsService customerTermsService;

    @Autowired
    public PriceOfferServiceImpl(PriceOfferRepository repository,
                                 SalesOfficeService salesOfficeService,
                                 UserService userService,
                                 CustomerTermsService customerTermsService) {
        this.repository = repository;
        this.salesOfficeService = salesOfficeService;
        this.userService = userService;
        this.customerTermsService = customerTermsService;
    }

    private PriceOffer createNewPriceOffer(User salesEmployee) {
        PriceOffer entity = new PriceOffer();
        entity.setSalesEmployee(salesEmployee);

        return repository.save(entity);
    }

    @Override
    public PriceOffer save(PriceOffer newPriceOffer) {
        log.debug("Received PriceOffer {}", newPriceOffer);

        if(newPriceOffer.getSalesEmployee() != null) {
            log.debug("Sales Employee object provided with email: {}", newPriceOffer.getSalesEmployee().getEmail());
            User salesEmployee = checkUserObject(newPriceOffer.getSalesEmployee());
            log.debug("User: {}", salesEmployee);

            if (salesEmployee == null) {
                // TODO: Create own exception
                throw new RuntimeException("No sales employee provided!");
            }
        }

        PriceOffer entity;

        if(newPriceOffer.getId() != null) {
            Optional<PriceOffer> optEntity = repository.findById(newPriceOffer.getId());

            if(optEntity.isPresent()) {
                entity = optEntity.get();
            } else {
                entity = createNewPriceOffer(newPriceOffer.getSalesEmployee());
            }
        } else {
            entity = createNewPriceOffer(newPriceOffer.getSalesEmployee());
        }

        entity.setCustomerNumber(newPriceOffer.getCustomerNumber());
        if(newPriceOffer.getCustomerName() != null) {
            entity.setCustomerName(newPriceOffer.getCustomerName());
        }
        entity.setNeedsApproval(newPriceOffer.getNeedsApproval());
        entity.setIsApproved(newPriceOffer.getIsApproved());
        entity.setApprovalDate(newPriceOffer.getApprovalDate());
        entity.setDateIssued(newPriceOffer.getDateIssued());

        if(newPriceOffer.getSalesOfficeList() != null) {
            // Use traditional for-loop to avoid Concurrency Exception
            if(newPriceOffer.getSalesOfficeList().size() > 0) {
                List<SalesOffice> salesOffices = salesOfficeService.saveAll(newPriceOffer.getSalesOfficeList());

                entity.setSalesOfficeList(salesOffices);
                entity = repository.save(entity);
            }
        }

        if(newPriceOffer.getApprover() != null) {
            User approver = checkUserObject(newPriceOffer.getApprover());

            if(approver != null) {
                entity.setApprover(approver);
            }
        }

        if(newPriceOffer.getCustomerTerms() != null) {
            Terms customerTerms = customerTermsService.save(newPriceOffer.getCustomerTerms());

            entity.setCustomerTerms(customerTerms);
        }

        return repository.save(entity);
    }

    private User checkUserObject(User user) {
        User salesEmployee = userService.findByEmail(user.getEmail());
        log.debug("Result from service: {}", salesEmployee);

        return salesEmployee;
    }

    @Override
    public Optional<PriceOffer> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<PriceOffer> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean delete(Long id) {
        Optional<PriceOffer> priceOffer = repository.findById(id);

        if(priceOffer.isEmpty()) {
            log.debug("Could not find PriceOffer with id: {}", id);
            return false;
        }

        repository.delete(priceOffer.get());

        return !repository.existsById(id);
    }

}
