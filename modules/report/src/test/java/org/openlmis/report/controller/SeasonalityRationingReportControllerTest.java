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
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.SeasonalRationingReport;
import org.openlmis.report.service.SeasonalRationingReportDataProvider;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
@PrepareForTest(OpenLmisResponse.class)
public class SeasonalityRationingReportControllerTest {

    public static final Long userId = 1L;

    @InjectMocks
    InteractiveReportController controller;

    @Mock
    SeasonalRationingReportDataProvider dataProvider;

    @Mock
    ReportManager reportManager;

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();


    @Before
    public void setup(){
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        mockHttpSession.setAttribute(USER_ID, userId);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnSeasonalityRationingReportDataForSelectedParameters() throws Exception{

        Map<String, String[]> requestParam = new HashMap<String, String[]>();
        int pageSize = 1;
        int maxSize = 1;

        List<ReportData> expectedSeasonalityReportData = new ArrayList<>(1);
        expectedSeasonalityReportData.add(  new SeasonalRationingReport());

        Pages expectedPage = new Pages(pageSize, maxSize, expectedSeasonalityReportData);

        Report expectedReport = new Report();
        expectedReport.setReportDataProvider(dataProvider);

        when(dataProvider.getReportBody(requestParam, requestParam, pageSize, maxSize)).thenAnswer(createAnswer(expectedSeasonalityReportData));
        when(reportManager.getReportByKey("seasonality_rationing")).thenReturn(expectedReport);

        Pages reportPages = controller.getSeasonalityRationingAdjustmentReport(pageSize, maxSize, httpServletRequest);

        verify(dataProvider).getReportBody(requestParam, requestParam, pageSize, maxSize);
        verify(reportManager).getReportByKey("seasonality_rationing");

        assertThat(reportPages, is(expectedPage));
    }

    public static <T> Answer<T> createAnswer(final T value) {
        Answer<T> dummy = new Answer<T>() {
            @Override
            public T answer(InvocationOnMock invocation) throws Throwable {
                return value;
            }
        };
        return dummy;
    }

}
