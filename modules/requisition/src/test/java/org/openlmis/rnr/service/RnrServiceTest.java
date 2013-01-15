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
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.rnr.builder.RnrBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
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
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.rnr.builder.RnrBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RnrBuilder.status;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.openlmis.rnr.service.RnrService.*;

@RunWith(MockitoJUnitRunner.class)
public class RnrServiceTest {

  public static final Integer HIV = 1;
  public static final int USER_ID = 1;
  public Integer facilityId = 1;

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

  private Rnr rnr;
  private Rnr submittedRnr;
  private Rnr initiatedRnr;


  @Before
  public void setup() {
    rnrService = new RnrService(rnrRepository, rnrTemplateRepository, facilityApprovedProductService, supervisoryNodeService, roleRightService);
    rnr = spy(make(a(defaultRnr)));
    submittedRnr = make(a(RnrBuilder.defaultRnr, with(status, RnrStatus.SUBMITTED)));
    initiatedRnr = make(a(RnrBuilder.defaultRnr, with(status, INITIATED)));
  }

  @Test
  public void shouldInitRequisition() {
    when(rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(new Rnr());
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(true);
    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(ProductBuilder.defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getByFacilityAndProgram(facilityId, HIV)).thenReturn(facilityApprovedProducts);
    Rnr rnr = rnrService.initRnr(facilityId, HIV, 1);
    verify(facilityApprovedProductService).getByFacilityAndProgram(facilityId, HIV);
    verify(rnrRepository).insert(rnr);
    assertThat(rnr.getLineItems().size(), is(USER_ID));
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(false);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Please contact Admin to define R&R template for this program");
    Rnr rnr = rnrService.initRnr(facilityId, HIV, 1);
    verify(facilityApprovedProductService, never()).getByFacilityAndProgram(facilityId, HIV);
    verify(rnrRepository, never()).insert(rnr);
  }

  @Test
  public void shouldReturnMessageWhileSubmittingRnrIfSupervisingNodeNotPresent() {
    when(rnrRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(null);

    String message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);
    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message, is("There is no supervisory node to process the R&R further, Please contact the Administrator"));
  }

  @Test
  public void shouldSubmitValidRnrAndSetMessage() {
    when(rnrRepository.getById(rnr.getId())).thenReturn(initiatedRnr);
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(new SupervisoryNode());
    String message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);

    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message, is("R&R submitted successfully!"));
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
}
