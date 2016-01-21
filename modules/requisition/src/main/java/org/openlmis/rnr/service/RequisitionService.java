package org.openlmis.rnr.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.db.repository.mapper.DbMapper;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.service.EquipmentInventoryService;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.factory.RequisitionSearchStrategyFactory;
import org.openlmis.rnr.search.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Exposes the services for handling Rnr entity.
 */

@Service
public class RequisitionService {

  public static final String RNR_AUTHORIZATION_ERROR = "error.rnr.authorization";
  public static final String RNR_SUBMISSION_ERROR = "error.rnr.submission";
  public static final String RNR_OPERATION_UNAUTHORIZED = "error.rnr.operation.unauthorized";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY = "msg.rnr.authorized.success";
  public static final String RNR_SUBMITTED_SUCCESSFULLY = "msg.rnr.submitted.success";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "msg.rnr.authorized.without.supervisor";
  public static final String RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "msg.rnr.approved.without.supervisor";
  public static final String NO_SUPERVISORY_NODE_CONTACT_ADMIN = "msg.rnr.submitted.without.supervisor";
  public static final String RNR_APPROVED_SUCCESSFULLY = "msg.rnr.approved.success";
  public static final String APPROVAL_NOT_ALLOWED = "rnr.approval.not.allowed";

  public static final String SEARCH_ALL = "all";
  public static final String SEARCH_PROGRAM_NAME = "programName";
  public static final String SEARCH_FACILITY_CODE = "facilityCode";
  public static final String SEARCH_FACILITY_NAME = "facilityName";
  public static final String SEARCH_SUPPLYING_DEPOT_NAME = "supplyingDepot";
  public static final String CONVERT_TO_ORDER_PAGE_SIZE = "order.page.size";
  public static final String NUMBER_OF_PAGES = "number_of_pages";

  @Autowired
  private RequisitionRepository requisitionRepository;
  @Autowired
  private RnrTemplateService rnrTemplateService;
  @Autowired
  private FacilityApprovedProductService facilityApprovedProductService;
  @Autowired
  private SupervisoryNodeService supervisoryNodeService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;
  @Autowired
  private ProgramService programService;
  @Autowired
  private ProcessingScheduleService processingScheduleService;
  @Autowired
  private FacilityService facilityService;
  @Autowired
  private SupplyLineService supplyLineService;
  @Autowired
  private RequisitionPermissionService requisitionPermissionService;
  @Autowired
  private UserService userService;
  @Autowired
  private RequisitionEventService requisitionEventService;
  @Autowired
  private RegimenService regimenService;
  @Autowired
  private RegimenColumnService regimenColumnService;
  @Autowired
  private ProgramProductService programProductService;
  @Autowired
  private StaticReferenceDataService staticReferenceDataService;
  @Autowired
  private CalculationService calculationService;
  @Autowired
  private DbMapper dbMapper;
  @Autowired
  private BudgetLineItemService budgetLineItemService;
  @Autowired
  private StatusChangeEventService statusChangeEventService;
  @Autowired
  private EquipmentInventoryService equipmentInventoryService;
  @Autowired
  private ConfigurationSettingService configurationSettingsService;

  private RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;

  @Autowired
  private ProductPriceScheduleService priceScheduleService;

  @Autowired
  public void setRequisitionSearchStrategyFactory(RequisitionSearchStrategyFactory requisitionSearchStrategyFactory) {
    this.requisitionSearchStrategyFactory = requisitionSearchStrategyFactory;
  }

  @Transactional
  public Rnr initiate(Facility facility, Program program, Long modifiedBy, Boolean emergency, ProcessingPeriod proposedPeriod) {

    if (!requisitionPermissionService.hasPermission(modifiedBy, facility, program, CREATE_REQUISITION)) {
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    }

    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplateForRequisition(program.getId());
    if (rnrTemplate.getColumns().size() == 0) {
      throw new DataException("error.rnr.template.not.defined");
    }

    program = programService.getById(program.getId());
    ProcessingPeriod period = findPeriod(facility, program, emergency);

    if(proposedPeriod != null){
      if(proposedPeriod.getId() != period.getId()){
        // take caution in this case.
        // todo: log a warning here.
        period = proposedPeriod;
      }
    }

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(
      facility.getId(), program.getId());

     //N:B If usePriceSchedule is selected for the selected program, use the product price from price_schedule table
    if(program.getUsePriceSchedule())
        populateProductsPriceBasedOnPriceSchedule(facility.getId(), program.getId(), facilityTypeApprovedProducts); //non intrusive on the legacy setup

    List<Regimen> regimens = regimenService.getByProgram(program.getId());
    RegimenTemplate regimenTemplate = regimenColumnService.getRegimenTemplateByProgramId(program.getId());

    Rnr requisition = new Rnr(facility, program, period, emergency, facilityTypeApprovedProducts, regimens, modifiedBy);
    requisition.setCreatedDate(dbMapper.getCurrentTimeStamp());
    populateAllocatedBudget(requisition);

    calculationService.fillFieldsForInitiatedRequisition(requisition, rnrTemplate, regimenTemplate);
    calculationService.fillReportingDays(requisition);
    if(configurationSettingsService.getBoolValue("RNR_COPY_SKIPPED_FROM_PREVIOUS_RNR")) {
      calculationService.copySkippedFieldFromPreviousPeriod(requisition);
    }
    // if program supports equipments, initialize it here.
    if(program.getIsEquipmentConfigured()){
       populateEquipments(requisition);
    }

    insert(requisition);
    requisition = requisitionRepository.getById(requisition.getId());

    return fillSupportingInfo(requisition);
  }

    public void populateProductsPriceBasedOnPriceSchedule(Long facilityId, Long programId, List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts) {
        List<ProductPriceSchedule> productPriceSchedules = priceScheduleService.getPriceScheduleFullSupplyFacilityApprovedProduct(programId, facilityId);

        for(ProductPriceSchedule productPriceSchedule : productPriceSchedules){
            for(FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts) {
                if (productPriceSchedule.getProduct().getId().equals(facilityTypeApprovedProduct.getProgramProduct().getProduct().getId()))
                    facilityTypeApprovedProduct.getProgramProduct().setCurrentPrice(new Money(BigDecimal.valueOf(productPriceSchedule.getPrice())));
            }
        }
    }


    private void populateEquipments(Rnr requisition) {
    List<EquipmentInventory> inventories = equipmentInventoryService.getInventoryForFacility(requisition.getFacility().getId(), requisition.getProgram().getId());
    requisition.setEquipmentLineItems(new ArrayList<EquipmentLineItem>());
    for(EquipmentInventory inv : inventories){
      EquipmentLineItem lineItem = new EquipmentLineItem();
      lineItem.setRnrId(requisition.getId());
      lineItem.setEquipmentSerial(inv.getSerialNumber());
      lineItem.setEquipmentInventoryId(inv.getId());
      lineItem.setCode(inv.getEquipment().getEquipmentType().getCode());
      lineItem.setEquipmentCategory(inv.getEquipment().getEquipmentType().getName());
      lineItem.setOperationalStatusId(inv.getOperationalStatusId());
      lineItem.setEquipmentName(inv.getEquipment().getName());
      lineItem.setDaysOutOfUse(0L);

      requisition.getEquipmentLineItems().add(lineItem);
    }
  }

  private void populateAllocatedBudget(Rnr requisition) {
    if (requisition.isBudgetingApplicable()) {
      BudgetLineItem budgetLineItem = budgetLineItemService.get(requisition.getFacility().getId(), requisition.getProgram().getId(), requisition.getPeriod().getId());
      BigDecimal allocatedBudget = (budgetLineItem == null) ? null : budgetLineItem.getAllocatedBudget();
      requisition.setAllocatedBudget(allocatedBudget);
    }
  }

  @Transactional
  public Rnr save(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (!requisitionPermissionService.hasPermissionToSave(rnr.getModifiedBy(), savedRnr)) {
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    }

    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId());
    RegimenTemplate regimenTemplate = regimenColumnService.getRegimenTemplateByProgramId(savedRnr.getProgram().getId());

    if (savedRnr.getStatus() == AUTHORIZED || savedRnr.getStatus() == IN_APPROVAL) {
      savedRnr.copyApproverEditableFields(rnr, rnrTemplate);

    } else {
      List<ProgramProduct> programProducts = programProductService.getNonFullSupplyProductsForProgram(savedRnr.getProgram());
      savedRnr.copyCreatorEditableFields(rnr, rnrTemplate, regimenTemplate, programProducts);
      //TODO: copy only the editable fields.
      savedRnr.setEquipmentLineItems(rnr.getEquipmentLineItems());
    }

    requisitionRepository.update(savedRnr);

    return savedRnr;
  }

  @Transactional
  public Rnr submit(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (savedRnr.getStatus() != INITIATED)
      throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR));

    savedRnr.validateRegimenLineItems(regimenColumnService.getRegimenTemplateByProgramId(savedRnr.getProgram().getId()));

    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, CREATE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.setAuditFieldsForRequisition(rnr.getModifiedBy(), SUBMITTED);

    ProgramRnrTemplate template = rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId());

    calculationService.perform(savedRnr, template);

    return update(savedRnr);
  }

  @Transactional
  public Rnr authorize(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (savedRnr.getStatus() != SUBMITTED)
      throw new DataException(new OpenLmisMessage(RNR_AUTHORIZATION_ERROR));

    savedRnr.validateRegimenLineItems(regimenColumnService.getRegimenTemplateByProgramId(savedRnr.getProgram().getId()));

    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, AUTHORIZE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.setAuditFieldsForRequisition(rnr.getModifiedBy(), AUTHORIZED);
    savedRnr.setSupervisoryNodeId(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram()).getId());

    ProgramRnrTemplate template = rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId());

    calculationService.perform(savedRnr, template);
    savedRnr.setFieldsForApproval();

    return update(savedRnr);
  }

  @Transactional
  public Rnr approve(Rnr requisition, String name) {
    Rnr savedRnr = getFullRequisitionById(requisition.getId());
    if (!savedRnr.isApprovable())
      throw new DataException(APPROVAL_NOT_ALLOWED);

    if (!requisitionPermissionService.hasPermission(requisition.getModifiedBy(), savedRnr, APPROVE_REQUISITION)) {
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    }

    savedRnr.validateForApproval();

    savedRnr.calculateForApproval();

    final SupervisoryNode parent = supervisoryNodeService.getParent(savedRnr.getSupervisoryNodeId());

    boolean notifyStatusChange = true;
    if (parent == null) {
      savedRnr.prepareForFinalApproval();
    } else {
      if (savedRnr.getStatus() == IN_APPROVAL) {
        notifyStatusChange = false;
      }
      savedRnr.approveAndAssignToNextSupervisoryNode(parent);
    }

    savedRnr.setModifiedBy(requisition.getModifiedBy());
    requisitionRepository.approve(savedRnr);

    logStatusChangeAndNotify(savedRnr, notifyStatusChange, name);

    return savedRnr;
  }

  public void releaseRequisitionsAsOrder(List<Rnr> requisitions, Long userId) {
    for (Rnr requisition : requisitions) {
      Rnr loadedRequisition = requisitionRepository.getById(requisition.getId());
      fillSupportingInfo(loadedRequisition);
      loadedRequisition.convertToOrder(userId);
      update(loadedRequisition);
    }
  }

  public List<Rnr> get(RequisitionSearchCriteria criteria) {
    RequisitionSearchStrategy strategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);
    List<Rnr> requisitions = strategy.search();
    if (requisitions != null) {
      fillSupportingInfo(requisitions);
    }
    return requisitions;
  }

  private void fillSupportingInfo(List<Rnr> requisitions) {
    for (Rnr rnr : requisitions) {
      fillSupportingInfo(rnr);
    }
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return requisitionRepository.getLossesAndAdjustmentsTypes();
  }

  public OpenLmisMessage getSubmitMessageBasedOnSupervisoryNode(Facility facility, Program program) {
    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facility, program);
    String msg = (supervisoryNode == null) ? NO_SUPERVISORY_NODE_CONTACT_ADMIN : RNR_SUBMITTED_SUCCESSFULLY;

    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage getAuthorizeMessageBasedOnSupervisoryNode(Facility facility, Program program) {
    User approver = supervisoryNodeService.getApproverFor(facility, program);
    String msg = (approver == null) ? RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR : RNR_AUTHORIZED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage getApproveMessageBasedOnParentNode(Rnr rnr) {
    SupervisoryNode parent = supervisoryNodeService.getParent(rnr.getSupervisoryNodeId());
    if (parent != null && supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram()) == null) {
      return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR);
    }
    return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY);
  }

  public Rnr getFullRequisitionById(Long id) {
    Rnr savedRnr = requisitionRepository.getById(id);
    fillSupportingInfo(savedRnr);
    fillSupplyingDepot(savedRnr);
    savedRnr.setSubmittedDate(getOperationDateFor(savedRnr.getId(), RnrStatus.SUBMITTED.toString()));
    return savedRnr;
  }

  ProcessingPeriod findPeriod(Facility facility, Program program, Boolean emergency) {
    if (!(emergency || facility.getVirtualFacility())) {
      return getPeriodForInitiating(facility, program);
    }

    ProcessingPeriod currentPeriod = processingScheduleService.getCurrentPeriod(facility.getId(), program.getId(),
      programService.getProgramStartDate(facility.getId(), program.getId()));

    if (currentPeriod == null)
      throw new DataException("error.program.configuration.missing");

    return currentPeriod;
  }

  public ProcessingPeriod getPeriodForInitiating(Facility facility, Program program) {
    Date programStartDate = programService.getProgramStartDate(facility.getId(), program.getId());
    Rnr lastRegularRequisition = requisitionRepository.getLastRegularRequisition(facility, program);
    Long periodIdForLastRequisition = null;
    if (lastRegularRequisition != null) {
      if (lastRegularRequisition.preAuthorize()) {
        throw new DataException("error.rnr.previous.not.filled");
      }
      periodIdForLastRequisition = lastRegularRequisition.getPeriod().getId();
    }

    List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsAfterDateAndPeriod(facility.getId(), program.getId(), programStartDate, periodIdForLastRequisition);

    if (periods.size() == 0) {
      throw new DataException("error.program.configuration.missing");
    }
    return periods.get(0);
  }

  public List<ProcessingPeriod> getAllPeriodsForInitiatingRequisition(Long facilityId, Long programId) {
    Date programStartDate = programService.getProgramStartDate(facilityId, programId);

    Rnr lastRequisitionToEnterThePostSubmitFlow = requisitionRepository.getLastRegularRequisitionToEnterThePostSubmitFlow(facilityId, programId);

    Long periodIdOfLastRequisitionToEnterPostSubmitFlow = lastRequisitionToEnterThePostSubmitFlow == null ? null : lastRequisitionToEnterThePostSubmitFlow.getPeriod().getId();


    List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsAfterDateAndPeriod(facilityId, programId, programStartDate, periodIdOfLastRequisitionToEnterPostSubmitFlow);

    List<ProcessingPeriod> rejected = processingScheduleService.getOpenPeriods(facilityId, programId, periodIdOfLastRequisitionToEnterPostSubmitFlow);

    if(periods == null || periods.isEmpty()){
      periods = rejected;
    }else {
      if(rejected != null && !rejected.isEmpty()){
        periods.addAll(rejected);
      }
    }

    // find the distinct list
    Set<ProcessingPeriod> finalList = new HashSet<ProcessingPeriod> (periods);
    periods = new ArrayList<ProcessingPeriod>(finalList);
    // sort the list of periods by start date
    Collections.sort(periods, new Comparator<ProcessingPeriod>() {

      public int compare(ProcessingPeriod o1, ProcessingPeriod o2) {
        return o1.getStartDate().compareTo(o2.getStartDate());
      }
    });

    return periods;
  }

  private Rnr fillSupportingInfo(Rnr requisition) {
    if (requisition == null) {
      return null;
    }

    fillFacilityPeriodProgramWithAuditFields(asList(requisition));

    return requisition;
  }

  private void fillSupplyingFacility(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      fillSupplyingDepot(requisition);
    }
  }

  private void fillSupplyingDepot(Rnr requisition) {
    if (requisition.getSupervisoryNodeId() != null && requisition.getStatus().equals(RnrStatus.APPROVED)) {
      SupplyLine supplyLine = supplyLineService.getSupplyLineBy(new SupervisoryNode(requisition.getSupervisoryNodeId()), requisition.getProgram());
      if (supplyLine != null)
        requisition.setSupplyingDepot(supplyLine.getSupplyingFacility());
    }
  }

  @Deprecated
  public List<Rnr> listForApproval(Long userId) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);
    List<Rnr> requisitionsForApproval = new ArrayList<>();
    for (RoleAssignment assignment : assignments) {
      final List<Rnr> requisitions = requisitionRepository.getAuthorizedRequisitions(assignment);
      requisitionsForApproval.addAll(requisitions);
    }
    fillFacilityPeriodProgramWithAuditFields(requisitionsForApproval);
    return requisitionsForApproval;
  }

  public List<RnrDTO> listForApprovalDto(Long userId) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);
    List<RnrDTO> requisitionsForApproval = new ArrayList<>();
    for (RoleAssignment assignment : assignments) {
      final List<RnrDTO> requisitions = requisitionRepository.getAuthorizedRequisitionsDTOs(assignment);
      requisitionsForApproval.addAll(requisitions);
    }
    return requisitionsForApproval;
  }

  public List<RnrLineItem> getNRnrLineItems(String productCode, Rnr rnr, Integer n, Date startDate) {
    return requisitionRepository.getAuthorizedRegularUnSkippedLineItems(productCode, rnr, n, startDate);
  }

  private Rnr update(Rnr requisition) {
    requisitionRepository.update(requisition);
    logStatusChangeAndNotify(requisition, true, null);
    return requisition;
  }

  private void logStatusChangeAndNotify(Rnr requisition, boolean notifyStatusChange, String name) {
    requisitionRepository.logStatusChange(requisition, name);
    if (notifyStatusChange) {
      requisitionEventService.notifyForStatusChange(requisition);
    }

    // the function call above (notify for status implements sending of notificaiton email.
    // the benefit of the above call is that the email template is being taken from the administrative settings.
    // a call to the following method will do the same thing but takes the message template from the messages.properties file.
    //send RequisitionStatusChangeMail ( requisition );
  }

  private void sendRequisitionStatusChangeMail(Rnr requisition) {

    List<User> userList = new ArrayList<>();

    if (requisition.getStatus().equals(SUBMITTED)) {
      Long supervisoryNodeId = supervisoryNodeService.getFor(requisition.getFacility(), requisition.getProgram()).getId();
      userList = userService.getUsersWithRightInHierarchyUsingBaseNode(supervisoryNodeId, requisition.getProgram(), AUTHORIZE_REQUISITION);
      userList.addAll(userService.getUsersWithRightInNodeForProgram(requisition.getProgram(), new SupervisoryNode(), AUTHORIZE_REQUISITION));
    } else if (requisition.getStatus().equals(AUTHORIZED) || requisition.getStatus().equals(IN_APPROVAL)) {
      userList = userService.getUsersWithRightInNodeForProgram(requisition.getProgram(), new SupervisoryNode(requisition.getSupervisoryNodeId()), APPROVE_REQUISITION);
    } else if (requisition.getStatus().equals(APPROVED)) {
      SupervisoryNode baseSupervisoryNode = supervisoryNodeService.getFor(requisition.getFacility(), requisition.getProgram());
      SupplyLine supplyLine = supplyLineService.getSupplyLineBy(new SupervisoryNode(baseSupervisoryNode.getId()), requisition.getProgram());
      if (supplyLine != null) {
        userList = userService.getUsersWithRightOnWarehouse(supplyLine.getSupplyingFacility().getId(), CONVERT_TO_ORDER);
      }
    }

    ArrayList<User> activeUsersWithRight = userService.filterForActiveUsers(userList);
    statusChangeEventService.notifyUsers(activeUsersWithRight, requisition.getId(), requisition.getFacility(),
        requisition.getProgram(), requisition.getPeriod(), requisition.getStatus().toString());
  }

  private void insert(Rnr requisition) {
    requisitionRepository.insert(requisition);
    logStatusChangeAndNotify(requisition, true, null);
  }

  public Integer getCategoryCount(Rnr requisition, boolean fullSupply) {
    return requisitionRepository.getCategoryCount(requisition, fullSupply);
  }

  public void insertComment(Comment comment) {
    requisitionRepository.insertComment(comment);
  }

  public List getCommentsByRnrId(Long rnrId) {
    List<Comment> comments = requisitionRepository.getCommentsByRnrID(rnrId);
    for (Comment comment : comments) {
      User user = userService.getById(comment.getAuthor().getId());
      comment.setAuthor(user.basicInformation());
    }
    return comments;
  }

  public Rnr getLWById(Long rnrId) {
    return requisitionRepository.getLWById(rnrId);
  }

  public Date getOperationDateFor(Long rnrId, String status) {
    return requisitionRepository.getOperationDateFor(rnrId, status);
  }

  public ProcessingPeriod getCurrentPeriod(RequisitionSearchCriteria criteria) {
    Date programStartDate = programService.getProgramStartDate(criteria.getFacilityId(), criteria.getProgramId());
    return processingScheduleService.getCurrentPeriod(criteria.getFacilityId(), criteria.getProgramId(), programStartDate);
  }

  public List<Rnr> getApprovedRequisitionsForCriteriaAndPageNumber(String searchType, String searchVal, Integer pageNumber,
                                                                   Integer totalNumberOfPages, Long userId, String rightName,
                                                                   String sortBy, String sortDirection) {
    if (pageNumber.equals(1) && totalNumberOfPages.equals(0))
      return new ArrayList<>();

    if (pageNumber <= 0 || pageNumber > totalNumberOfPages)
      throw new DataException("error.page.not.found");

    Integer pageSize = Integer.parseInt(staticReferenceDataService.getPropertyValue(CONVERT_TO_ORDER_PAGE_SIZE));

    List<Rnr> requisitions = requisitionRepository.getApprovedRequisitionsForCriteriaAndPageNumber(searchType, searchVal,
      pageNumber, pageSize, userId, rightName, sortBy, sortDirection);

    fillFacilityPeriodProgramWithAuditFields(requisitions);
    fillSupplyingFacility(requisitions.toArray(new Rnr[requisitions.size()]));
    return requisitions;
  }

  public Integer getNumberOfPagesOfApprovedRequisitionsForCriteria(String searchType, String searchVal, Long userId, String rightName) {
    Integer approvedRequisitionsByCriteria = requisitionRepository.getCountOfApprovedRequisitionsForCriteria(searchType, searchVal, userId, rightName);
    Integer pageSize = Integer.parseInt(staticReferenceDataService.getPropertyValue(CONVERT_TO_ORDER_PAGE_SIZE));
    return (int) Math.ceil(approvedRequisitionsByCriteria.doubleValue() / pageSize.doubleValue());
  }

  public List<ProcessingPeriod> getProcessingPeriods(RequisitionSearchCriteria criteria) {
    if (!criteria.isEmergency()) {
      return getAllPeriodsForInitiatingRequisition(criteria.getFacilityId(), criteria.getProgramId());
    }
    ProcessingPeriod currentPeriod = getCurrentPeriod(criteria);
    return currentPeriod == null ? null : asList(currentPeriod);
  }

  public List<Rnr> getRequisitionsFor(RequisitionSearchCriteria criteria, List<ProcessingPeriod> periodList) {
    if (isEmpty(periodList) && (!criteria.isEmergency())) {
      return emptyList();
    }

    if (!criteria.isEmergency()) {
      criteria.setPeriodId(periodList.get(0).getId());
    }

    criteria.setWithoutLineItems(true);
    return get(criteria);
  }

  public Long getFacilityId(Long id) {
    return requisitionRepository.getFacilityId(id);
  }

  private void fillFacilityPeriodProgramWithAuditFields(List<Rnr> requisitions) {
    for (Rnr requisition : requisitions) {
      Facility facility = facilityService.getById(requisition.getFacility().getId());
      ProcessingPeriod period = processingScheduleService.getPeriodById(requisition.getPeriod().getId());
      Program program = programService.getById(requisition.getProgram().getId());

      requisition.fillBasicInformation(facility, program, period);
      requisition.setSubmittedDate(getOperationDateFor(requisition.getId(), SUBMITTED.toString()));
    }
  }

  public RnrLineItem getNonSkippedLineItem(Long rnrId, String productCode) {
    return requisitionRepository.getNonSkippedLineItem(rnrId, productCode);
  }

  public String deleteRnR(Long rnrId) {
    return requisitionRepository.deleteRnR(rnrId);
  }

  public void skipRnR(Long rnrId, Long userId) {
    Rnr rnr = this.getFullRequisitionById(rnrId);
    for(RnrLineItem li : rnr.getFullSupplyLineItems()){
      li.setSkipped(true);
    }
    rnr.setModifiedBy(userId);
    rnr.setStatus(RnrStatus.SKIPPED);
    this.save(rnr);
    requisitionRepository.update(rnr);
    logStatusChangeAndNotify(rnr, false, RnrStatus.SKIPPED.toString());
  }


  public void reOpenRnR(Long rnrId, Long userId) {
    Rnr rnr = this.getFullRequisitionById(rnrId);
    rnr.setModifiedBy(userId);
    for(RnrLineItem li : rnr.getFullSupplyLineItems()){
      li.setSkipped(false);
    }
    rnr.setStatus(RnrStatus.INITIATED);
    requisitionRepository.update(rnr);
    this.save(rnr);
    logStatusChangeAndNotify(rnr, false, RnrStatus.INITIATED.toString());
  }

  public void rejectRnR(Long rnrId, Long userId) {
    Rnr rnr = this.getFullRequisitionById(rnrId);
    rnr.setModifiedBy(userId);
    rnr.setStatus(RnrStatus.INITIATED);
    requisitionRepository.update(rnr);
    logStatusChangeAndNotify(rnr, false, RnrStatus.INITIATED.toString());
  }

  public Integer findM(ProcessingPeriod period) {
    return processingScheduleService.findM(period);
  }

  public Long getProgramId(Long rnrId) {
    return requisitionRepository.getProgramId(rnrId);
  }

  @Transactional
  public void updateClientFields(Rnr rnr) {
    requisitionRepository.updateClientFields(rnr);
  }

  @Transactional
  public void insertPatientQuantificationLineItems(Rnr rnr) {
    requisitionRepository.insertPatientQuantificationLineItems(rnr);
  }


  public List<Rnr> getRequisitionsByFacility(Facility facility) {
    return requisitionRepository.getRequisitionDetailsByFacility(facility);
  }

  @Transactional
  public void insertRnrSignatures(Rnr rnr) {
    requisitionRepository.insertRnrSignatures(rnr);
  }
}

