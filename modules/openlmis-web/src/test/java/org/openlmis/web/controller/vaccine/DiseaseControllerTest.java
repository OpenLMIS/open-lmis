/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.vaccine;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineDisease;
import org.openlmis.vaccine.service.DiseaseService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class DiseaseControllerTest {

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Mock
  DiseaseService service;

  @Mock
  private MessageService messageService;

  @InjectMocks
  DiseaseController controller;


  @Test
  public void shouldGetOneDisease() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Polio");
    when(service.getById(1L)).thenReturn(disease);

    ResponseEntity<OpenLmisResponse> response = controller.get(1L);
    assertThat(disease, is(response.getBody().getData().get("disease")));
  }

  @Test
  public void shouldGetAll() throws Exception {
    VaccineDisease disease = new VaccineDisease();
    disease.setName("Polio");
    List<VaccineDisease> list = asList(disease);
    when(service.getAll()).thenReturn(list);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(list, is(response.getBody().getData().get("diseases")));

  }

  @Test
  public void shouldSaveNewRecords() throws Exception {

    VaccineDisease disease = new VaccineDisease();
    disease.setName("Polio");
    disease.setId(1L);
    when(service.getById(1L)).thenReturn(disease);
    doNothing().when(service).save(disease);

    ResponseEntity<OpenLmisResponse> response = controller.save(disease);
    assertThat(disease, is(response.getBody().getData().get("disease")));
  }
}