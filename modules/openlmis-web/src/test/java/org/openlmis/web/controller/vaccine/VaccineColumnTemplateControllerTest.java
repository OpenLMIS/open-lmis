package org.openlmis.web.controller.vaccine;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.openlmis.vaccine.dto.ProgramColumnTemplateDTO;
import org.openlmis.vaccine.service.VaccineColumnTemplateService;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineColumnTemplateControllerTest {

  @Mock
  VaccineColumnTemplateService service;

  @InjectMocks
  VaccineColumnTemplateController controller;

  @Test
  public void shouldGet() throws Exception {
    ProgramColumnTemplateDTO dto = new ProgramColumnTemplateDTO();
    List<LogisticsColumn> columns = asList(new LogisticsColumn());
    dto.setColumns(columns);
    when(service.getTemplate(1L)).thenReturn(dto);
    ResponseEntity<OpenLmisResponse> response = controller.get(1L);
    assertThat(dto, is(response.getBody().getData().get("columns")));
  }

  @Test
  public void shouldSave() throws Exception {

    List<LogisticsColumn> columns = asList(new LogisticsColumn());
    ProgramColumnTemplateDTO dto = new ProgramColumnTemplateDTO();
    dto.setColumns(columns);
    dto.setProgramId(1L);
    when(service.getTemplate(1L)).thenReturn(dto);
    doNothing().when(service).saveChanges(columns);

    ResponseEntity<OpenLmisResponse> response = controller.save(dto);
    assertThat(dto, is(response.getBody().getData().get("columns")));
  }
}