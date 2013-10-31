/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

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
        //reportManager.showReport();
    }


}
