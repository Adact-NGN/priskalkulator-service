package no.ding.pk.service.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.UserService;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public PriceOfferServiceImpl(PriceOfferRepository repository,
                                 SalesOfficeService salesOfficeService,
                                 UserService userService) {
        this.repository = repository;
        this.salesOfficeService = salesOfficeService;
        this.userService = userService;
    }

    private PriceOffer createNewPriceOffer(User salesEmployee) {
        PriceOffer entity = new PriceOffer();
        entity.setSalesEmployee(salesEmployee);

        return repository.save(entity);
    }

    @Override
    public PriceOffer save(PriceOffer newPriceOffer) {
        log.debug("Received PriceOffer {}", newPriceOffer);

        User salesEmployee = checkAndGetSalesEmployee(newPriceOffer.getSalesEmployee());

        PriceOffer entity;

        if(newPriceOffer.getId() != null) {
            Optional<PriceOffer> optEntity = repository.findById(newPriceOffer.getId());

            entity = optEntity.orElseGet(() -> createNewPriceOffer(salesEmployee));
        } else {
            entity = createNewPriceOffer(salesEmployee);
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
            if(newPriceOffer.getSalesOfficeList().size() > 0) {
                List<SalesOffice> salesOffices = salesOfficeService.saveAll(newPriceOffer.getSalesOfficeList(), entity.getCustomerNumber());

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
            entity.setCustomerTerms(newPriceOffer.getCustomerTerms());
        }

        return repository.save(entity);
    }

    private User checkAndGetSalesEmployee(User salesEmployee) {
        if (salesEmployee == null) {
            throw new EmployeeNotProvidedException("No sales employee provided!");
        } else {
            log.debug("Sales Employee object provided with email: {}", salesEmployee.getEmail());
            User persistedSalesEmployee = checkUserObject(salesEmployee);
            log.debug("User: {}", salesEmployee);

            if (persistedSalesEmployee == null) {
                // TODO: Create own exception
                throw new EmployeeNotProvidedException("No sales employee provided!");
            }

            return persistedSalesEmployee;
        }
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
