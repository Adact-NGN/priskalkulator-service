package no.ding.pk.service.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.domain.offer.PriceRow;
import no.ding.pk.domain.offer.SalesOffice;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.UserService;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import no.ding.pk.web.handlers.PriceOfferNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

        return entity;
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

        // entity.setIsApproved(newPriceOffer.getIsApproved());
        entity.setApprovalDate(newPriceOffer.getApprovalDate());
        entity.setDateIssued(newPriceOffer.getDateIssued());

        entity.setContactPersonList(newPriceOffer.getContactPersonList());

        if(newPriceOffer.getSalesOfficeList() != null) {
            if(newPriceOffer.getSalesOfficeList().size() > 0) {
                List<SalesOffice> salesOffices = salesOfficeService.saveAll(newPriceOffer.getSalesOfficeList(), entity.getCustomerNumber());

                entity.setSalesOfficeList(salesOffices);

                entity = repository.save(entity);
            }
        }

        if(newPriceOffer.getNeedsApproval() && !newPriceOffer.isApproved()) {
            List<String> materialsForApproval = getAllMaterialsForApproval(newPriceOffer);

            if(!materialsForApproval.isEmpty()) {
                entity.setMaterialsForApproval(String.join(",", materialsForApproval));
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

    private List<String> getAllMaterialsForApproval(PriceOffer priceOffer) {
        List<String> materialsInPriceOffer = new ArrayList<>();
        for(SalesOffice salesOffice : priceOffer.getSalesOfficeList()) {
            if(salesOffice.getMaterialList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getMaterialList());

            if(salesOffice.getTransportServiceList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getTransportServiceList());

            if(salesOffice.getRentalList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getRentalList());
        }
        return materialsInPriceOffer;
    }

    private static void collectMaterial(List<String> materialsInPriceOffer, List<PriceRow> priceRows) {
        for(PriceRow pr : priceRows) {
            if(pr.getNeedsApproval() && !pr.isApproved()) {
                materialsInPriceOffer.add(pr.getMaterial().getMaterialNumber());
            }
        }
    }

    private User checkAndGetSalesEmployee(User salesEmployee) {
        if (salesEmployee == null) {
            throw new EmployeeNotProvidedException("No sales employee provided!");
        } else {
            log.debug("Sales Employee object provided with email: {}", salesEmployee.getEmail());
            User persistedSalesEmployee = checkUserObject(salesEmployee);
            log.debug("User: {}", salesEmployee);

            if (persistedSalesEmployee == null) {
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

        PriceOffer entity = priceOffer.get();

        entity.getCustomerTerms().setAgreementEndDate(new Date());
        entity.setDeleted(true);

        return !repository.existsById(id);
    }

    @Override
    public List<PriceOffer> findAllBySalesEmployeeId(Long userId) {
        return repository.findAllBySalesEmployeeId(userId);
    }

    @Override
    public List<PriceOffer> findAllByApproverIdAndNeedsApproval(Long approverId) {
        return repository.findAllByApproverIdAndNeedsApprovalIsTrue(approverId);
    }

    @Override
    public Boolean approvePriceOffer(Long priceOfferId, Long approverId, Boolean approved) {
        PriceOffer priceOfferToApprove = repository.findByIdAndApproverIdAndNeedsApprovalIsTrue(priceOfferId, approverId);

        if(priceOfferToApprove == null) {
            throw new PriceOfferNotFoundException();
        }

        if(approved != null && approved)  {
            approveMaterialsSinceLastUpdate(priceOfferToApprove);
            boolean needsReApproval = checkIfPriceOfferNeedsApproval(priceOfferToApprove);

            priceOfferToApprove.setIsApproved(!needsReApproval);
            priceOfferToApprove.setNeedsApproval(needsReApproval);
        }

        priceOfferToApprove = repository.save(priceOfferToApprove);

        return priceOfferToApprove.getIsApproved();
    }

    private void approveMaterialsSinceLastUpdate(PriceOffer priceOfferToApprove) {
        // Exception thrown here
        List<String> materialsToApprove = new ArrayList<>(Arrays.stream(priceOfferToApprove.getMaterialsForApproval().split(",")).sorted().toList());
        List<String> approvedMaterials = new ArrayList<>();

        for(SalesOffice so : priceOfferToApprove.getSalesOfficeList()) {
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getMaterialList(), priceOfferToApprove.getIsApproved()));
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getTransportServiceList(), priceOfferToApprove.getIsApproved()));
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getRentalList(), priceOfferToApprove.getIsApproved()));
        }

        Collections.sort(approvedMaterials);

        materialsToApprove.removeAll(approvedMaterials);

        priceOfferToApprove.setMaterialsForApproval(String.join(",", materialsToApprove));
    }

    private List<String> setApprovalStatusForMaterials(List<String> materialsToApprove, List<PriceRow> materialList, Boolean isApproved) {
        List<String> approvedMaterials = new ArrayList<>();
        for(PriceRow pr : materialList) {
            if(pr.getMaterial() == null) {
                continue;
            }
            if(materialsToApprove.contains(pr.getMaterial().getMaterialNumber())) {
                if(pr.getNeedsApproval() && !pr.isApproved()) {
                    pr.setApproved(isApproved);

                    approvedMaterials.add(pr.getMaterial().getMaterialNumber());
                    materialsToApprove.remove(pr.getMaterial().getMaterialNumber());
                }
            }
        }

        return approvedMaterials;
    }

    private boolean checkIfPriceOfferNeedsApproval(PriceOffer priceOfferToApprove) {
        List<String> currentMaterialInPriceOffer = getAllMaterialsForApproval(priceOfferToApprove).stream().sorted().toList();
        List<String> previousMaterialInPriceOffer = Arrays.stream(priceOfferToApprove.getMaterialsForApproval().split(",")).sorted().toList();

        return currentMaterialInPriceOffer.equals(previousMaterialInPriceOffer);
    }

}
