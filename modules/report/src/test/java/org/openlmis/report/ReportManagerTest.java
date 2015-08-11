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
