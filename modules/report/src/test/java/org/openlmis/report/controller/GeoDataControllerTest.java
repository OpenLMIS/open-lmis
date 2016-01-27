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

package org.openlmis.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.GeoZoneReportingRate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class GeoDataControllerTest {

    public static final Long userId = 1L;

  @Mock
  private GeographicZoneReportMapper mapper;

  @InjectMocks
  private GeoDataController controller;
    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
  @Before
  public void setup(){
      MockHttpSession mockHttpSession = new MockHttpSession();
      httpServletRequest.setSession(mockHttpSession);
      mockHttpSession.setAttribute(USER, USER);
      mockHttpSession.setAttribute(USER_ID, userId);
    initMocks(this);
  }

  @Test
  public void shouldGetReportingRateReport() throws Exception {
    List<GeoZoneReportingRate> reportData = new ArrayList<GeoZoneReportingRate>();
    reportData.add(new GeoZoneReportingRate());
    when(mapper.getGeoReportingRate(1l,1L,1L, 1L)).thenReturn(reportData);

    OpenLmisResponse response = new OpenLmisResponse();
    response.addData("map", reportData);
    ResponseEntity<OpenLmisResponse> expectResponse = new ResponseEntity<>(response, HttpStatus.OK);

    ResponseEntity<OpenLmisResponse> actual = controller.getReportingRateReport(1L, 1L,1L,httpServletRequest);

    verify(mapper).getGeoReportingRate(1l,1L,1L, 1L);
  }
}
