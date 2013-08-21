/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.report;

import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 5/2/13
 * Time: 3:05 PM
 */
public class MasterReport implements ReportData {

    public List<? extends ReportData> details;

    public List<? extends ReportData> summary;

}
