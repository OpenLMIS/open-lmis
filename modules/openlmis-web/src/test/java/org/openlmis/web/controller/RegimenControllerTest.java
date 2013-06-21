package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.service.RegimenService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.RegimenController.REGIMENS;
import static org.openlmis.web.controller.RegimenController.REGIMEN_CATEGORIES;

@RunWith(MockitoJUnitRunner.class)
public class RegimenControllerTest {

  @Mock
  RegimenService service;

  @InjectMocks
  RegimenController controller;


  @Test
  public void shouldInsertARegimen() {
    Regimen regimen = new Regimen();
    List<Regimen> regimens = Arrays.asList(regimen);
    controller.save(regimens);
    verify(service).save(regimens);
  }

  @Test
  public void shouldGetRegimenByProgram() {
    List<Regimen> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    when(service.getByProgram(programId)).thenReturn(expectedRegimens);

    ResponseEntity<OpenLmisResponse> response = controller.getByProgram(programId);

    assertThat((List<Regimen>) response.getBody().getData().get(REGIMENS),is(expectedRegimens));
    verify(service).getByProgram(programId);
  }

  @Test
  public void shouldGetAllRegimenCategories() throws Exception {
    List<RegimenCategory> expectedRegimenCategories = new ArrayList<>();
    when(service.getAllRegimenCategories()).thenReturn(expectedRegimenCategories);

    ResponseEntity<OpenLmisResponse> response = controller.getAllRegimenCategories();

    assertThat((List<RegimenCategory>) response.getBody().getData().get(REGIMEN_CATEGORIES),is(expectedRegimenCategories));
    verify(service).getAllRegimenCategories();

  }
}
