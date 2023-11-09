package no.ding.pk.service.offer;

import lombok.Data;
import no.ding.pk.domain.PowerOfAttorney;
import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.*;
import no.ding.pk.repository.offer.PriceOfferRepository;
import no.ding.pk.service.SalesOfficePowerOfAttorneyService;
import no.ding.pk.service.UserService;
import no.ding.pk.web.enums.PriceOfferStatus;
import no.ding.pk.web.handlers.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    private final CustomerTermsService customerTermsService;
    private final ModelMapper modelMapper;
    private final List<Integer> salesOfficesWhichRequiresOwnFaApprover;

    @Autowired
    public PriceOfferServiceImpl(PriceOfferRepository repository,
                                 SalesOfficeService salesOfficeService,
                                 UserService userService,
                                 SalesOfficePowerOfAttorneyService powerOfAttorneyService,
                                 CustomerTermsService customerTermsService,
                                 @Qualifier("modelMapperV2") ModelMapper modelMapper,
                                 @Value("${sales.offices.requires.fa.approvment}") List<Integer> salesOfficesWhichRequiresOwnFaApprover) {
        this.repository = repository;
        this.salesOfficeService = salesOfficeService;
        this.userService = userService;
        this.powerOfAttorneyService = powerOfAttorneyService;
        this.customerTermsService = customerTermsService;
        this.modelMapper = modelMapper;
        this.salesOfficesWhichRequiresOwnFaApprover = salesOfficesWhichRequiresOwnFaApprover;
    }

    @Override
    public PriceOffer save(PriceOffer newPriceOffer) {
        log.debug("Received PriceOffer {}", newPriceOffer);

        User salesEmployee = checkAndGetSalesEmployee(newPriceOffer.getSalesEmployee());

        PriceOffer entity = getPriceOffer(newPriceOffer, salesEmployee);

        entity.setApprover(salesEmployee);

        if(newPriceOffer.getCustomerNumber() != null) {
            entity.setCustomerNumber(newPriceOffer.getCustomerNumber());
        }

        entity.setCustomerName(newPriceOffer.getCustomerName());

        entity.setStreetAddress(newPriceOffer.getStreetAddress());
        entity.setPostalNumber(newPriceOffer.getPostalNumber());
        entity.setCity(newPriceOffer.getCity());

        entity.setOrganizationNumber(newPriceOffer.getOrganizationNumber());

        entity.setNeedsApproval(newPriceOffer.getNeedsApproval());
        entity.setApprovalDate(newPriceOffer.getApprovalDate());
        entity.setDateIssued(newPriceOffer.getDateIssued());

        if(StringUtils.isNotBlank(newPriceOffer.getGeneralComment())) {
            entity.setGeneralComment(newPriceOffer.getGeneralComment());
        }

        if(StringUtils.isNotBlank(newPriceOffer.getAdditionalInformation())) {
            entity.setAdditionalInformation(newPriceOffer.getAdditionalInformation());
        }

        if(!CollectionUtils.isEmpty(newPriceOffer.getContactPersonList())) {
            if (CollectionUtils.isEmpty(entity.getContactPersonList()) || !CollectionUtils.containsAll(entity.getContactPersonList(), newPriceOffer.getContactPersonList())) {
                entity.setContactPersonList(newPriceOffer.getContactPersonList());
//                entity = repository.save(entity);
            }
        }

        if(newPriceOffer.getSalesOfficeList() != null) {
            if(!newPriceOffer.getSalesOfficeList().isEmpty()) {
                List<SalesOffice> salesOffices = salesOfficeService.saveAll(newPriceOffer.getSalesOfficeList(), entity.getCustomerNumber());

                entity.setSalesOfficeList(salesOffices);

                entity = repository.save(entity);
            }
        }

        Map<String, List<PriceRow>> materialsForApproval = getAllMaterialsForApproval(entity);

        if(!materialsForApproval.isEmpty()) {
            log.debug("Materials needs approving by responsible person.");
            entity.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
            entity.setNeedsApproval(true);
            String materialNumbersForApproval = flattenMaterialNumbersMapToCommaseparatedListString(materialsForApproval);

            entity.setMaterialsForApproval(materialNumbersForApproval);

            User approver = getApproverForOffer(materialsForApproval, entity.getSalesEmployee());

            if(approver != null) {
                entity.setApprover(approver);
            } else {
                log.debug("No approver found for PriceOffer with sales organization(s) {} and sales office {}", newPriceOffer.getSalesOfficeList().stream().map(SalesOffice::getSalesOrg).toList(), newPriceOffer.getSalesOfficeList().stream().map(SalesOffice::getSalesOffice).toList());
            }

            if(approver != null && approver.equals(entity.getSalesEmployee())) {
                log.debug("Sales employee has the rights to approve the price offer. Set it to approved.");
                entity.setNeedsApproval(false);
                entity.setPriceOfferStatus(PriceOfferStatus.APPROVED.getStatus());
            }
        } else if(newPriceOffer.getApprover() != null) {
            log.debug("Setting approver from new price offer object.");
            User approver = checkUserObject(newPriceOffer.getApprover());

            if(approver != null) {
                entity.setApprover(approver);
            } else {
                approver = getApproverForOffer(materialsForApproval, entity.getSalesEmployee());
                entity.setApprover(approver);
            }

            entity.setNeedsApproval(false);
            entity.setPriceOfferStatus(PriceOfferStatus.APPROVED.getStatus());
        } else {
            log.debug("No materials needs to be approved, set price offer as APPROVED.");
            entity.setNeedsApproval(false);
            entity.setPriceOfferStatus(PriceOfferStatus.APPROVED.getStatus());
        }

        if(newPriceOffer.getCustomerTerms() != null) {
            entity.setCustomerTerms(newPriceOffer.getCustomerTerms());
        }

        return repository.save(entity);
    }

    private static String flattenMaterialNumbersMapToCommaseparatedListString(Map<String, List<PriceRow>> materialsForApproval) {
        StringBuilder materialNumbersForApproval = new StringBuilder();
        for (Map.Entry<String, List<PriceRow>> listEntry : materialsForApproval.entrySet()) {
            String materials = String.join(",", listEntry.getValue().stream().map(priceRow -> priceRow.getMaterial().getMaterialNumber()).toList());

            if(!materialNumbersForApproval.isEmpty()) {
                materialNumbersForApproval.append(",").append(materials);
            } else {
                materialNumbersForApproval = new StringBuilder(materials);
            }
        }
        return materialNumbersForApproval.toString();
    }

    private PriceOffer getPriceOffer(PriceOffer newPriceOffer, User salesEmployee) {
        if(newPriceOffer.getId() != null) {
            Optional<PriceOffer> optEntity = repository.findById(newPriceOffer.getId());

            return optEntity.orElseGet(() -> createNewPriceOffer(salesEmployee));
        } else {
            return createNewPriceOffer(salesEmployee);
        }
    }

    private PriceOffer createNewPriceOffer(User salesEmployee) {
        PriceOffer entity = new PriceOffer();
        entity.setSalesEmployee(salesEmployee);

        return entity;
    }

    @Data
    private static class HighestDiscountLevelAndType {
        boolean faMaterial = false;
        boolean oaMaterial = false;

        int highestDiscountLevel = 0;
    }

    private HighestDiscountLevelAndType getHighestSetDiscountLevel(List<PriceRow> listEntry) {
        HighestDiscountLevelAndType levelAndType = new HighestDiscountLevelAndType();

        for (PriceRow priceRow : listEntry) {
            Material material = priceRow.getMaterial();
            if (material.isFaMaterial()) {
                levelAndType.setFaMaterial(true);
            } else {
                levelAndType.setOaMaterial(true);
            }

            if (priceRow.getDiscountLevel() != null && priceRow.getDiscountLevel() > levelAndType.getHighestDiscountLevel()) {
                levelAndType.setHighestDiscountLevel(priceRow.getDiscountLevel());
            }
        }

        return levelAndType;
    }

    private User getApproverForOffer(Map<String, List<PriceRow>> materialsForApproval, User salesEmployee) {
        Set<User> approvalUsers = new HashSet<>();

        for (Map.Entry<String, List<PriceRow>> salesOfficePriceRowMap : materialsForApproval.entrySet()) {
            HighestDiscountLevelAndType levelAndType = getHighestSetDiscountLevel(salesOfficePriceRowMap.getValue());

            log.debug("Highest discount level registered: {}", levelAndType.getHighestDiscountLevel());

            Integer salesOfficeNumber = Integer.valueOf(salesOfficePriceRowMap.getKey());
            PowerOfAttorney poa = powerOfAttorneyService.findBySalesOffice(salesOfficeNumber);

            if(poa == null) {
                log.debug("No power of attorney found for sales office {}", salesOfficeNumber);
                log.debug("Unable to set any approver for given price offer");
            } else {
                if(levelAndType.isFaMaterial() && salesOfficesWhichRequiresOwnFaApprover.contains(salesOfficeNumber)) {
                    if(poa.getDangerousWasteHolder() == null) {
                        log.debug("No approver elected for dangerous waste for sales office {}", salesOfficeNumber);
                    } else {
                        approvalUsers.add(poa.getDangerousWasteHolder());
                    }
                } else if (levelAndType.getHighestDiscountLevel() > 5) {
                    if (poa.getOrdinaryWasteLvlTwoHolder() == null) {
                        log.debug("No regional manager elected for ordinary waste for sales office {}", salesOfficeNumber);
                    } else {
                        approvalUsers.add(poa.getOrdinaryWasteLvlTwoHolder());
                    }
                } else if (salesEmployee.getPowerOfAttorneyOA() >= levelAndType.getHighestDiscountLevel()) {
                    log.debug("Sales employee has the correct level to approve this.");
                    approvalUsers.add(salesEmployee);
                } else if (levelAndType.isFaMaterial() && !levelAndType.isOaMaterial()) {
                    if (poa.getDangerousWasteHolder() == null) {
                        log.debug("No approver elected for dangerous waste for sales office {}", salesOfficeNumber);
                    } else {
                        approvalUsers.add(poa.getDangerousWasteHolder());
                    }
                } else {
                    if (poa.getOrdinaryWasteLvlOneHolder() == null) {
                        log.debug("No sales manager elected for approval of ordinary waste for sales office {}", salesOfficeNumber);
                    } else {
                        approvalUsers.add(poa.getOrdinaryWasteLvlOneHolder());
                    }
                }
            }
        }

        if(approvalUsers.isEmpty()) {
            return null;
        }

        return approvalUsers.iterator().next();
    }

    private Map<String, List<PriceRow>> getAllMaterialsForApproval(PriceOffer priceOffer) {
        log.debug("Getting all materials which needs approval.");
        Map<String, List<PriceRow>> salesOfficeMaterialsMap = new HashMap<>();

        if(CollectionUtils.isEmpty(priceOffer.getSalesOfficeList())) {
            log.debug("No sales offices registered on the Price offer, returning.");
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
        log.debug("Filtering done, found {} materials which needs to be approved.", salesOfficeMaterialsMap.size());
        return salesOfficeMaterialsMap;
    }

    private static void collectMaterial(List<PriceRow> materialsInPriceOffer, List<PriceRow> priceRows) {
        for(PriceRow pr : priceRows) {
            if(pr.getDiscountLevel() != null && !pr.isApproved()) {
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
    public List<PriceOffer> findAllBySalesEmployeeId(Long userId, List<String> statusList) {
        return repository.findAll(Specification.where(withSalesEmployeeId(userId)).and(withPriceOfferStatusInList(statusList)));
    }

    @Override
    public List<PriceOffer> findAllByApproverIdAndPriceOfferStatus(Long approverId, String priceOfferStatus) {
        return repository.findAll(Specification.where(withApproverId(approverId)).and(withPriceOfferStatus(priceOfferStatus)));
    }

    @Override
    public Boolean approvePriceOffer(Long priceOfferId, Long approverId, String priceOfferStatus, String additionalInformation) {
        Optional<User> approverOptional = userService.findById(approverId);

        if(approverOptional.isEmpty()) {
            log.debug("User with id {} trying to approve the offer was not found.", approverId);
            throw new UserNotFoundException(String.format("Approver User with id %d was not found.", approverId));
        }

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
                log.debug("Price offer still needs approval. Setting status to PENDING and needsApproval to true.");
                priceOfferToApprove.setPriceOfferStatus(PriceOfferStatus.PENDING.getStatus());
                priceOfferToApprove.setNeedsApproval(true);

                log.debug("Checking if Price offer has an approver registered on it.");
                if(priceOfferToApprove.getApprover() == null) {
                    log.debug("No approver set.");
                    Map<String, List<PriceRow>> materialsForApproval = getAllMaterialsForApproval(priceOfferToApprove);
                    User neededApprover = getApproverForOffer(materialsForApproval, priceOfferToApprove.getSalesEmployee());

                    log.debug("Found approver? {}", neededApprover != null);
                    priceOfferToApprove.setApprover(neededApprover);
                }
            } else {
                priceOfferToApprove.setNeedsApproval(false);
                priceOfferToApprove.setApprovalDate(new Date());
            }
        } else {
            priceOfferToApprove.setPriceOfferStatus(priceOfferStatus);
            priceOfferToApprove.setAdditionalInformation(additionalInformation);
        }

        priceOfferToApprove = repository.save(priceOfferToApprove);

        return PriceOfferStatus.getApprovalStates().contains(priceOfferToApprove.getPriceOfferStatus());
    }

    @Override
    public Boolean activatePriceOffer(Long activatedById, Long priceOfferId, PriceOfferTerms customerTerms, String generalComment) {
        Optional<PriceOffer> priceOfferToActivateOptional = repository.findById(priceOfferId);

        if(priceOfferToActivateOptional.isEmpty()) {
            String message = String.format("No PriceOffer with given id %d was found.", priceOfferId);
            throw new PriceOfferNotFoundException(message);
        }

        PriceOffer priceOfferToActivate = priceOfferToActivateOptional.get();

        if(StringUtils.isBlank(priceOfferToActivate.getPriceOfferStatus())) {
            String message = "Given Price offer has no status assigned.";

            throw new WrongStatusException(message);
        }

        if(!PriceOfferStatus.isApproved(priceOfferToActivate.getPriceOfferStatus())) {
            String message = String.format("Price offer can not be activated. Expected status is 'APPROVED', got %s", priceOfferToActivate.getPriceOfferStatus());

            throw new WrongStatusException(message);
        }

        if(StringUtils.isNotBlank(generalComment)) {
            priceOfferToActivate.setGeneralComment(generalComment);
        }

        priceOfferToActivate.setCustomerTerms(customerTerms);
        priceOfferToActivate.setPriceOfferStatus(PriceOfferStatus.ACTIVATED.getStatus());

        priceOfferToActivate = repository.save(priceOfferToActivate);

        disableExistingContract(priceOfferToActivate);

        log.debug("Activated new terms for sales offices in list: {}", priceOfferToActivate.getSalesOfficeList().stream().map(SalesOffice::getSalesOffice).toList());

        return true;
    }

    private void disableExistingContract(PriceOffer priceOfferToActivate) {
        for(SalesOffice salesOffice : priceOfferToActivate.getSalesOfficeList()) {
            endExistingCustomerTerms(priceOfferToActivate, salesOffice);

            CustomerTerms newCustomerTerms = modelMapper.map(priceOfferToActivate.getCustomerTerms(), CustomerTerms.class);

            newCustomerTerms.setId(null);
            newCustomerTerms.setCreatedBy(null);
            newCustomerTerms.setCreatedDate(null);
            newCustomerTerms.setLastModifiedBy(null);
            newCustomerTerms.setLastModifiedDate(null);

            customerTermsService.save(salesOffice.getSalesOffice(), priceOfferToActivate.getCustomerNumber(), priceOfferToActivate.getCustomerName(), newCustomerTerms);
        }
    }

    private void endExistingCustomerTerms(PriceOffer priceOfferToActivate, SalesOffice salesOffice) {
        CustomerTerms currentActiveCustomerTerms = customerTermsService.findActiveTermsForCustomerForSalesOfficeAndSalesOrg(priceOfferToActivate.getCustomerNumber(),
                salesOffice.getSalesOffice(), salesOffice.getSalesOrg());

        if(currentActiveCustomerTerms != null) {
            currentActiveCustomerTerms.setAgreementEndDate(new Date());

            customerTermsService.save(salesOffice.getSalesOffice(), priceOfferToActivate.getCustomerNumber(), priceOfferToActivate.getCustomerName(), currentActiveCustomerTerms);
        }
    }

    @Override
    public List<PriceOffer> findAllByPriceOfferStatusInList(List<String> statusList) {
        return repository.findAllByPriceOfferStatusIn(statusList);
    }

    private void approveMaterialsSinceLastUpdate(PriceOffer priceOfferToApprove) {
        List<String> materialsToApprove = convertMaterialsStringToList(priceOfferToApprove);
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

    private static List<String> convertMaterialsStringToList(PriceOffer priceOfferToApprove) {
        List<String> returnList = new ArrayList<>();
        if(StringUtils.isNotBlank(priceOfferToApprove.getMaterialsForApproval())) {
            String[] array = priceOfferToApprove.getMaterialsForApproval().split(",");
            if(array.length > 0)
                returnList.addAll(Arrays.stream(array).sorted().toList());
        }

        return returnList;
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
        log.debug("Checking if price offer needs approval.");
        List<String> currentMaterialInPriceOffer = new ArrayList<>();

        for (Map.Entry<String, List<PriceRow>> listEntry : getAllMaterialsForApproval(priceOfferToApprove).entrySet()) {
            currentMaterialInPriceOffer.addAll(listEntry.getValue().stream().map(priceRow -> priceRow.getMaterial().getMaterialNumber()).toList());
        }

        List<String> previousMaterialInPriceOffer = Arrays.stream(priceOfferToApprove.getMaterialsForApproval().split(",")).sorted().toList();

        return currentMaterialInPriceOffer.equals(previousMaterialInPriceOffer);
    }

    @Override
    public List<PriceOffer> findAllPriceOffersRadyForBoReport() {
        
        return repository.findAllByPriceOfferStatusIn(List.of(PriceOfferStatus.ACTIVATED.getStatus()));
    }

    @Override
    public void updateStatus(Long id, String status) {
        PriceOffer priceOffer = repository.findById(id).orElse(null);

        if(priceOffer == null) {
            String message = String.format("Price offer with id %d not found", id);
            throw new PriceOfferNotFoundException(message);
        }

        priceOffer.setPriceOfferStatus(status);

        repository.save(priceOffer);
    }

    @Override
    public void updateCustomerNumber(Long id, String customerNumber) {

    }

}
