package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.service.FacilityReportDataProvider;
import org.openlmis.report.service.ProductReportService;
import org.openlmis.report.service.ReportDataProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/18/13
 * Time: 7:38 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    public static final Integer userId = 1;
   // private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    AnnotationMethodHandlerAdapter adapter = new AnnotationMethodHandlerAdapter();
    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Mock
    private ReportManager reportManager;

    @Mock
    private ProductReportService productReportService;

    @InjectMocks
    private ReportController reportController;

    @Before
    public void setUp() throws Exception {

        productReportService = mock(ProductReportService.class);
        reportManager = mock(ReportManager.class);
        reportController = new ReportController(reportManager, productReportService);
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER, USER);
        mockHttpSession.setAttribute(USER_ID, userId);

    }

   /* public void tearDown() throws Exception {

    }*/


    @Test
    public void testGetProducts() throws Exception {

    }

    @Test
    public void testShowReport() throws Exception {

    }

    @Test
    public void testShowMailingListReport() throws Exception {

    }

    @Test
    public void testGetFacilityLists() throws Exception {
        int perPageSize = 20;
        int pageNum = 1;
        int total = 40;

        FacilityReport facilityreport = new FacilityReport("code","name","type",true);//mock(FacilityReport.class);
        List<FacilityReport> facilityReportList = new ArrayList<FacilityReport>();
        facilityReportList.add(0,facilityreport);
        Pages pagesOfacilities = new Pages(pageNum,total,perPageSize,facilityReportList);
        //Pages pagesOfacilities = mock(Pages.class);
        Map<String, String[]> filter = new HashMap<String, String[]>();
        Map<String, String[]> sorter = new HashMap<String, String[]>();
        String reportKey = "facilities";

        Report report = mock(Report.class);
        when(reportManager.getReportByKey(reportKey)).thenReturn(report);
        //Report reportByKey = reportManager.getReportByKey(reportKey);

        FacilityReportDataProvider facilityReportDataProvider = mock(FacilityReportDataProvider.class);
        when(report.getReportDataProvider()).thenReturn(facilityReportDataProvider);

        when(facilityReportDataProvider.getReportDataByFilterCriteriaAndPagingAndSorting(null, null, pageNum, perPageSize)).thenReturn(null);//.thenReturn(facilityReportList);

        Pages actualResult = reportController.getFacilityLists(pageNum, perPageSize, httpServletRequest);
        verify(report.getReportDataProvider()).getReportDataByFilterCriteriaAndPagingAndSorting(filter,sorter,pageNum,perPageSize);
       // assertTrue(actualResult.rows.contains(facilityreport));

    }

    @Test
    public void testGetFacilityListsWtihLables() throws Exception {

    }

}
