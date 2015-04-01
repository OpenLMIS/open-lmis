package org.openlmis.web.controller.vaccine;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.openlmis.vaccine.dto.ProgramColumnTemplateDTO;
import org.openlmis.vaccine.service.VaccineColumnTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineColumnTemplateControllerTest {

  @Mock
  VaccineColumnTemplateService service;

  @InjectMocks
  VaccineColumnTemplateController controller;

  @Test
  public void shouldGet() throws Exception {
    List<LogisticsColumn> columns = asList(new LogisticsColumn());
    when(service.getTemplate(1L)).thenReturn(columns);
    ResponseEntity<OpenLmisResponse> response = controller.get(1L);
    assertThat(columns, is(response.getBody().getData().get("columns")));
  }

  @Test
  public void shouldSave() throws Exception {
    List<LogisticsColumn> columns = asList(new LogisticsColumn());
    columns.get(0).setProgramId(1L);

    when(service.getTemplate(1L)).thenReturn(columns);
    doNothing().when(service).saveChanges(columns);

    ProgramColumnTemplateDTO dto = new ProgramColumnTemplateDTO();
    dto.setColumns(columns);

    ResponseEntity<OpenLmisResponse> response = controller.save(dto);
    assertThat(columns, is(response.getBody().getData().get("columns")));
  }
}