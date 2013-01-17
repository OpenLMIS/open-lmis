package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.builder.RnrBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RnrRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.rnr.builder.RnrBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RnrBuilder.status;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.rnr.service.RnrService.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrServiceTest {

  private static final Integer HIV = 1;
  private static final Integer FACILITY_ID = 1;
  private static final Integer PERIOD_ID = 10;
  private static final Integer USER_ID = 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Autowired
  private RnrService rnrService;
  @Mock
  private FacilityApprovedProductService facilityApprovedProductService;
  @Mock
  private RnrRepository rnrRepository;
  @Mock
  private RnrTemplateRepository rnrTemplateRepository;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private RoleRightsService roleRightService;

  @Mock
  ProgramService programService;
  @Mock
  FacilityService facilityService;

  private Rnr rnr;
  private Rnr submittedRnr;
  private Rnr initiatedRnr;

  @Before
  public void setup() {
    rnrService = new RnrService(rnrRepository, rnrTemplateRepository, facilityApprovedProductService,
      supervisoryNodeService, roleRightService, facilityService, programService);
    rnr = spy(make(a(defaultRnr)));
    submittedRnr = make(a(RnrBuilder.defaultRnr, with(status, SUBMITTED)));
    initiatedRnr = make(a(RnrBuilder.defaultRnr, with(status, INITIATED)));
  }

  @Test
  public void shouldInitRequisition() {
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(true);
    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(ProductBuilder.defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getByFacilityAndProgram(FACILITY_ID, HIV)).thenReturn(facilityApprovedProducts);

    Rnr rnr = rnrService.initRnr(FACILITY_ID, HIV, PERIOD_ID, 1);

    verify(facilityApprovedProductService).getByFacilityAndProgram(FACILITY_ID, HIV);
    verify(rnrRepository).insert(rnr);
    assertThat(rnr.getLineItems().size(), is(1));
    assertThat(rnr.getPeriodId(), is(PERIOD_ID));
  }

  @Test
  public void shouldGetRequisition() throws Exception {
    Rnr rnr = new Rnr();
    when(rnrRepository.getRequisition(FACILITY_ID, HIV, PERIOD_ID)).thenReturn(rnr);

    assertThat(rnrService.get(FACILITY_ID, HIV, PERIOD_ID), is(rnr));
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(false);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Please contact Admin to define R&R template for this program");
    Rnr rnr = rnrService.initRnr(FACILITY_ID, HIV, null, 1);
    verify(facilityApprovedProductService, never()).getByFacilityAndProgram(FACILITY_ID, HIV);
    verify(rnrRepository, never()).insert(rnr);
  }

  @Test
  public void shouldReturnMessageWhileSubmittingRnrIfSupervisingNodeNotPresent() {
    when(rnrRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(null);

    OpenLmisMessage message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);
    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message.getCode(), is("rnr.submitted.without.supervisor"));
  }

  @Test
  public void shouldSubmitValidRnrAndSetMessage() {
    when(rnrRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(new SupervisoryNode());
    OpenLmisMessage message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);

    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message.getCode(), is("rnr.submitted.success"));
  }

  @Test
  public void shouldAuthorizeAValidRnr() throws Exception {
    when(rnrRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    when(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId())).thenReturn(true);
    doReturn(true).when(rnr).validate(true);
    when(supervisoryNodeService.getApproverFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(new User());

    OpenLmisMessage authorize = rnrService.authorize(rnr);

    verify(rnrTemplateRepository).isFormulaValidated(rnr.getProgramId());
    verify(rnr).validate(true);
    verify(rnrRepository).update(rnr);
    assertThat(rnr.getStatus(), is(AUTHORIZED));
    assertThat(authorize.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY));
  }

  @Test
  public void shouldAuthorizeAValidRnrAndAdviseUserIfRnrDoesNotHaveApprover() throws Exception {
    when(rnrRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    when(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId())).thenReturn(true);
    when(supervisoryNodeService.getApproverFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(null);
    doReturn(true).when(rnr).validate(true);

    OpenLmisMessage openLmisMessage = rnrService.authorize(rnr);

    verify(rnrTemplateRepository).isFormulaValidated(rnr.getProgramId());
    verify(rnr).validate(true);
    verify(rnrRepository).update(rnr);
    assertThat(rnr.getStatus(), is(AUTHORIZED));
    assertThat(openLmisMessage.getCode(), is(RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR));
  }

  @Test
  public void shouldNotAuthorizeInvalidRnr() throws Exception {
    when(rnrRepository.getById(rnr.getId())).thenReturn(submittedRnr);
    when(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId())).thenReturn(true);
    doThrow(new DataException("error-message")).when(rnr).validate(true);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error-message");
    rnrService.authorize(rnr);
  }

  @Test
  public void shouldNotAuthorizeRnrIfNotSubmitted() throws Exception {
    when(rnrRepository.getById(rnr.getId())).thenReturn(initiatedRnr);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_AUTHORIZATION_ERROR);

    rnrService.authorize(rnr);
  }

  @Test
  public void shouldSaveRnrIfStatusIsSubmittedAndUserHasAuthorizeRight() {
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(SUBMITTED);
    List<Right> listUserRights = Arrays.asList(AUTHORIZE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    rnrService.save(rnr);
    verify(rnrRepository).update(rnr);
  }

  @Test
  public void shouldSaveRnrIfStatusIsInitiatedAndUserHasCreateRight() {
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(INITIATED);
    List<Right> listUserRights = Arrays.asList(CREATE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    rnrService.save(rnr);
    verify(rnrRepository).update(rnr);
  }

  @Test
  public void shouldNotSaveRnrWithStatusInitiatedIfUserHasOnlyAuthorizeRight() {
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(INITIATED);
    List<Right> listUserRights = Arrays.asList(AUTHORIZE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);
    rnrService.save(rnr);
  }

  @Test
  public void shouldNotSaveAlreadySubmittedRnrIfUserHasOnlyCreateRequisitionRight() {
    Integer userId = 1;
    rnr.setModifiedBy(userId);
    rnr.setStatus(SUBMITTED);
    List<Right> listUserRights = Arrays.asList(CREATE_REQUISITION);
    when(roleRightService.getRights(userId)).thenReturn(listUserRights);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(RNR_OPERATION_UNAUTHORIZED);
    rnrService.save(rnr);
  }
  @Test
  public void shouldFetchAllRequisitionsForFacilitiesAndProgramSupervisedByUserForApproval() throws Exception {

    Program program1 = new Program();
    program1.setId(1);
    Program program2 = new Program();
    program2.setId(2);
    List<Program> programs = new ArrayList<>();
    programs.add(program1);
    programs.add(program2);

    when(programService.getActiveProgramsForUserWithRights(USER_ID, APPROVE_REQUISITION)).thenReturn(programs);
    final List<Facility> facilityList1 = new ArrayList<>();
    final List<Facility> facilityList2 = new ArrayList<>();

    List<Facility> facilities = new ArrayList<Facility>() {{
      addAll(facilityList1);
      addAll(facilityList2);
    }};

    when(facilityService.getUserSupervisedFacilities(USER_ID, program1.getId(), APPROVE_REQUISITION)).thenReturn(facilityList1);
    when(facilityService.getUserSupervisedFacilities(USER_ID, program2.getId(), APPROVE_REQUISITION)).thenReturn(facilityList2);
    List<Rnr> expectedRequisitions = new ArrayList<>();
    when(rnrRepository.getSubmittedRequisitionsForFacilitiesAndPrograms(facilities, programs)).thenReturn(expectedRequisitions);

    List<Rnr> resultRequisitions = rnrService.fetchUserSupervisedRnrForApproval(USER_ID);


    assertThat(resultRequisitions, is(expectedRequisitions));
    verify(rnrRepository).getSubmittedRequisitionsForFacilitiesAndPrograms(facilities, programs);
    verify(programService).getActiveProgramsForUserWithRights(USER_ID, APPROVE_REQUISITION);
    verify(facilityService).getUserSupervisedFacilities(USER_ID, program1.getId(), APPROVE_REQUISITION);
    verify(facilityService).getUserSupervisedFacilities(USER_ID, program2.getId(), APPROVE_REQUISITION);
  }


}
