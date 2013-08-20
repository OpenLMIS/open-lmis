/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report;

import lombok.Data;
import org.openlmis.report.service.ReportDataProvider;
import org.openlmis.report.model.ReportData;

/**
 */
@Data
public class Report {

    public String reportKey;
    public String template;
    public ReportDataProvider reportDataProvider;
    public ReportData filterOption;
    /**
     *  report properties used by report design
     */
    public String title;
    public String subTitle;
    public String name;
    public String id;
    public String version;
    /*                       */


}
