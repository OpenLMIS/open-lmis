/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.exporter;

import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.ReportData;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 *  Defines API for exporting reports.
 *  Implementation can be done for different Java reporting frameworks.(Jasper, BIRT)
 */
public interface ReportExporter {

    /**
     *
     * @param reportInputStream -   <b>The report being exported</b>
     * @param reportExtraParams  -  <b>Extra report parameters that can be passed to the report to fill report header and footer details</b>
     * @param reportData   - <b>DataSource used to fill the report</b>
     * @param outputOption  -   <b>Report out put option </b>
     * @param response - <b>HttpServletResponse for writing the report to OutputStream</b>
     */
    public void exportReport(InputStream reportInputStream,  HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption, HttpServletResponse response);

    /**
     * This method return the exported report byte stream to save the output as a file or any operation
     * @param reportInputStream
     * @param reportExtraParams
     * @param reportData
     * @param outputOption
     * @return
     */
    public ByteArrayOutputStream exportReportBytesStream(InputStream reportInputStream, HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption);

}