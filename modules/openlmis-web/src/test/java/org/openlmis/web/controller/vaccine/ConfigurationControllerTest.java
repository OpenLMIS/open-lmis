/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.vaccine;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.config.VaccineIvdTabVisibility;
import org.openlmis.vaccine.dto.VaccineServiceConfigDTO;
import org.openlmis.vaccine.service.VaccineIvdTabVisibilityService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationControllerTest {

  @Mock
  VaccineIvdTabVisibilityService service;

  @InjectMocks
  ConfigurationController controller;

  @Test
  public void shouldGetProgramTabVisibility() throws Exception {
    List<VaccineIvdTabVisibility> list = asList(new VaccineIvdTabVisibility());
    when(service.getVisibilityForProgram(2L)).thenReturn( list );

    ResponseEntity<OpenLmisResponse> response = controller.getProgramTabVisibility(2L);

    verify(service).getVisibilityForProgram(2L);
    assertThat(response.getBody().getData().get("visibilities"), is(notNullValue()));
  }

  @Test
  public void shouldSave() throws Exception {
    List<VaccineIvdTabVisibility> list = asList(new VaccineIvdTabVisibility());
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    dto.setTabVisibilitySettings(list);
    dto.setProgramId(2L);
    ResponseEntity<OpenLmisResponse> response = controller.save( dto);

    verify(service).save(list, dto.getProgramId());
    assertThat(response.getBody().getData().get("visibilities"), is(notNullValue()));
  }
}