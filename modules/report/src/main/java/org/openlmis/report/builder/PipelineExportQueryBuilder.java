/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.builder;
import org.openlmis.report.model.params.DistrictConsumptionReportParam;
import org.openlmis.report.model.params.LabEquipmentListReportParam;
import org.openlmis.report.model.params.PipelineExportParams;
import org.openlmis.report.model.params.RnRFeedbackReportParam;
import org.openlmis.report.model.report.PipelineExportReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class PipelineExportQueryBuilder {

    public static String getQuery(Map params){

     PipelineExportParams filter  = (PipelineExportParams)params.get("filterCriteria");


     int programId  = filter.getProgramId();
     int yearId     = filter.getYearId();
     int periodId   = filter.getPeriodId();


     BEGIN();
     SELECT("line");
     FROM("fn_e2e_pipeline("+programId+","+yearId+","+periodId+")");
     // copy the sql over to a variable, this makes the debugging much more possible.
      String sql = SQL();
        return sql;

    }
}
