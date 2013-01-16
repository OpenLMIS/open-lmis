package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityApprovedProductRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityApprovedProductBuilder.*;


@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductServiceTest {

  @Mock
  private FacilityApprovedProductRepository facilityApprovedProductRepository;

  @Mock
  private ProgramService programService;

  @Mock
  private ProductService productService;

  @Rule
  public ExpectedException expectedException = none();

  @Test
  public void shouldSaveFacilityApprovedProduct() throws Exception {

    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    when(programService.getIdForCode(defaultProgramCode)).thenReturn(45);
    when(productService.getIdForCode(defaultProductCode)).thenReturn(10);

    facilityApprovedProductService.save(facilityApprovedProduct);

    verify(programService).getIdForCode(defaultProgramCode);
    verify(productService).getIdForCode(defaultProductCode);
    verify(facilityApprovedProductRepository).insert(facilityApprovedProduct);
    assertThat(facilityApprovedProduct.getProgramProduct().getProgram().getId(), is(45));
    assertThat(facilityApprovedProduct.getProgramProduct().getProduct().getId(), is(10));
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProgramDoesNotExist() throws Exception {
    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(programService).getIdForCode(defaultProgramCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityApprovedProduct);
    verify(programService).getIdForCode(defaultProgramCode);

    verify(facilityApprovedProductRepository, never()).insert(facilityApprovedProduct);
  }

  @Test
  public void shouldNotSaveFacilityApprovedProductAndThrowAnExceptionWhenProductDoesNotExist() throws Exception {
    FacilityApprovedProductService facilityApprovedProductService = new FacilityApprovedProductService(facilityApprovedProductRepository, programService, productService);
    FacilityApprovedProduct facilityApprovedProduct = make(a(defaultFacilityApprovedProduct));

    doThrow(new DataException("abc")).when(productService).getIdForCode(defaultProductCode);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("abc");

    facilityApprovedProductService.save(facilityApprovedProduct);
    verify(productService).getIdForCode(defaultProgramCode);

    verify(facilityApprovedProductRepository, never()).insert(facilityApprovedProduct);
  }
}
