package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.filter.FacilityReportFilter;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.model.sorter.FacilityReportSorter;
import org.openlmis.report.service.ProductReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 4/18/13
 * Time: 7:38 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    AnnotationMethodHandlerAdapter adapter = new AnnotationMethodHandlerAdapter();


   /* public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }*/

    //public static final String USER_ID = "USER_ID";
    @Mock
    ReportManager reportManager;

    @Mock
    ProductReportService productReportService;

    ReportController reportController;

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
        String reportKey = "facilities";
        Report report = reportManager.getReportByKey(reportKey);
        int perPageSize = 20;
        int pageNum = 1;
        int total = 40;

        List<FacilityReport> facilityReportList = new ArrayList<FacilityReport>();
        Pages pagesOfacilities = new Pages(pageNum,total,perPageSize,facilityReportList);
        Map<String, String[]> filter = new HashMap<String, String[]>();
        Map<String, String[]> sorter = new HashMap<String, String[]>();

        //when(report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(filter, sorter, pageNum, perPageSize)).thenReturn(pagesOfacilities);

        //reportController = new ReportController();

        //request.setMethod("GET");
        //request.setRequestURI("/reportdata/facilitylist"); //http://localhost:9091/public/pages/reports/facilitylist/index.html
        //request.setAttribute();

        //       adapter.handle(request, response, reportController);
        //HttpServletRequest request = new MockHttpServletRequest();
        //HttpServletResponse response = new HttpServletResponseWrapper();


       //  response =
        //        reportController.getFacilityLists(pageNum, perPageSize, request);
        //verify(report.getReportDataProvider()).getReportDataByFilterCriteriaAndPagingAndSorting(filter,sorter,pageNum,perPageSize);
        //assertThat((ArrayList<FacilityApprovedProduct>) openLmisResponse.getBody().getData().get(FacilityApprovedProductController.NON_FULL_SUPPLY_PRODUCTS), is(nonFullSupplyProducts));
    }

    @Test
    public void testGetFacilityListsWtihLables() throws Exception {

    }

}
