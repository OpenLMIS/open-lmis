package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RnrRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RnrBuilder.defaultRnr;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.openlmis.rnr.service.RnrService.RNR_AUTHORIZED_SUCCESSFULLY;
import static org.openlmis.rnr.service.RnrService.RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR;

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

  private Rnr rnr;



  @Before
  public void setup() {
    rnrService = new RnrService(rnrRepository, rnrTemplateRepository, facilityApprovedProductService, supervisoryNodeService);
    rnr = spy(make(a(defaultRnr)));
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
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(null);

    String message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);
    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message, is("There is no supervisory node to process the R&R further, Please contact the Administrator"));
  }

  @Test
  public void shouldSubmitValidRnrAndSetMessage() {
    doReturn(true).when(rnr).validate(false);
    when(supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId())).thenReturn(new SupervisoryNode());
    String message = rnrService.submit(rnr);
    verify(rnrRepository).update(rnr);

    assertThat(rnr.getStatus(), is(SUBMITTED));
    assertThat(message, is("R&R submitted successfully!"));
  }

  @Test
  public void shouldAuthorizeAValidRnr() throws Exception {
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
    when(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId())).thenReturn(true);
    doThrow(new DataException("error-message")).when(rnr).validate(true);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error-message");
    rnrService.authorize(rnr);
  }
}
