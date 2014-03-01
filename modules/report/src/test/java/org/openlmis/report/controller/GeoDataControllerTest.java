/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.controller;

import groovy.lang.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.mapper.lookup.GeographicZoneReportMapper;
import org.openlmis.report.model.GeoReportData;
import org.openlmis.report.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class GeoDataControllerTest {

  @Before
  public void setup(){

  }

  @Mock
  private GeographicZoneReportMapper mapper;

  @InjectMocks
  private GeoDataController controller;


  @Test
  public void shouldGetReportingRateReport() throws Exception {
    List<GeoReportData> reportData = new ArrayList<GeoReportData>();
    reportData.add(new GeoReportData());
    when(mapper.getGeoReportingRate(1L, 1L)).thenReturn(reportData);

    ResponseEntity<OpenLmisResponse> actual = controller.getReportingRateReport(1L, 1L);

    verify(mapper).getGeoReportingRate(1L, 1L);
    assertEquals(actual.getBody().getData().get("map"), is( reportData ));

  }
}
