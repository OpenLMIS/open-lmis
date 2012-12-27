package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityApprovedProductService;
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

@RunWith(MockitoJUnitRunner.class)
public class RnrServiceTest {

  public static final Integer HIV = 1;
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

  @Before
  public void setup() {
    rnrService = new RnrService(rnrRepository, rnrTemplateRepository, facilityApprovedProductService);
  }

  @Test
  public void shouldInitRequisition() {
    when(rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(new Rnr());
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(true);
    List<FacilityApprovedProduct> facilityApprovedProducts = new ArrayList<>();
    ProgramProduct programProduct = new ProgramProduct(null, make(a(ProductBuilder.defaultProduct)), 10, true);
    facilityApprovedProducts.add(new FacilityApprovedProduct("warehouse", programProduct, 30));
    when(facilityApprovedProductService.getByFacilityAndProgram(facilityId, HIV)).thenReturn(facilityApprovedProducts);
    Rnr rnr = rnrService.initRnr(facilityId, HIV, "user");
    verify(facilityApprovedProductService).getByFacilityAndProgram(facilityId, HIV);
    verify(rnrRepository).insert(rnr);
    assertThat(rnr.getLineItems().size(), is(1));
  }

  @Test
  public void shouldNotInitRequisitionIfTemplateNotDefined() {
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(false);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Please contact Admin to define R&R template for this program");
    Rnr rnr = rnrService.initRnr(facilityId, HIV, "user");
    verify(facilityApprovedProductService, never()).getByFacilityAndProgram(facilityId, HIV);
    verify(rnrRepository, never()).insert(rnr);
  }

  @Test @Ignore
  public void shouldReturnExistingRnrIfAlreadyInitiated() {
    Rnr initiatedRnr = new Rnr();
    initiatedRnr.setId(1);
    when(rnrTemplateRepository.isRnrTemplateDefined(HIV)).thenReturn(true);
    when(rnrRepository.getRequisitionByFacilityAndProgram(facilityId, HIV)).thenReturn(initiatedRnr);
    Rnr rnr = rnrService.initRnr(facilityId, HIV, "user");
    verify(facilityApprovedProductService).getByFacilityAndProgram(facilityId, HIV);
    verify(rnrRepository).insert(rnr);
    assertThat(rnr, is(initiatedRnr));
  }
}
