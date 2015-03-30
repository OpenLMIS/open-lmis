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
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.SeasonalRationingReport;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.SeasonalRationingReportDataProvider;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

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

        when(dataProvider.getMainReportData(requestParam, requestParam, pageSize, maxSize)).thenAnswer(createAnswer(expectedSeasonalityReportData));
        when(reportManager.getReportByKey("seasonality_rationing")).thenReturn(expectedReport);

        Pages reportPages = controller.getSeasonalityRationingAdjustmentReport(pageSize, maxSize, httpServletRequest);

        verify(dataProvider).getMainReportData(requestParam, requestParam, pageSize, maxSize);
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
