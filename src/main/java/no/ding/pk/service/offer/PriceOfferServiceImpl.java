package no.ding.pk.service.offer;

import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.ApproverNotFoundException;
import no.ding.pk.web.handlers.EmployeeNotProvidedException;
import no.ding.pk.web.handlers.MissingApprovalStatusException;
import no.ding.pk.web.handlers.PriceOfferNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static no.ding.pk.repository.specifications.ApprovalSpecifications.*;

@Transactional
@Service
public class PriceOfferServiceImpl implements PriceOfferService {

    private static final Logger log = LoggerFactory.getLogger(PriceOfferServiceImpl.class);

    private final PriceOfferRepository repository;

    private final SalesOfficeService salesOfficeService;

    private final UserService userService;

    private final SalesOfficePowerOfAttorneyService powerOfAttorneyService;

    @Autowired
    public PriceOfferServiceImpl(PriceOfferRepository repository,
                                 SalesOfficeService salesOfficeService,
                                 UserService userService,
                                 SalesOfficePowerOfAttorneyService powerOfAttorneyService) {
        this.repository = repository;
        this.salesOfficeService = salesOfficeService;
        this.userService = userService;
        this.powerOfAttorneyService = powerOfAttorneyService;
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

        entity.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());

        entity.setCustomerNumber(newPriceOffer.getCustomerNumber());
        if(newPriceOffer.getCustomerName() != null) {
            entity.setCustomerName(newPriceOffer.getCustomerName());
        }
        entity.setNeedsApproval(newPriceOffer.getNeedsApproval());

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

        Map<String, List<PriceRow>> materialsForApproval = getAllMaterialsForApproval(newPriceOffer);

        if(!materialsForApproval.isEmpty()) {
            StringBuilder materialNumbersForApproval = new StringBuilder();
            for (Map.Entry<String, List<PriceRow>> listEntry : materialsForApproval.entrySet()) {
                String materials = String.join(",", listEntry.getValue().stream().map(priceRow -> priceRow.getMaterial().getMaterialNumber()).toList());

                if(materialNumbersForApproval.length() > 0) {
                    materialNumbersForApproval.append(",").append(materials);
                } else {
                    materialNumbersForApproval = new StringBuilder(materials);
                }
            }

            entity.setMaterialsForApproval(materialNumbersForApproval.toString());
        }

        if(newPriceOffer.getApprover() != null) {
            User approver = checkUserObject(newPriceOffer.getApprover());

            if(approver != null) {
                entity.setApprover(approver);
            } else {
                approver = getApproverForOffer(materialsForApproval);
                entity.setApprover(approver);
            }
        } else if(!materialsForApproval.isEmpty()) {
            User approver = getApproverForOffer(materialsForApproval);

            if(approver == null) {
                log.debug("No approver found for PriceOffer with sales organization(s) {} and sales office {}", newPriceOffer.getSalesOfficeList().stream().map(SalesOffice::getSalesOrg).toList(), newPriceOffer.getSalesOfficeList().stream().map(SalesOffice::getSalesOffice).toList());
            }
            entity.setApprover(approver);
        }

        if(newPriceOffer.getCustomerTerms() != null) {
            entity.setCustomerTerms(newPriceOffer.getCustomerTerms());
        }

        return repository.save(entity);
    }

    private User getApproverForOffer(Map<String, List<PriceRow>> materialsForApproval) {
        Set<User> approvalUsers = new HashSet<>();

        for (Map.Entry<String, List<PriceRow>> listEntry : materialsForApproval.entrySet()) {
            boolean hasFaMaterialForApproval = false;
            boolean hasRegularMaterialForApproval = false;

            int highestDiscountLevel = 0;

            for(PriceRow priceRow : listEntry.getValue()) {
                Material material = priceRow.getMaterial();
                if(material.isFaMaterial()) {
                    hasFaMaterialForApproval = true;
                } else {
                    hasRegularMaterialForApproval = true;
                }

                if(priceRow.getDiscountLevel() > highestDiscountLevel) {
                    highestDiscountLevel = priceRow.getDiscountLevel();
                }
            }

            PowerOfAttorney poa = powerOfAttorneyService.findBySalesOffice(Integer.valueOf(listEntry.getKey()));

            if(hasFaMaterialForApproval && !hasRegularMaterialForApproval) {
                approvalUsers.add(poa.getDangerousWasteHolder());
            } else {
                if(highestDiscountLevel > 5) {
                    approvalUsers.add(poa.getOrdinaryWasteLvlTwoHolder());
                } else {
                    approvalUsers.add(poa.getOrdinaryWasteLvlOneHolder());
                }
            }
        }

        if(approvalUsers.isEmpty()) {
            return null;
        }

        return approvalUsers.iterator().next();
    }

    private Map<String, List<PriceRow>> getAllMaterialsForApproval(PriceOffer priceOffer) {
        Map<String, List<PriceRow>> salesOfficeMaterialsMap = new HashMap<>();

        if(priceOffer.getSalesOfficeList() == null) {
            return salesOfficeMaterialsMap;
        }
        for(SalesOffice salesOffice : priceOffer.getSalesOfficeList()) {
            List<PriceRow> materialsInPriceOffer = new ArrayList<>();
            if(salesOffice.getMaterialList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getMaterialList());

            if(salesOffice.getTransportServiceList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getTransportServiceList());

            if(salesOffice.getRentalList() != null)
                collectMaterial(materialsInPriceOffer, salesOffice.getRentalList());

            if(!materialsInPriceOffer.isEmpty()) {
                salesOfficeMaterialsMap.put(salesOffice.getSalesOffice(), materialsInPriceOffer);
            }
        }
        return salesOfficeMaterialsMap;
    }

    private static void collectMaterial(List<PriceRow> materialsInPriceOffer, List<PriceRow> priceRows) {
        for(PriceRow pr : priceRows) {
            if(pr.getNeedsApproval() && !pr.isApproved()) {
                materialsInPriceOffer.add(pr);
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
    public List<PriceOffer> findAllWithoutStatusInList(List<String> status) {
        return repository.findAllByPriceOfferStatusNotIn(status);
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
    public List<PriceOffer> findAllByApproverIdAndPriceOfferStatus(Long approverId, String priceOfferStatus) {
        return repository.findAll(Specification.where(withApproverId(approverId)).and(withPriceOfferStatus(priceOfferStatus)));
    }

    @Override
    public Boolean approvePriceOffer(Long priceOfferId, Long approverId, String priceOfferStatus, String comment) {
        PriceOffer priceOfferToApprove = repository.findByIdAndApproverIdAndNeedsApprovalIsTrue(priceOfferId, approverId);

        if(priceOfferToApprove == null) {
            String message = String.format("Could not find price offer to approve. Given price offer ID %d and approver ID %d", priceOfferId, approverId);
            log.debug(message);
            throw new PriceOfferNotFoundException(message);
        }

        if(!PriceOfferStatus.getAllPriceOfferStatuses().contains(priceOfferStatus)) {
            String message = String.format("Given status was not found. Given status: %s, expected one of the following %s", priceOfferStatus, PriceOfferStatus.getApprovalStates());
            log.debug(message);
            throw new MissingApprovalStatusException(message);
        }

        if(StringUtils.isNotBlank(priceOfferStatus) && PriceOfferStatus.isApprovalState(priceOfferStatus))  {
            priceOfferToApprove.setPriceOfferStatus(priceOfferStatus);
            approveMaterialsSinceLastUpdate(priceOfferToApprove);
            boolean needsReApproval = checkIfPriceOfferNeedsApproval(priceOfferToApprove);

            if(needsReApproval) {
                priceOfferToApprove.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
                priceOfferToApprove.setNeedsApproval(true);

                if(priceOfferToApprove.getApprover() == null) {
                    Map<String, List<PriceRow>> materialsForApproval = getAllMaterialsForApproval(priceOfferToApprove);
                    User approver = getApproverForOffer(materialsForApproval);

                    priceOfferToApprove.setApprover(approver);
                }
            }
        } else {
            priceOfferToApprove.setPriceOfferStatus(priceOfferStatus);
            priceOfferToApprove.setDismissalReason(comment);
        }

        priceOfferToApprove = repository.save(priceOfferToApprove);

        return PriceOfferStatus.getApprovalStates().contains(priceOfferToApprove.getPriceOfferStatus());
    }

    @Override
    public Boolean activatePriceOffer(Long approverId, Long priceOfferId, PriceOfferTerms customerTerms) {
        User approver = userService.findById(approverId).orElse(null);

        if(approver == null) {
            String message = String.format("No approver with given id %d was found.", approverId);
            throw new ApproverNotFoundException(message);
        }

        PriceOffer priceOfferToActivate = repository.findById(priceOfferId).orElse(null);

        if(priceOfferToActivate == null) {
            String message = String.format("No PriceOffer with given id %d was found.", priceOfferId);
            throw new PriceOfferNotFoundException(message);
        }

        priceOfferToActivate.setCustomerTerms(customerTerms);
        priceOfferToActivate.setPriceOfferStatus(PriceOfferStatus.ACTIVATED.getStatus());

        repository.save(priceOfferToActivate);

        return true;
    }

    @Override
    public List<PriceOffer> findAllByPriceOfferStatusInList(List<String> statusList) {
        return repository.findAllByPriceOfferStatusIn(statusList);
    }

    private void approveMaterialsSinceLastUpdate(PriceOffer priceOfferToApprove) {
        // Exception thrown here
        List<String> materialsToApprove = new ArrayList<>(Arrays.stream(priceOfferToApprove.getMaterialsForApproval().split(",")).sorted().toList());
        List<String> approvedMaterials = new ArrayList<>();

        boolean priceOfferIsApproved = PriceOfferStatus.getApprovalStates().contains(priceOfferToApprove.getPriceOfferStatus());

        for(SalesOffice so : priceOfferToApprove.getSalesOfficeList()) {
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getMaterialList(), priceOfferIsApproved));
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getTransportServiceList(), priceOfferIsApproved));
            approvedMaterials.addAll(setApprovalStatusForMaterials(materialsToApprove, so.getRentalList(), priceOfferIsApproved));
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
        List<String> currentMaterialInPriceOffer = new ArrayList<>();
        for (Map.Entry<String, List<PriceRow>> listEntry : getAllMaterialsForApproval(priceOfferToApprove).entrySet()) {
            currentMaterialInPriceOffer.addAll(listEntry.getValue().stream().map(priceRow -> priceRow.getMaterial().getMaterialNumber()).toList());
        }

        List<String> previousMaterialInPriceOffer = Arrays.stream(priceOfferToApprove.getMaterialsForApproval().split(",")).sorted().toList();

        return currentMaterialInPriceOffer.equals(previousMaterialInPriceOffer);
    }

}
