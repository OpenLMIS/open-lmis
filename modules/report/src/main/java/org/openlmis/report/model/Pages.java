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

    public List<ReportData> rows;
    public int page;
    public int max;
    public int total;

    public Pages(int page,int total,int max,List<ReportData> rows) {
        this.rows = rows;
        this.page = page;
        this.max = max;
        this.total = total;
    }
}