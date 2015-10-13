package org.openlmis.web.controller.demographic;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.service.EstimateCategoryService;
import org.openlmis.web.controller.demographics.EstimateCategoryController;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EstimateCategoryControllerTest {

    @Mock
    EstimateCategoryService service;

    @InjectMocks
    EstimateCategoryController controller;

    @Test
    public void shouldGetAll() throws Exception {
        List<EstimateCategory> categories = new ArrayList<>();
        when(service.getAll()).thenReturn(categories);

        ResponseEntity<OpenLmisResponse> result = controller.getAll();

        assertThat(categories, is(result.getBody().getData().get("estimate_categories")));
    }

    @Test
    public void shouldGetById() throws Exception {
        EstimateCategory category = new EstimateCategory();
        when(service.getById(2L)).thenReturn(category);

        ResponseEntity<OpenLmisResponse> result = controller.getById(2L);

        assertThat(category, is(result.getBody().getData().get("estimate_category")));
    }

    @Test
    public void shouldSave() throws Exception {
        doNothing().when(service).save(anyList());
        EstimateCategory category = new EstimateCategory();

        ResponseEntity<OpenLmisResponse> result = controller.save(category);

        assertThat(category, is(result.getBody().getData().get("estimate_category")));
        verify(service).save(anyList());
    }
}