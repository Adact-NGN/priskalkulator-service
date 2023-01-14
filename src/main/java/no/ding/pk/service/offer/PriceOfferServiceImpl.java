package no.ding.pk.service.offer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.domain.offer.Terms;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.repository.offer.SalesOfficeRepository;
import no.ding.pk.service.UserService;

@Transactional
@Service
public class PriceOfferServiceImpl implements PriceOfferService {

    Logger log = LoggerFactory.getLogger(PriceOfferServiceImpl.class);
    
    private PriceOfferRepository repository;
    
    private SalesOfficeService salesOfficeService;
    
    private UserService userService;
    
    private CustomerTermsService customerTermsService;
    
    @Autowired
    public PriceOfferServiceImpl(PriceOfferRepository repository, SalesOfficeRepository salesOfficeRepository,
    SalesOfficeService salesOfficeService, PriceRowService priceRowService, MaterialService materialService,
    MaterialPriceService materialPriceService, UserService userService,
    CustomerTermsService customerTermsService) {
        this.repository = repository;
        this.salesOfficeService = salesOfficeService;
        this.userService = userService;
        this.customerTermsService = customerTermsService;
    }
    
    @Override
    public PriceOffer save(PriceOffer newPriceOffer) {
        log.debug("New PriceOffer {}", newPriceOffer);

        // if(newPriceOffer.getSalesOfficeList() != null) {
        //     // Use traditional for-loop to avoid Concurrency Exception
        //     if(newPriceOffer.getSalesOfficeList().size() > 0) {
        //         List<SalesOffice> salesOffices = salesOfficeService.saveAll(newPriceOffer.getSalesOfficeList());

        //         newPriceOffer.setSalesOfficeList(salesOffices);
        //     }
        // }

        // if(newPriceOffer.getSalesEmployee() != null) {
        //     log.debug("Sales Employee object provided with email: {}", newPriceOffer.getSalesEmployee().getEmail());
        //     User salesEmployee = checkUserObject(newPriceOffer.getSalesEmployee());

        //     if(salesEmployee == null) {
        //         // TODO: Create own exception
        //         throw new RuntimeException("No sales employee provided!");
        //     }

        //     newPriceOffer.setSalesEmployee(salesEmployee);
        // }

        // if(newPriceOffer.getApprover() != null) {
        //     User approver = checkUserObject(newPriceOffer.getApprover());

        //     if(approver != null) {
        //         newPriceOffer.setApprover(approver);
        //     }
        // }

        // if(newPriceOffer.getCustomerTerms() != null) {
        //     Terms customerTerms = customerTermsService.save(newPriceOffer.getCustomerTerms());

        //     newPriceOffer.setCustomerTerms(customerTerms);
        // }
        
        return repository.save(newPriceOffer);
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
    
}
