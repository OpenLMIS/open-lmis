package org.openlmis.web.controller.vaccine.demographic;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.service.demographics.DemographicEstimateCategoryService;
import org.openlmis.web.controller.vaccine.demographic.DemographicEstimateCategoryController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DemographicEstimateCategoryControllerTest {

  @Mock
  DemographicEstimateCategoryService service;

  @InjectMocks
  DemographicEstimateCategoryController controller;

  @Test
  public void shouldGetAll() throws Exception {
    List<DemographicEstimateCategory> categories = new ArrayList<>();
    when(service.getAll()).thenReturn(categories);

    ResponseEntity<OpenLmisResponse> result  = controller.getAll();

    assertThat(categories, is(result.getBody().getData().get("estimate_categories")));
  }

  @Test
  public void shouldGetById() throws Exception {
    DemographicEstimateCategory category = new DemographicEstimateCategory();
    when(service.getById(2L)).thenReturn(category);

    ResponseEntity<OpenLmisResponse> result = controller.getById(2L);

    assertThat(category, is(result.getBody().getData().get("estimate_category")));
  }

  @Test
  public void shouldSave() throws Exception {
    doNothing().when(service).save(anyList());
    DemographicEstimateCategory category = new DemographicEstimateCategory();

    ResponseEntity<OpenLmisResponse> result = controller.save(category);

    assertThat(category, is(result.getBody().getData().get("estimate_category")));
    verify(service).save(anyList());
  }
}