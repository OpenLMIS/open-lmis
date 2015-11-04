package org.openlmis.restapi.controller;

import org.apache.ibatis.session.RowBounds;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class LookupControllerTest {

  Principal principal;
  RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  @Mock
  private ReportLookupService lookupService;
  @InjectMocks
  private LookupController controller;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("1");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldGetProductCategories() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(lookupService.getAllProductCategories()).thenReturn(new ArrayList<ProductCategory>());
    when(RestResponse.response("product-categories", new ArrayList<ProductCategory>())).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.getProductCategories();

    verify(lookupService).getAllProductCategories();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProducts() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("products", new ArrayList<ProductList>())).thenReturn(expectResponse);
    when(lookupService.getFullProductList(rowBounds)).thenReturn(new ArrayList<org.openlmis.core.domain.Product>());

    ResponseEntity<RestResponse> response = controller.getProducts(1,10,false);

    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProductByCode() throws Exception {
    when(lookupService.getProductByCode("123")).thenReturn(new Product());
    ResponseEntity<RestResponse> response = controller.getProductByCode( "123");
    verify(lookupService).getProductByCode("123");
  }


  @Test
  public void shouldGetDosageUnits() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("dosage-units", new ArrayList<DosageUnit>())).thenReturn(expectResponse);
    when(lookupService.getDosageUnits()).thenReturn(new ArrayList<DosageUnit>());

    ResponseEntity<RestResponse> response = controller.getDosageUnits();

    verify(lookupService).getDosageUnits();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilityTypes() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facility-types", new ArrayList<FacilityType>())).thenReturn(expectResponse);
    when(lookupService.getAllFacilityTypes()).thenReturn(new ArrayList<FacilityType>());

    ResponseEntity<RestResponse> response = controller.getFacilityTypes();

    verify(lookupService).getAllFacilityTypes();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilities() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facilities", new ArrayList<Facility>())).thenReturn(expectResponse);

    when(lookupService.getAllFacilities(rowBounds)).thenReturn(new ArrayList<Facility>());

    ResponseEntity<RestResponse> response = controller.getFacilities(1,10, false);

    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilityByCode() throws Exception {
    when(lookupService.getFacilityByCode("123")).thenReturn(new Facility());
    ResponseEntity<RestResponse> response = controller.getFacilityByCode("123");
    verify(lookupService).getFacilityByCode("123");
  }


  @Test
  public void shouldGetPrograms() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("programs", new ArrayList<Program>())).thenReturn(expectResponse);
    when(lookupService.getAllPrograms()).thenReturn(new ArrayList<Program>());

    ResponseEntity<RestResponse> response = controller.getPrograms();

    verify(lookupService).getAllPrograms();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProgramProducts() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("program-products", new ArrayList<ProgramProduct>())).thenReturn(expectResponse);
    when(lookupService.getAllProgramProducts()).thenReturn(new ArrayList<ProgramProduct>());

    ResponseEntity<RestResponse> response = controller.getProgramProducts();

    verify(lookupService).getAllProgramProducts();
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldGetFacilityApprovedProducts() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facility-approved-products", new ArrayList<FacilityTypeApprovedProduct>())).thenReturn(expectResponse);
    when(lookupService.getAllFacilityTypeApprovedProducts()).thenReturn(new ArrayList<FacilityTypeApprovedProduct>());

    ResponseEntity<RestResponse> response = controller.getFacilityApprovedProducts();

    verify(lookupService).getAllFacilityTypeApprovedProducts();
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldGetProgramByCode() throws Exception {

    when(lookupService.getProgramByCode("123")).thenReturn(new Program());
    ResponseEntity<RestResponse> response = controller.getProgramByCode( "123");
    verify(lookupService).getProgramByCode("123");
  }


  @Test
  public void shouldGetLossesAdjustmentsTypes() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("losses-adjustments-types", new ArrayList<AdjustmentType>())).thenReturn(expectResponse);
    when(lookupService.getAllAdjustmentTypes()).thenReturn(new ArrayList<AdjustmentType>());

    ResponseEntity<RestResponse> response = controller.getLossesAdjustmentsTypes();

    verify(lookupService).getAllAdjustmentTypes();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProcessingPeriods() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("processing-periods", new ArrayList<ProcessingPeriod>())).thenReturn(expectResponse);
    when(lookupService.getAllProcessingPeriods()).thenReturn(new ArrayList<ProcessingPeriod>());

    ResponseEntity<RestResponse> response = controller.getProcessingPeriods();

    verify(lookupService).getAllProcessingPeriods();
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldGetRegimes() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("regimens", new ArrayList<Regimen>())).thenReturn(expectResponse);
    when(lookupService.getAllRegimens()).thenReturn(new ArrayList<Regimen>());

    ResponseEntity<RestResponse> response = controller.getRegimens();

    verify(lookupService).getAllRegimens();
    assertThat(response, is(expectResponse));
  }


} 
