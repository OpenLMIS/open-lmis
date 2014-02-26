package org.openlmis.restapi.controller; 

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestResponse.class)
@Category(UnitTests.class)
public class LookupControllerTest { 

  @Mock
  private ReportLookupService lookupService;

  @InjectMocks
  private LookupController controller;

  Principal principal;

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

    ResponseEntity<RestResponse> response = controller.getProductCategories(principal);

    verify(lookupService).getAllProductCategories();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProducts() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("products", new ArrayList<ProductList>())).thenReturn(expectResponse);
    when(lookupService.getFullProductList()).thenReturn(new ArrayList<ProductList>());

    ResponseEntity<RestResponse> response = controller.getProducts(principal);

    verify(lookupService).getFullProductList();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProductByCode() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("product", new Product())).thenReturn(expectResponse);
    when(lookupService.getProductByCode("123")).thenReturn(new Product());

    ResponseEntity<RestResponse> response = controller.getProductByCode(principal, "123");

    verify(lookupService).getProductByCode("123");
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetDosageUnits() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("dosage-units", new ArrayList<DosageUnit>())).thenReturn(expectResponse);
    when(lookupService.getDosageUnits()).thenReturn(new ArrayList<DosageUnit>());

    ResponseEntity<RestResponse> response = controller.getDosageUnits(principal);

    verify(lookupService).getDosageUnits();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilityTypes() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facility-types", new ArrayList<FacilityType>())).thenReturn(expectResponse);
    when(lookupService.getFacilityTypes()).thenReturn(new ArrayList<FacilityType>());

    ResponseEntity<RestResponse> response = controller.getFacilityTypes(principal);

    verify(lookupService).getFacilityTypes();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilities() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facilities", new ArrayList<Facility>())).thenReturn(expectResponse);
    when(lookupService.getAllFacilities()).thenReturn(new ArrayList<Facility>());

    ResponseEntity<RestResponse> response = controller.getFacilities(principal);

    verify(lookupService).getAllFacilities();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetFacilityByCode() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("facility", new Facility())).thenReturn(expectResponse);
    when(lookupService.getFacilityByCode("123")).thenReturn(new Facility());

    ResponseEntity<RestResponse> response = controller.getFacilityByCode(principal, "123");

    verify(lookupService).getFacilityByCode("123");
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetPrograms() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("programs", new ArrayList<Program>())).thenReturn(expectResponse);
    when(lookupService.getAllPrograms()).thenReturn(new ArrayList<Program>());

    ResponseEntity<RestResponse> response = controller.getPrograms(principal);

    verify(lookupService).getAllPrograms();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProgramByCode() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("program", new Program())).thenReturn(expectResponse);
    when(lookupService.getProgramByCode("123")).thenReturn(new Program());

    ResponseEntity<RestResponse> response = controller.getProgramByCode(principal, "123");

    verify(lookupService).getProgramByCode("123");
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetLossesAdjustmentsTypes() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("losses-adjustments-types", new ArrayList<AdjustmentType>())).thenReturn(expectResponse);
    when(lookupService.getAllAdjustmentTypes()).thenReturn(new ArrayList<AdjustmentType>());

    ResponseEntity<RestResponse> response = controller.getLossesAdjustmentsTypes(principal);

    verify(lookupService).getAllAdjustmentTypes();
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldGetProcessingPeriods() throws Exception {
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.response("processing-periods", new ArrayList<ProcessingPeriod>())).thenReturn(expectResponse);
    when(lookupService.getAllProcessingPeriods()).thenReturn(new ArrayList<ProcessingPeriod>());

    ResponseEntity<RestResponse> response = controller.getProcessingPeriods(principal);

    verify(lookupService).getAllProcessingPeriods();
    assertThat(response, is(expectResponse));
  }


} 
