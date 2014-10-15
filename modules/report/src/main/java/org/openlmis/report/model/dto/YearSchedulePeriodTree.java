package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearSchedulePeriodTree {

    private int groupid;
    private int periodid;
    private String year;
    private String groupname;
    private String periodname;

    private List<YearSchedulePeriodTree> children = new ArrayList<YearSchedulePeriodTree>();

}

