package org.openlmis.report;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.report.exporter.ReportExporter;
import org.openlmis.report.service.FacilityReportDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**

 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:test-applicationContext-report.xml")
public class ReportManagerTest {

    private static String  REPORT_ID = "RP_ID";
    private static String REPORT_NAME = "RP_NAME";
    private static String REPORT_TITLE = "RP_TITLE";
    private static String REPORT_VERSION = "RP_VERSION";

    ReportManager reportManager;
    List<Report> reportList;

    @Mock
    ReportExporter reportExporter;
    @Mock
    FacilityReportDataProvider facilityReportDataProvider;

    @Mock
    ReportAccessAuthorizer reportAccessAuthorizer;

    @Before
    public void setUp() throws Exception {
        reportList = new ArrayList<>();

        Report facilityReport = new Report();
        facilityReport.setReportKey("facilities");
        facilityReport.setTemplate("facility-report.jasper");
        facilityReport.setReportDataProvider(facilityReportDataProvider);
        facilityReport.setId(REPORT_ID);
        facilityReport.setTitle(REPORT_TITLE);
        facilityReport.setName(REPORT_NAME);
        facilityReport.setVersion(REPORT_VERSION);

        reportList.add(facilityReport);

        reportManager = new ReportManager(reportAccessAuthorizer, reportExporter, reportList);
    }

    @Test
    public void shouldPrintFacilityReport(){
        reportManager.showReport();
    }


}
