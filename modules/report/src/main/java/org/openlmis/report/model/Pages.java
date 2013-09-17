/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.report.model.ReportData;


import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 3/22/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
//@NoArgsConstructor
//@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pages {

    public List<? extends ReportData> rows;
    public int page;
    public int max;
    public int total;

    public Pages(int page,int max,List<? extends ReportData> rows) {
        this.rows = rows;
        this.page = page;
        this.max = max;
        this.total = rows.size();
    }
}